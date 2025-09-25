import React, { useState, useEffect, useRef, useCallback } from 'react';
import { 
  Bell, 
  BellOff, 
  Check, 
  CheckCheck, 
  Trash2, 
  Settings, 
  X, 
  AlertCircle,
  Clock,
  User,
  FileText,
  RefreshCw,
  Filter
} from 'lucide-react';
import { useNotifications, useNotificationPreferences, Notification, NotificationType } from '../hooks/useNotifications';
import { NOTIFICATION_STYLES, SUCCESS_MESSAGES } from '../config/constants';
import './NotificationCenter.css';

// Interface pour les props du composant
interface NotificationCenterProps {
  className?: string;
  autoStartPolling?: boolean;
  maxDisplayedNotifications?: number;
  showPreferences?: boolean;
}

// Composant pour une notification individuelle
const NotificationItem: React.FC<{
  notification: Notification;
  onMarkAsRead: (id: number) => void;
  onDelete: (id: number) => void;
  onNotificationClick?: (notification: Notification) => void;
}> = ({ notification, onMarkAsRead, onDelete, onNotificationClick }) => {
  const [isDeleting, setIsDeleting] = useState(false);
  
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const now = new Date();
    const diffInHours = (now.getTime() - date.getTime()) / (1000 * 60 * 60);
    
    if (diffInHours < 1) {
      const diffInMinutes = Math.floor(diffInHours * 60);
      return `il y a ${diffInMinutes} min`;
    } else if (diffInHours < 24) {
      return `il y a ${Math.floor(diffInHours)}h`;
    } else {
      return date.toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    }
  };

  const getNotificationIcon = (type: NotificationType) => {
    const iconName = NOTIFICATION_STYLES[type]?.icon || 'info-circle';
    const iconMap: Record<string, React.ReactNode> = {
      'plus-circle': <FileText size={16} />,
      'edit': <RefreshCw size={16} />,
      'user-plus': <User size={16} />,
      'refresh': <RefreshCw size={16} />,
      'clock': <Clock size={16} />,
      'exclamation-triangle': <AlertCircle size={16} />,
      'comment': <FileText size={16} />,
      'times-circle': <X size={16} />,
      'tools': <Settings size={16} />,
      'hand-wave': <User size={16} />,
      'info-circle': <AlertCircle size={16} />,
    };
    return iconMap[iconName] || <AlertCircle size={16} />;
  };

  const handleDelete = async (e: React.MouseEvent) => {
    e.stopPropagation();
    setIsDeleting(true);
    try {
      await onDelete(notification.id);
    } catch (error) {
      setIsDeleting(false);
    }
  };

  const handleMarkAsRead = async (e: React.MouseEvent) => {
    e.stopPropagation();
    if (!notification.read) {
      await onMarkAsRead(notification.id);
    }
  };

  const handleClick = () => {
    if (onNotificationClick) {
      onNotificationClick(notification);
    }
    if (!notification.read) {
      onMarkAsRead(notification.id);
    }
  };

  return (
    <div 
      className={`notification-item ${notification.read ? 'read' : 'unread'} ${notification.critical ? 'critical' : ''} ${isDeleting ? 'deleting' : ''}`}
      onClick={handleClick}
    >
      <div className="notification-icon" style={{ color: NOTIFICATION_STYLES[notification.type]?.color }}>
        {getNotificationIcon(notification.type)}
      </div>
      
      <div className="notification-content">
        <div className="notification-header">
          <h4 className="notification-title">{notification.title}</h4>
          <span className="notification-time">{formatDate(notification.createdAt)}</span>
        </div>
        
        <p className="notification-message">{notification.message}</p>
        
        {notification.rdqInfo && (
          <div className="notification-rdq-info">
            <span className="rdq-numero">RDQ #{notification.rdqInfo.numero}</span>
            <span className="rdq-status">{notification.rdqInfo.status}</span>
          </div>
        )}
      </div>
      
      <div className="notification-actions">
        {!notification.read && (
          <button
            className="action-button mark-read"
            onClick={handleMarkAsRead}
            title="Marquer comme lu"
          >
            <Check size={14} />
          </button>
        )}
        
        <button
          className="action-button delete"
          onClick={handleDelete}
          disabled={isDeleting}
          title="Supprimer"
        >
          <Trash2 size={14} />
        </button>
      </div>
    </div>
  );
};

