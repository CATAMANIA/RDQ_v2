/**
 * Hooks personnalisés pour la gestion des données API - TM-45
 * Remplace l'utilisation directe des données mock par des appels API avec gestion d'état
 */

import { useState, useEffect, useCallback } from 'react';
import { MockDataService, type AdminDataResponse, type CollaborateurDataResponse, type ManagerDataResponse, type ModalDataResponse } from '../services/MockDataService';
import { ApiError } from '../services/ApiService';
import type { User, Client, Projet, RDQ } from '../types';

export interface ApiState<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
}

/**
 * Hook générique pour les appels API
 */
function useApiCall<T>(
  apiCall: () => Promise<T>,
  dependencies: unknown[] = []
): ApiState<T> {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const result = await apiCall();
      setData(result);
    } catch (err) {
      if (err instanceof ApiError) {
        setError(`Erreur ${err.status}: ${err.message}`);
      } else {
        setError('Une erreur inattendue s\'est produite');
      }
      console.error('Erreur API:', err);
    } finally {
      setLoading(false);
    }
  }, dependencies);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return { data, loading, error, refetch: fetchData };
}

/**
 * Hook pour les données d'administration
 * Remplace : mockManagers, mockCollaborateurs, mockClients, mockAdmins
 */
export function useAdminData(): ApiState<AdminDataResponse> {
  return useApiCall(() => MockDataService.getAdminData());
}

/**
 * Hook pour les données de collaborateur
 * Remplace : mockRDQs, mockClients
 */
export function useCollaborateurData(): ApiState<CollaborateurDataResponse> {
  return useApiCall(() => MockDataService.getCollaborateurData());
}

/**
 * Hook pour les données de manager
 * Remplace : mockRDQs, mockCollaborateurs, mockClients
 */
export function useManagerData(): ApiState<ManagerDataResponse> {
  return useApiCall(() => MockDataService.getManagerData());
}

/**
 * Hook pour les données des modales
 * Remplace : mockCollaborateurs, mockClients, mockProjets
 */
export function useModalData(): ApiState<ModalDataResponse> {
  return useApiCall(() => MockDataService.getModalData());
}

/**
 * Hooks individuels pour chaque entité
 */

export function useManagers(): ApiState<User[]> {
  return useApiCall(() => MockDataService.getManagers());
}

export function useCollaborateurs(): ApiState<User[]> {
  return useApiCall(() => MockDataService.getCollaborateurs());
}

export function useAdmins(): ApiState<User[]> {
  return useApiCall(() => MockDataService.getAdmins());
}

export function useClients(): ApiState<Client[]> {
  return useApiCall(() => MockDataService.getAllClients());
}

export function useProjets(): ApiState<Projet[]> {
  return useApiCall(() => MockDataService.getAllProjets());
}

export function useRDQs(): ApiState<RDQ[]> {
  return useApiCall(() => MockDataService.getAllRDQs());
}

/**
 * Hook pour récupérer un utilisateur par ID
 */
export function useUser(id: number): ApiState<User> {
  return useApiCall(() => MockDataService.getUserById(id), [id]);
}

/**
 * Hook pour récupérer un client par ID
 */
export function useClient(id: number): ApiState<Client> {
  return useApiCall(() => MockDataService.getClientById(id), [id]);
}

/**
 * Hook pour récupérer un projet par ID
 */
export function useProjet(id: number): ApiState<Projet> {
  return useApiCall(() => MockDataService.getProjetById(id), [id]);
}

/**
 * Hook pour récupérer un RDQ par ID
 */
export function useRDQ(id: number): ApiState<RDQ> {
  return useApiCall(() => MockDataService.getRDQById(id), [id]);
}

/**
 * Hook pour la recherche de clients
 */
export function useClientSearch(searchTerm: string): ApiState<Client[]> {
  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState(searchTerm);

  // Debounce search term
  useEffect(() => {
    const timer = setTimeout(() => setDebouncedSearchTerm(searchTerm), 300);
    return () => clearTimeout(timer);
  }, [searchTerm]);

  return useApiCall(
    () => MockDataService.searchClients(debouncedSearchTerm), 
    [debouncedSearchTerm]
  );
}

/**
 * Hook pour la recherche de projets
 */
export function useProjetSearch(searchTerm: string): ApiState<Projet[]> {
  const [debouncedSearchTerm, setDebouncedSearchTerm] = useState(searchTerm);

  // Debounce search term
  useEffect(() => {
    const timer = setTimeout(() => setDebouncedSearchTerm(searchTerm), 300);
    return () => clearTimeout(timer);
  }, [searchTerm]);

  return useApiCall(
    () => MockDataService.searchProjets(debouncedSearchTerm), 
    [debouncedSearchTerm]
  );
}