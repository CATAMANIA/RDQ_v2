import { useState, useEffect, useCallback, useRef } from 'react';
import { API_BASE_URL } from '../config/constants';

// Types de notification basés sur l'enum backend
export enum NotificationType {
  RDQ_CREATED = 'RDQ_CREATED',
  RDQ_UPDATED = 'RDQ_UPDATED',
  RDQ_ASSIGNED = 'RDQ_ASSIGNED',
  RDQ_STATUS_CHANGED = 'RDQ_STATUS_CHANGED',
  RDQ_DEADLINE_APPROACHING = 'RDQ_DEADLINE_APPROACHING',
  RDQ_OVERDUE = 'RDQ_OVERDUE',
  RDQ_COMMENTED = 'RDQ_COMMENTED',
  RDQ_CANCELLED = 'RDQ_CANCELLED',
  SYSTEM_MAINTENANCE = 'SYSTEM_MAINTENANCE',
  USER_WELCOME = 'USER_WELCOME',
  GENERAL_INFO = 'GENERAL_INFO'
}

// Interface pour les informations RDQ dans les notifications
export interface RdqInfo {
  id: number;
  numero: string;
  titre: string;
  status: string;
  priorite: string;
  dateEcheance?: string;
  responsableNom?: string;
  responsableEmail?: string;
  demandeurNom?: string;
  demandeurEmail?: string;
}

// Interface pour une notification
export interface Notification {
  id: number;
  type: NotificationType;
  title: string;
  message: string;
  read: boolean;
  createdAt: string;
  readAt?: string;
  userId: number;
  rdqId?: number;
  metadata?: Record<string, any>;
  rdqInfo?: RdqInfo;
  critical: boolean;
}

// Interface pour les préférences de notification
export interface NotificationPreference {
  id: number;
  userId: number;
  notificationType: NotificationType;
  enabled: boolean;
  emailEnabled: boolean;
  description: string;
}

// Interface pour la liste paginée de notifications
export interface NotificationListResponse {
  notifications: Notification[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
  unreadCount: number;
  criticalCount: number;
}

// Interface pour les paramètres de recherche
export interface NotificationSearchParams {
  page?: number;
  size?: number;
  type?: NotificationType;
  read?: boolean;
  critical?: boolean;
  rdqId?: number;
  sortBy?: 'createdAt' | 'type' | 'read';
  sortDirection?: 'ASC' | 'DESC';
}

// Interface pour les statistiques de notifications
export interface NotificationStats {
  unreadCount: number;
  criticalCount: number;
  totalCount: number;
  byType: Record<NotificationType, number>;
}

// Hook principal pour la gestion des notifications
export const useNotifications = () => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [stats, setStats] = useState<NotificationStats>({
    unreadCount: 0,
    criticalCount: 0,
    totalCount: 0,
    byType: {} as Record<NotificationType, number>
  });
  
  // Pagination et recherche
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  
  // Référence pour éviter les appels multiples
  const abortController = useRef<AbortController | null>(null);