// Composant pour les filtres de notifications
const NotificationFilters: React.FC<{
  selectedType: NotificationType | null;
  selectedRead: boolean | null;
  selectedCritical: boolean | null;
  onTypeChange: (type: NotificationType | null) => void;
  onReadChange: (read: boolean | null) => void;
  onCriticalChange: (critical: boolean | null) => void;
  onReset: () => void;
}> = ({ 
  selectedType, 
  selectedRead, 
  selectedCritical, 
  onTypeChange, 
  onReadChange, 
  onCriticalChange, 
  onReset 
}) => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="notification-filters">
      <button 
        className="filter-toggle"
        onClick={() => setIsOpen(!isOpen)}
        title="Filtres"
      >
        <Filter size={16} />
        Filtres
      </button>
      
      {isOpen && (
        <div className="filter-dropdown">
          <div className="filter-group">
            <label>Type de notification</label>
            <select 
              value={selectedType || ''} 
              onChange={(e) => onTypeChange(e.target.value as NotificationType || null)}
            >
              <option value="">Tous les types</option>
              {Object.values(NotificationType).map(type => (
                <option key={type} value={type}>
                  {type.replace('_', ' ').toLowerCase()}
                </option>
              ))}
            </select>
          </div>
          
          <div className="filter-group">
            <label>Statut de lecture</label>
            <select 
              value={selectedRead === null ? '' : selectedRead.toString()} 
              onChange={(e) => onReadChange(e.target.value === '' ? null : e.target.value === 'true')}
            >
              <option value="">Toutes</option>
              <option value="false">Non lues</option>
              <option value="true">Lues</option>
            </select>
          </div>
          
          <div className="filter-group">
            <label>Criticité</label>
            <select 
              value={selectedCritical === null ? '' : selectedCritical.toString()} 
              onChange={(e) => onCriticalChange(e.target.value === '' ? null : e.target.value === 'true')}
            >
              <option value="">Toutes</option>
              <option value="true">Critiques</option>
              <option value="false">Normales</option>
            </select>
          </div>
          
          <button className="filter-reset" onClick={onReset}>
            Réinitialiser
          </button>
        </div>
      )}
    </div>
  );
};

