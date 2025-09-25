import { useState, useEffect, useCallback } from 'react';
import { RDQ } from '../types';
import { SearchCriteria } from '../components/search/SearchFilterPanel';

interface SearchResponse {
  rdqs: RDQ[];
  currentPage: number;
  totalPages: number;
  pageSize: number;
  totalElements: number;
  appliedCriteria?: SearchCriteria;
  appliedFiltersDescription?: string;
  searchDuration?: number;
  stats?: SearchStats;
}

interface SearchStats {
  totalRdqs: number;
  rdqsPlanifies: number;
  rdqsEnCours: number;
  rdqsTermines: number;
  rdqsAnnules: number;
  rdqsClos: number;
  rdqsPresentiel?: number;
  rdqsDistanciel?: number;
  rdqsHybride?: number;
  rdqsCetteSemaine?: number;
  rdqsCeMois?: number;
  rdqsCetteAnnee?: number;
}

interface UseRdqSearchReturn {
  // Données
  rdqs: RDQ[];
  searchResponse: SearchResponse | null;
  
  // États
  isLoading: boolean;
  error: string | null;
  
  // Pagination
  currentPage: number;
  totalPages: number;
  totalElements: number;
  
  // Critères
  searchCriteria: SearchCriteria;
  
  // Actions
  search: (criteria: SearchCriteria) => Promise<void>;
  clearSearch: () => void;
  goToPage: (page: number) => void;
  setPageSize: (size: number) => void;
  
  // Utilitaires
  hasResults: boolean;
  hasActiveFilters: boolean;
}

const API_BASE_URL = (import.meta as any).env.VITE_API_BASE_URL || 'http://localhost:8080';

/**
 * Hook personnalisé pour la gestion de la recherche de RDQ
 * Implémentation pour TM-41 - US09 Historique et filtrage des RDQ
 */
