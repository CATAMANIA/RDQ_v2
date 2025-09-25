/**
 * Service API pour les RDQ
 * Gestion des appels REST vers le backend Java
 */

import { RDQ } from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
const API_VERSION = '/api/v1';

export interface RdqApiResponse {
  success: boolean;
  data: RDQ[];
  count: number;
  filters?: {
    includeHistory: boolean;
    statut: string;
  };
  error?: string;
}

export interface RdqDetailResponse {
  success: boolean;
  data: RDQ;
  error?: string;
}

/**
 * Classe service pour les appels API RDQ
 */
export class RdqApiService {
  private static async fetchWithAuth(url: string, options: RequestInit = {}): Promise<Response> {
    const token = localStorage.getItem('authToken');
    
    const headers = {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
      ...options.headers,
    };

    const response = await fetch(`${API_BASE_URL}${API_VERSION}${url}`, {
      ...options,
      headers,
    });

    if (!response.ok) {
      if (response.status === 401) {
        // Token expiré, rediriger vers login
        localStorage.removeItem('authToken');
        window.location.href = '/login';
        throw new Error('Token expiré');
      }
      
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.error || `Erreur HTTP ${response.status}`);
    }

    return response;
  }

  /**
   * Récupère les RDQ assignés au collaborateur connecté
   */
  static async getMyAssignments(
    includeHistory: boolean = false,
    statut?: string
  ): Promise<RdqApiResponse> {
    try {
      const params = new URLSearchParams();
      params.append('includeHistory', includeHistory.toString());
      
      if (statut && statut !== 'all') {
        params.append('statut', statut);
      }

      const response = await this.fetchWithAuth(`/rdq/my-assignments?${params}`);
      return await response.json();
    } catch (error) {
      console.error('Erreur lors de la récupération des RDQ:', error);
      throw error;
    }
  }

  /**
   * Récupère un RDQ spécifique par son ID
   */
  static async getRdqById(id: number): Promise<RdqDetailResponse> {
    try {
      const response = await this.fetchWithAuth(`/rdq/${id}`);
      return await response.json();
    } catch (error) {
      console.error(`Erreur lors de la récupération du RDQ ${id}:`, error);
      throw error;
    }
  }

  /**
   * Test de connectivité de l'API
   */
  static async healthCheck(): Promise<{ status: string; service: string; version: string }> {
    try {
      const response = await this.fetchWithAuth('/rdq/health');
      return await response.json();
    } catch (error) {
      console.error('Erreur lors du health check:', error);
      throw error;
    }
  }

  /**
   * Modifie un RDQ existant (Manager uniquement)
   */
  static async updateRdq(rdqId: number, updateData: Partial<RDQ>): Promise<RdqDetailResponse> {
    try {
      const body = {
        // Champs modifiables
        ...(updateData.titre && { titre: updateData.titre }),
        ...(updateData.dateHeure && { dateHeure: updateData.dateHeure }),
        ...(updateData.adresse !== undefined && { adresse: updateData.adresse }),
        ...(updateData.mode && { mode: updateData.mode }),
        ...(updateData.description !== undefined && { description: updateData.description }),
        ...(updateData.projet?.idProjet && { projetId: updateData.projet.idProjet }),
        ...(updateData.collaborateurs && { 
          collaborateurIds: updateData.collaborateurs.map(c => c.idCollaborateur) 
        })
      };

      const response = await this.fetchWithAuth(`/rdq/${rdqId}`, {
        method: 'PUT',
        body: JSON.stringify(body),
      });

      const result = await response.json();
      
      if (result.success) {
        return {
          success: true,
          data: result.data,
        };
      } else {
        throw new Error(result.error || 'Erreur lors de la modification du RDQ');
      }
    } catch (error) {
      console.error('Erreur lors de la modification du RDQ:', error);
      throw error;
    }
  }
}

/**
 * Utilitaires pour la gestion des statuts RDQ
 */
export const RdqStatusUtils = {
  /**
   * Traduit les statuts de l'API vers l'affichage français
   */
  translateStatus: (status: string): string => {
    const translations: Record<string, string> = {
      'PLANIFIE': 'Planifié',
      'EN_COURS': 'En cours',
      'TERMINE': 'Terminé',
      'ANNULE': 'Annulé',
      'CLOS': 'Clôturé',
    };
    return translations[status] || status;
  },

  /**
   * Retourne la couleur du badge selon le statut
   */
  getStatusBadgeVariant: (status: string): 'default' | 'secondary' | 'destructive' | 'outline' => {
    switch (status) {
      case 'PLANIFIE':
        return 'outline';
      case 'EN_COURS':
        return 'default';
      case 'TERMINE':
        return 'secondary';
      case 'ANNULE':
        return 'destructive';
      case 'CLOS':
        return 'secondary';
      default:
        return 'outline';
    }
  },

  /**
   * Détermine si un RDQ est dans l'historique
   */
  isHistorical: (status: string): boolean => {
    return status === 'TERMINE' || status === 'ANNULE' || status === 'CLOS';
  },

  /**
   * Filtre les RDQ selon les critères d'affichage
   */
  filterRdqs: (
    rdqs: RDQ[], 
    searchTerm: string, 
    clientFilter: string, 
    showHistory: boolean
  ): RDQ[] => {
    return rdqs.filter(rdq => {
      // Filtre par terme de recherche
      const matchesSearch = searchTerm === '' ||
        rdq.titre.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (rdq.projet?.nom || '').toLowerCase().includes(searchTerm.toLowerCase()) ||
        (rdq.manager?.nom || '').toLowerCase().includes(searchTerm.toLowerCase());

      // Filtre par client (si applicable)
      const matchesClient = clientFilter === 'all' || 
        rdq.projet?.client === clientFilter;

      // Filtre par historique
      const matchesHistory = showHistory || !RdqStatusUtils.isHistorical(rdq.statut || '');

      return matchesSearch && matchesClient && matchesHistory;
    });
  }
};

export default RdqApiService;