// Composant pour les préférences de notifications
const NotificationPreferences: React.FC<{
  isOpen: boolean;
  onClose: () => void;
}> = ({ isOpen, onClose }) => {
  const { preferences, loading, updatePreference } = useNotificationPreferences();

  if (!isOpen) return null;

  return (
    <div className="notification-preferences-modal">
      <div className="modal-overlay" onClick={onClose} />
      <div className="modal-content">
        <div className="modal-header">
          <h3>Préférences de notifications</h3>
          <button className="close-button" onClick={onClose}>
            <X size={20} />
          </button>
        </div>
        
        <div className="modal-body">
          {loading ? (
            <div className="loading">Chargement des préférences...</div>
          ) : (
            <div className="preferences-list">
              {preferences.map(preference => (
                <div key={preference.id} className="preference-item">
                  <div className="preference-info">
                    <h4>{preference.notificationType.replace('_', ' ')}</h4>
                    <p>{preference.description}</p>
                  </div>
                  
                  <div className="preference-controls">
                    <label className="checkbox-label">
                      <input
                        type="checkbox"
                        checked={preference.enabled}
                        onChange={(e) => updatePreference(preference.id, { enabled: e.target.checked })}
                      />
                      Activé
                    </label>
                    
                    <label className="checkbox-label">
                      <input
                        type="checkbox"
                        checked={preference.emailEnabled}
                        onChange={(e) => updatePreference(preference.id, { emailEnabled: e.target.checked })}
                        disabled={!preference.enabled}
                      />
                      Email
                    </label>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

// Composant principal NotificationCenter
const NotificationCenter: React.FC<NotificationCenterProps> = ({
  className = '',
  autoStartPolling = true,
  maxDisplayedNotifications = 10,
  showPreferences = true
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [showPreferencesModal, setShowPreferencesModal] = useState(false);
  const [selectedType, setSelectedType] = useState<NotificationType | null>(null);
  const [selectedRead, setSelectedRead] = useState<boolean | null>(null);
  const [selectedCritical, setSelectedCritical] = useState<boolean | null>(null);
  const [toastMessage, setToastMessage] = useState<string | null>(null);
  
  const dropdownRef = useRef<HTMLDivElement>(null);
  
  const {
    notifications,
    loading,
    error,
    stats,
    fetchNotifications,
    markAsRead,
    markAllAsRead,
    deleteNotification,
    startPolling,
    stopPolling,
    refresh,
    clearError
  } = useNotifications();

  // Gestion du polling automatique
  useEffect(() => {
    if (autoStartPolling) {
      startPolling();
      return () => stopPolling();
    }
  }, [autoStartPolling, startPolling, stopPolling]);

  // Fermeture du dropdown en cliquant à l'extérieur
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Affichage des messages toast
  const showToast = useCallback((message: string) => {
    setToastMessage(message);
    setTimeout(() => setToastMessage(null), 3000);
  }, []);

  // Gestionnaires d'événements
  const handleMarkAsRead = async (notificationId: number) => {
    try {
      await markAsRead(notificationId);
      showToast(SUCCESS_MESSAGES.NOTIFICATION_MARKED_READ);
    } catch (error) {
      console.error('Erreur lors du marquage:', error);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await markAllAsRead();
      showToast(SUCCESS_MESSAGES.ALL_NOTIFICATIONS_MARKED_READ);
    } catch (error) {
      console.error('Erreur lors du marquage global:', error);
    }
  };

  const handleDeleteNotification = async (notificationId: number) => {
    try {
      await deleteNotification(notificationId);
      showToast(SUCCESS_MESSAGES.NOTIFICATION_DELETED);
    } catch (error) {
      console.error('Erreur lors de la suppression:', error);
    }
  };

  const handleFilterChange = () => {
    fetchNotifications({
      page: 0,
      size: maxDisplayedNotifications,
      type: selectedType || undefined,
      read: selectedRead === null ? undefined : selectedRead,
      critical: selectedCritical === null ? undefined : selectedCritical,
      sortBy: 'createdAt',
      sortDirection: 'DESC'
    });
  };

  const resetFilters = () => {
    setSelectedType(null);
    setSelectedRead(null);
    setSelectedCritical(null);
    fetchNotifications({ page: 0, size: maxDisplayedNotifications });
  };

  // Appliquer les filtres lors du changement
  useEffect(() => {
    handleFilterChange();
  }, [selectedType, selectedRead, selectedCritical]);

  const handleNotificationClick = (notification: Notification) => {
    // Navigation vers la RDQ si applicable
    if (notification.rdqId && notification.rdqInfo) {
      // Ici on pourrait utiliser React Router pour naviguer
      console.log('Navigation vers RDQ:', notification.rdqInfo.numero);
    }
  };

  return (
    <>
      <div className={`notification-center ${className}`} ref={dropdownRef}>
        <button 
          className={`notification-button ${stats.unreadCount > 0 ? 'has-unread' : ''}`}
          onClick={() => setIsOpen(!isOpen)}
          title={`${stats.unreadCount} notification(s) non lue(s)`}
        >
          {stats.unreadCount > 0 ? <Bell size={20} /> : <BellOff size={20} />}
          
          {stats.unreadCount > 0 && (
            <span className="notification-badge">
              {stats.unreadCount > 99 ? '99+' : stats.unreadCount}
            </span>
          )}
          
          {stats.criticalCount > 0 && (
            <span className="critical-indicator" title={`${stats.criticalCount} notification(s) critique(s)`}>
              <AlertCircle size={12} />
            </span>
          )}
        </button>

        {isOpen && (
          <div className="notification-dropdown">
            <div className="notification-header">
              <h3>Notifications</h3>
              <div className="header-actions">
                <button 
                  className="action-button refresh"
                  onClick={refresh}
                  disabled={loading}
                  title="Actualiser"
                >
                  <RefreshCw size={16} className={loading ? 'spinning' : ''} />
                </button>
                
                {stats.unreadCount > 0 && (
                  <button 
                    className="action-button mark-all-read"
                    onClick={handleMarkAllAsRead}
                    title="Marquer toutes comme lues"
                  >
                    <CheckCheck size={16} />
                  </button>
                )}
                
                {showPreferences && (
                  <button 
                    className="action-button settings"
                    onClick={() => setShowPreferencesModal(true)}
                    title="Préférences"
                  >
                    <Settings size={16} />
                  </button>
                )}
              </div>
            </div>

            <NotificationFilters
              selectedType={selectedType}
              selectedRead={selectedRead}
              selectedCritical={selectedCritical}
              onTypeChange={setSelectedType}
              onReadChange={setSelectedRead}
              onCriticalChange={setSelectedCritical}
              onReset={resetFilters}
            />

            <div className="notification-stats">
              <span>Total: {stats.totalCount}</span>
              <span>Non lues: {stats.unreadCount}</span>
              {stats.criticalCount > 0 && (
                <span className="critical">Critiques: {stats.criticalCount}</span>
              )}
            </div>

            {error && (
              <div className="error-message">
                {error}
                <button onClick={clearError} className="error-close">
                  <X size={14} />
                </button>
              </div>
            )}

            <div className="notification-list">
              {loading && notifications.length === 0 ? (
                <div className="loading">Chargement des notifications...</div>
              ) : notifications.length === 0 ? (
                <div className="empty-state">
                  <BellOff size={48} />
                  <p>Aucune notification</p>
                </div>
              ) : (
                notifications.slice(0, maxDisplayedNotifications).map(notification => (
                  <NotificationItem
                    key={notification.id}
                    notification={notification}
                    onMarkAsRead={handleMarkAsRead}
                    onDelete={handleDeleteNotification}
                    onNotificationClick={handleNotificationClick}
                  />
                ))
              )}
              
              {notifications.length > maxDisplayedNotifications && (
                <div className="load-more">
                  <button onClick={() => fetchNotifications({ page: 1, size: 20 })}>
                    Voir plus
                  </button>
                </div>
              )}
            </div>
          </div>
        )}
      </div>

      {/* Modal des préférences */}
      <NotificationPreferences
        isOpen={showPreferencesModal}
        onClose={() => setShowPreferencesModal(false)}
      />

      {/* Toast de notification */}
      {toastMessage && (
        <div className="notification-toast">
          <Check size={16} />
          {toastMessage}
        </div>
      )}
    </>
  );
};

export default NotificationCenter;