import { useState, useEffect, useCallback } from 'react';
import { Bilan } from '../types';

/**
 * Hook personnalisé pour la gestion des bilans post-entretien (TM-38)
 * Fournit les fonctionnalités CRUD pour les bilans d'un RDQ
 */
export const useBilans = (rdqId: number) => {
  const [bilans, setBilans] = useState<Bilan[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  /**
   * Charge tous les bilans d'un RDQ
   */
  const loadBilans = useCallback(async () => {
    if (!rdqId) return;

    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`/api/rdqs/${rdqId}/bilans`);
      if (!response.ok) {
        throw new Error('Erreur lors du chargement des bilans');
      }
      const bilansData: Bilan[] = await response.json();
      setBilans(bilansData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erreur inconnue');
      setBilans([]);
    } finally {
      setLoading(false);
    }
  }, [rdqId]);

  /**
   * Ajoute un nouveau bilan à la liste
   */
  const addBilan = useCallback((newBilan: Bilan) => {
    setBilans(prev => [newBilan, ...prev]);
  }, []);

  /**
   * Met à jour un bilan existant
   */
  const updateBilan = useCallback((updatedBilan: Bilan) => {
    setBilans(prev => 
      prev.map(bilan => 
        bilan.idBilan === updatedBilan.idBilan ? updatedBilan : bilan
      )
    );
  }, []);

  /**
   * Supprime un bilan de la liste
   */
  const removeBilan = useCallback((bilanId: number) => {
    setBilans(prev => prev.filter(bilan => bilan.idBilan !== bilanId));
  }, []);

  /**
   * Recharge les bilans depuis le serveur
   */
  const refreshBilans = useCallback(() => {
    loadBilans();
  }, [loadBilans]);

  // Chargement initial des bilans
  useEffect(() => {
    loadBilans();
  }, [loadBilans]);

  return {
    bilans,
    loading,
    error,
    addBilan,
    updateBilan,
    removeBilan,
    refreshBilans,
    setError
  };
};