export const useRdqSearch = (initialCriteria: SearchCriteria = {}): UseRdqSearchReturn => {
  // États locaux
  const [rdqs, setRdqs] = useState<RDQ[]>([]);
  const [searchResponse, setSearchResponse] = useState<SearchResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searchCriteria, setSearchCriteria] = useState<SearchCriteria>({
    page: 0,
    size: 10,
    sortBy: 'dateHeure',
    sortDirection: 'DESC',
    includeHistory: false,
    ...initialCriteria
  });

  // Fonction de recherche avec authentification
  const fetchWithAuth = async (url: string, options: RequestInit = {}): Promise<Response> => {
    const token = localStorage.getItem('authToken');
    
    const headers = {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
      ...options.headers,
    };

    const response = await fetch(`${API_BASE_URL}${url}`, {
      ...options,
      headers,
    });

    if (!response.ok) {
      if (response.status === 401) {
        localStorage.removeItem('authToken');
        window.location.href = '/login';
        throw new Error('Token expiré');
      }
      
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.error || `Erreur HTTP ${response.status}`);
    }

    return response;
  };

  // Construction des paramètres URL
  const buildSearchParams = (criteria: SearchCriteria): string => {
    const params = new URLSearchParams();
    
    // Paramètres textuels
    if (criteria.clientNom) params.append('clientNom', criteria.clientNom);
    if (criteria.collaborateurNom) params.append('collaborateurNom', criteria.collaborateurNom);
    if (criteria.projetNom) params.append('projetNom', criteria.projetNom);
    if (criteria.searchTerm) params.append('searchTerm', criteria.searchTerm);
    
    // IDs
    if (criteria.clientId) params.append('clientId', criteria.clientId.toString());
    if (criteria.collaborateurId) params.append('collaborateurId', criteria.collaborateurId.toString());
    if (criteria.managerId) params.append('managerId', criteria.managerId.toString());
    if (criteria.projetId) params.append('projetId', criteria.projetId.toString());
    
    // Dates
    if (criteria.dateDebut) params.append('dateDebut', criteria.dateDebut);
    if (criteria.dateFin) params.append('dateFin', criteria.dateFin);
    
    // Arrays
    if (criteria.statuts && criteria.statuts.length > 0) {
      params.append('statuts', criteria.statuts.join(','));
    }
    if (criteria.modes && criteria.modes.length > 0) {
      params.append('modes', criteria.modes.join(','));
    }
    
    // Pagination et tri
    params.append('page', (criteria.page ?? 0).toString());
    params.append('size', (criteria.size ?? 10).toString());
    params.append('sortBy', criteria.sortBy ?? 'dateHeure');
    params.append('sortDirection', criteria.sortDirection ?? 'DESC');
    
    // Options
    params.append('includeHistory', (criteria.includeHistory ?? false).toString());
    if (criteria.includeDocuments !== undefined) {
      params.append('includeDocuments', criteria.includeDocuments.toString());
    }
    if (criteria.includeBilans !== undefined) {
      params.append('includeBilans', criteria.includeBilans.toString());
    }
    
    return params.toString();
  };

  // Fonction principale de recherche
  const search = useCallback(async (criteria: SearchCriteria) => {
    setIsLoading(true);
    setError(null);
    
    try {
      const searchParams = buildSearchParams(criteria);
      const response = await fetchWithAuth(`/api/v1/rdq/search?${searchParams}`);
      const data: SearchResponse = await response.json();
      
      setSearchResponse(data);
      setRdqs(data.rdqs || []);
      setSearchCriteria(criteria);
      
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erreur lors de la recherche';
      setError(errorMessage);
      setRdqs([]);
      setSearchResponse(null);
    } finally {
      setIsLoading(false);
    }
  }, []);

  // Effacer la recherche
  const clearSearch = useCallback(() => {
    const clearedCriteria: SearchCriteria = {
      page: 0,
      size: searchCriteria.size || 10,
      sortBy: 'dateHeure',
      sortDirection: 'DESC',
      includeHistory: false
    };
    
    setSearchCriteria(clearedCriteria);
    setRdqs([]);
    setSearchResponse(null);
    setError(null);
  }, [searchCriteria.size]);

  // Navigation pagination
  const goToPage = useCallback((page: number) => {
    if (searchResponse && page >= 0 && page < searchResponse.totalPages) {
      const newCriteria = { ...searchCriteria, page };
      search(newCriteria);
    }
  }, [search, searchCriteria, searchResponse]);

  // Changer la taille de page
  const setPageSize = useCallback((size: number) => {
    const newCriteria = { ...searchCriteria, size, page: 0 };
    search(newCriteria);
  }, [search, searchCriteria]);

  // Utilitaires calculés
  const hasResults = rdqs.length > 0;
  
  const hasActiveFilters = Object.keys(searchCriteria).some(key => {
    const value = searchCriteria[key as keyof SearchCriteria];
    if (['page', 'size', 'sortBy', 'sortDirection', 'includeHistory'].includes(key)) {
      return false;
    }
    return value !== undefined && value !== '' && 
           (Array.isArray(value) ? value.length > 0 : true);
  });

  // Pagination info
  const currentPage = searchResponse?.currentPage ?? 0;
  const totalPages = searchResponse?.totalPages ?? 0;
  const totalElements = searchResponse?.totalElements ?? 0;

  // Recherche initiale si des critères sont fournis
  useEffect(() => {
    if (initialCriteria && Object.keys(initialCriteria).length > 0) {
      search(searchCriteria);
    }
  }, []); // Seulement au montage

  return {
    // Données
    rdqs,
    searchResponse,
    
    // États
    isLoading,
    error,
    
    // Pagination
    currentPage,
    totalPages,
    totalElements,
    
    // Critères
    searchCriteria,
    
    // Actions
    search,
    clearSearch,
    goToPage,
    setPageSize,
    
    // Utilitaires
    hasResults,
    hasActiveFilters
  };
};