  // Fonction utilitaire pour les appels API
  const makeApiCall = useCallback(async (
    endpoint: string, 
    options: RequestInit = {}
  ): Promise<Response> => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.headers,
      },
    });

    if (!response.ok) {
      const errorData = await response.text();
      throw new Error(errorData || `HTTP error! status: ${response.status}`);
    }

    return response;
  }, []);

  // Récupérer les notifications avec pagination et filtres
  const fetchNotifications = useCallback(async (
    params: NotificationSearchParams = {}
  ) => {
    try {
      // Annuler la requête précédente si elle existe
      if (abortController.current) {
        abortController.current.abort();
      }
      
      abortController.current = new AbortController();
      setLoading(true);
      setError(null);

      // Construire les paramètres de requête
      const searchParams = new URLSearchParams();
      if (params.page !== undefined) searchParams.append('page', params.page.toString());
      if (params.size !== undefined) searchParams.append('size', params.size.toString());
      if (params.type) searchParams.append('type', params.type);
      if (params.read !== undefined) searchParams.append('read', params.read.toString());
      if (params.critical !== undefined) searchParams.append('critical', params.critical.toString());
      if (params.rdqId) searchParams.append('rdqId', params.rdqId.toString());
      if (params.sortBy) searchParams.append('sortBy', params.sortBy);
      if (params.sortDirection) searchParams.append('sortDirection', params.sortDirection);

      const response = await makeApiCall(
        `/api/notifications?${searchParams}`,
        { signal: abortController.current.signal }
      );

      const data: NotificationListResponse = await response.json();
      
      setNotifications(data.notifications);
      setCurrentPage(data.currentPage);
      setTotalPages(data.totalPages);
      setHasMore(data.hasNext);
      
      // Mettre à jour les statistiques
      setStats(prev => ({
        ...prev,
        unreadCount: data.unreadCount,
        criticalCount: data.criticalCount,
        totalCount: data.totalElements
      }));

    } catch (err: any) {
      if (err.name !== 'AbortError') {
        setError(`Erreur lors du chargement des notifications: ${err.message}`);
        console.error('Erreur fetchNotifications:', err);
      }
    } finally {
      setLoading(false);
      abortController.current = null;
    }
  }, [makeApiCall]);

  // Marquer une notification comme lue
  const markAsRead = useCallback(async (notificationId: number) => {
    try {
      await makeApiCall(`/api/notifications/${notificationId}/read`, {
        method: 'PUT'
      });

      // Mettre à jour l'état local
      setNotifications(prev => prev.map(notif => 
        notif.id === notificationId 
          ? { ...notif, read: true, readAt: new Date().toISOString() }
          : notif
      ));

      // Mettre à jour les statistiques
      setStats(prev => ({
        ...prev,
        unreadCount: Math.max(0, prev.unreadCount - 1)
      }));

    } catch (err: any) {
      setError(`Erreur lors du marquage: ${err.message}`);
      console.error('Erreur markAsRead:', err);
    }
  }, [makeApiCall]);

  // Marquer toutes les notifications comme lues
  const markAllAsRead = useCallback(async () => {
    try {
      await makeApiCall('/api/notifications/mark-all-read', {
        method: 'PUT'
      });

      // Mettre à jour l'état local
      setNotifications(prev => prev.map(notif => 
        notif.read ? notif : { ...notif, read: true, readAt: new Date().toISOString() }
      ));

      // Réinitialiser le compteur non lu
      setStats(prev => ({
        ...prev,
        unreadCount: 0
      }));

    } catch (err: any) {
      setError(`Erreur lors du marquage global: ${err.message}`);
      console.error('Erreur markAllAsRead:', err);
    }
  }, [makeApiCall]);

  // Supprimer une notification
  const deleteNotification = useCallback(async (notificationId: number) => {
    try {
      await makeApiCall(`/api/notifications/${notificationId}`, {
        method: 'DELETE'
      });

      // Retirer de l'état local
      const notification = notifications.find(n => n.id === notificationId);
      setNotifications(prev => prev.filter(notif => notif.id !== notificationId));

      // Mettre à jour les statistiques
      if (notification) {
        setStats(prev => ({
          ...prev,
          totalCount: prev.totalCount - 1,
          unreadCount: notification.read ? prev.unreadCount : prev.unreadCount - 1,
          criticalCount: notification.critical ? prev.criticalCount - 1 : prev.criticalCount
        }));
      }

    } catch (err: any) {
      setError(`Erreur lors de la suppression: ${err.message}`);
      console.error('Erreur deleteNotification:', err);
    }
  }, [makeApiCall, notifications]);

  // Récupérer les statistiques détaillées
  const fetchStats = useCallback(async () => {
    try {
      const response = await makeApiCall('/api/notifications/stats');
      const data = await response.json();
      setStats(data);
    } catch (err: any) {
      console.error('Erreur fetchStats:', err);
    }
  }, [makeApiCall]);

  // Polling automatique pour les nouvelles notifications
  const [pollingInterval, setPollingInterval] = useState<number | null>(null);

  const startPolling = useCallback((interval: number = 30000) => {
    if (pollingInterval) {
      clearInterval(pollingInterval);
    }
    
    const intervalId = setInterval(() => {
      fetchNotifications({ page: 0, size: 20 });
    }, interval) as unknown as number;
    
    setPollingInterval(intervalId);
  }, [fetchNotifications, pollingInterval]);

  const stopPolling = useCallback(() => {
    if (pollingInterval) {
      clearInterval(pollingInterval);
      setPollingInterval(null);
    }
  }, [pollingInterval]);

  // Chargement initial et nettoyage
  useEffect(() => {
    fetchNotifications({ page: 0, size: 20 });
    
    // Nettoyage lors du démontage
    return () => {
      if (abortController.current) {
        abortController.current.abort();
      }
      if (pollingInterval) {
        clearInterval(pollingInterval);
      }
    };
  }, [fetchNotifications]);

  // Fonction pour rafraîchir les données
  const refresh = useCallback(() => {
    fetchNotifications({ page: currentPage, size: 20 });
  }, [fetchNotifications, currentPage]);

  return {
    // État
    notifications,
    loading,
    error,
    stats,
    
    // Pagination
    currentPage,
    totalPages,
    hasMore,
    
    // Actions
    fetchNotifications,
    markAsRead,
    markAllAsRead,
    deleteNotification,
    fetchStats,
    refresh,
    
    // Polling
    startPolling,
    stopPolling,
    
    // Utilitaires
    clearError: () => setError(null)
  };
};

// Hook pour les préférences de notification
export const useNotificationPreferences = () => {
  const [preferences, setPreferences] = useState<NotificationPreference[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Fonction utilitaire pour les appels API
  const makeApiCall = useCallback(async (
    endpoint: string, 
    options: RequestInit = {}
  ): Promise<Response> => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.headers,
      },
    });

    if (!response.ok) {
      const errorData = await response.text();
      throw new Error(errorData || `HTTP error! status: ${response.status}`);
    }

    return response;
  }, []);

  // Récupérer les préférences
  const fetchPreferences = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await makeApiCall('/api/notifications/preferences');
      const data: NotificationPreference[] = await response.json();
      
      setPreferences(data);
    } catch (err: any) {
      setError(`Erreur lors du chargement des préférences: ${err.message}`);
      console.error('Erreur fetchPreferences:', err);
    } finally {
      setLoading(false);
    }
  }, [makeApiCall]);

  // Mettre à jour une préférence
  const updatePreference = useCallback(async (
    preferenceId: number, 
    updates: Partial<Pick<NotificationPreference, 'enabled' | 'emailEnabled'>>
  ) => {
    try {
      const response = await makeApiCall(`/api/notifications/preferences/${preferenceId}`, {
        method: 'PUT',
        body: JSON.stringify(updates)
      });

      const updatedPreference: NotificationPreference = await response.json();
      
      setPreferences(prev => prev.map(pref => 
        pref.id === preferenceId ? updatedPreference : pref
      ));

    } catch (err: any) {
      setError(`Erreur lors de la mise à jour: ${err.message}`);
      console.error('Erreur updatePreference:', err);
    }
  }, [makeApiCall]);

  // Chargement initial
  useEffect(() => {
    fetchPreferences();
  }, [fetchPreferences]);

  return {
    preferences,
    loading,
    error,
    fetchPreferences,
    updatePreference,
    clearError: () => setError(null)
  };
};

export default useNotifications;