/**
 * Hook React pour la gestion des RDQ
 * Fournit un état et des méthodes pour récupérer et manipuler les RDQ
 */

import { useState, useEffect, useCallback } from 'react';
import { RdqApiService, RdqStatusUtils } from '../services/rdqApi';
import { RDQ } from '../types';

export interface UseRdqsResult {
  rdqs: RDQ[];
  loading: boolean;
  error: string | null;
  filteredRdqs: RDQ[];
  stats: {
    total: number;
    enCours: number;
    termines: number;
    annules: number;
    bilanManquant: number;
  };
  // Filtres
  searchTerm: string;
  setSearchTerm: (term: string) => void;
  clientFilter: string;
  setClientFilter: (client: string) => void;
  showHistory: boolean;
  setShowHistory: (show: boolean) => void;
  statusFilter: string;
  setStatusFilter: (status: string) => void;
  // Actions
  refreshRdqs: () => Promise<void>;
  getRdqById: (id: number) => Promise<RDQ | null>;
}

/**
 * Hook pour gérer les RDQ du collaborateur connecté
 */
export const useRdqs = (): UseRdqsResult => {
  // État des données
  const [rdqs, setRdqs] = useState<RDQ[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // État des filtres
  const [searchTerm, setSearchTerm] = useState('');
  const [clientFilter, setClientFilter] = useState('all');
  const [showHistory, setShowHistory] = useState(false);
  const [statusFilter, setStatusFilter] = useState('all');

  /**
   * Charge les RDQ depuis l'API
   */
  const loadRdqs = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await RdqApiService.getMyAssignments(
        showHistory,
        statusFilter === 'all' ? undefined : statusFilter
      );

      if (response.success) {
        setRdqs(response.data);
      } else {
        setError(response.error || 'Erreur lors du chargement des RDQ');
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erreur inconnue';
      setError(errorMessage);
      console.error('Erreur lors du chargement des RDQ:', err);
    } finally {
      setLoading(false);
    }
  }, [showHistory, statusFilter]);

  /**
   * Rafraîchit les données
   */
  const refreshRdqs = useCallback(async () => {
    await loadRdqs();
  }, [loadRdqs]);

  /**
   * Récupère un RDQ spécifique par ID
   */
  const getRdqById = useCallback(async (id: number): Promise<RDQ | null> => {
    try {
      const response = await RdqApiService.getRdqById(id);
      if (response.success) {
        return response.data;
      } else {
        setError(response.error || `RDQ ${id} non trouvé`);
        return null;
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erreur inconnue';
      setError(errorMessage);
      return null;
    }
  }, []);

  // Chargement initial
  useEffect(() => {
    loadRdqs();
  }, [loadRdqs]);

  // RDQ filtrés selon les critères de recherche et filtres
  const filteredRdqs = RdqStatusUtils.filterRdqs(
    rdqs,
    searchTerm,
    clientFilter,
    showHistory
  );

  // Calcul des statistiques
  const stats = {
    total: rdqs.length,
    enCours: rdqs.filter((rdq: RDQ) => rdq.statut === 'EN_COURS').length,
    termines: rdqs.filter((rdq: RDQ) => rdq.statut === 'TERMINE').length,
    annules: rdqs.filter((rdq: RDQ) => rdq.statut === 'ANNULE').length,
    bilanManquant: rdqs.filter((rdq: RDQ) => {
      // RDQ passé sans bilan collaborateur
      const isPassé = new Date(rdq.dateHeure) < new Date();
      const hasBilanCollaborateur = rdq.bilans?.some((b: any) => b.auteur === 'collaborateur');
      return isPassé && !hasBilanCollaborateur && rdq.statut === 'EN_COURS';
    }).length,
  };

  return {
    rdqs,
    loading,
    error,
    filteredRdqs,
    stats,
    searchTerm,
    setSearchTerm,
    clientFilter,
    setClientFilter,
    showHistory,
    setShowHistory,
    statusFilter,
    setStatusFilter,
    refreshRdqs,
    getRdqById,
  };
};

/**
 * Hook pour un RDQ spécifique
 */
export const useRdq = (id: number) => {
  const [rdq, setRdq] = useState<RDQ | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadRdq = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await RdqApiService.getRdqById(id);
      if (response.success) {
        setRdq(response.data);
      } else {
        setError(response.error || 'RDQ non trouvé');
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erreur inconnue';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    if (id) {
      loadRdq();
    }
  }, [id, loadRdq]);

  return {
    rdq,
    loading,
    error,
    refresh: loadRdq,
  };
};

export default useRdqs;