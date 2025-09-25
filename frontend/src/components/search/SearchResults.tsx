import React from 'react';
import { RDQ } from '../../types';
import { SearchCriteria } from './SearchFilterPanel';

interface SearchResultsProps {
  rdqs: RDQ[];
  searchResponse: {
    currentPage: number;
    totalPages: number;
    pageSize: number;
    totalElements: number;
    appliedFiltersDescription?: string;
    searchDuration?: number;
    stats?: {
      totalRdqs: number;
      rdqsPlanifies: number;
      rdqsEnCours: number;
      rdqsTermines: number;
      rdqsAnnules: number;
      rdqsClos: number;
    };
  } | null;
  isLoading: boolean;
  error: string | null;
  onPageChange: (page: number) => void;
  onPageSizeChange: (size: number) => void;
  onRdqClick?: (rdq: RDQ) => void;
}

const PAGE_SIZE_OPTIONS = [5, 10, 20, 50];

const getStatusColor = (statut?: string) => {
  switch (statut) {
    case 'PLANIFIE':
      return 'bg-blue-100 text-blue-800';
    case 'EN_COURS':
      return 'bg-yellow-100 text-yellow-800';
    case 'TERMINE':
      return 'bg-green-100 text-green-800';
    case 'ANNULE':
      return 'bg-red-100 text-red-800';
    case 'CLOS':
      return 'bg-gray-100 text-gray-800';
    default:
      return 'bg-gray-100 text-gray-800';
  }
};

const getStatusLabel = (statut?: string) => {
  switch (statut) {
    case 'PLANIFIE': return 'Planifié';
    case 'EN_COURS': return 'En cours';
    case 'TERMINE': return 'Terminé';
    case 'ANNULE': return 'Annulé';
    case 'CLOS': return 'Clos';
    default: return statut || 'Inconnu';
  }
};

const getModeIcon = (mode?: string) => {
  switch (mode) {
    case 'PRESENTIEL': return '🏢';
    case 'DISTANCIEL': return '💻';
    case 'HYBRIDE': return '🔄';
    default: return '❓';
  }
};

const formatDate = (dateString?: string) => {
  if (!dateString) return 'Non défini';
  
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch {
    return 'Date invalide';
  }
};

/**
 * Composant d'affichage des résultats de recherche avec pagination
 * Implémentation pour TM-41 - US09 Historique et filtrage des RDQ
 */
export const SearchResults: React.FC<SearchResultsProps> = ({
  rdqs,
  searchResponse,
  isLoading,
  error,
  onPageChange,
  onPageSizeChange,
  onRdqClick
}) => {
  
  const renderPagination = () => {
    if (!searchResponse || searchResponse.totalPages <= 1) return null;

    const { currentPage, totalPages } = searchResponse;
    const pages = [];
    
    // Calcul des pages à afficher
    const showPages = 5;
    let startPage = Math.max(0, currentPage - Math.floor(showPages / 2));
    let endPage = Math.min(totalPages - 1, startPage + showPages - 1);
    
    if (endPage - startPage < showPages - 1) {
      startPage = Math.max(0, endPage - showPages + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return (
      <div className="flex items-center justify-between px-4 py-3 bg-white border-t border-gray-200 sm:px-6">
        <div className="flex justify-between flex-1 sm:hidden">
          {/* Mobile pagination */}
          <button
            onClick={() => onPageChange(currentPage - 1)}
            disabled={currentPage === 0}
            className="relative inline-flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Précédent
          </button>
          <button
            onClick={() => onPageChange(currentPage + 1)}
            disabled={currentPage >= totalPages - 1}
            className="relative ml-3 inline-flex items-center px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Suivant
          </button>
        </div>
        
        <div className="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
          <div className="flex items-center space-x-2">
            <p className="text-sm text-gray-700">
              Affichage de{' '}
              <span className="font-medium">
                {searchResponse.totalElements > 0 ? currentPage * searchResponse.pageSize + 1 : 0}
              </span>{' '}
              à{' '}
              <span className="font-medium">
                {Math.min((currentPage + 1) * searchResponse.pageSize, searchResponse.totalElements)}
              </span>{' '}
              sur{' '}
              <span className="font-medium">{searchResponse.totalElements}</span>{' '}
              résultats
            </p>
            
            <select
              value={searchResponse.pageSize}
              onChange={(e) => onPageSizeChange(parseInt(e.target.value))}
              className="ml-4 text-sm border border-gray-300 rounded-md px-2 py-1 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              {PAGE_SIZE_OPTIONS.map(size => (
                <option key={size} value={size}>
                  {size} par page
                </option>
              ))}
            </select>
          </div>
          
          <div>
            <nav className="relative z-0 inline-flex -space-x-px rounded-md shadow-sm" aria-label="Pagination">
              {/* Bouton Précédent */}
              <button
                onClick={() => onPageChange(currentPage - 1)}
                disabled={currentPage === 0}
                className="relative inline-flex items-center px-2 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-l-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clipRule="evenodd" />
                </svg>
              </button>
              
              {/* Pages */}
              {pages.map(page => (
                <button
                  key={page}
                  onClick={() => onPageChange(page)}
                  className={`relative inline-flex items-center px-4 py-2 text-sm font-medium border ${
                    page === currentPage
                      ? 'z-10 bg-blue-50 border-blue-500 text-blue-600'
                      : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50'
                  }`}
                >
                  {page + 1}
                </button>
              ))}
              
              {/* Bouton Suivant */}
              <button
                onClick={() => onPageChange(currentPage + 1)}
                disabled={currentPage >= totalPages - 1}
                className="relative inline-flex items-center px-2 py-2 text-sm font-medium text-gray-500 bg-white border border-gray-300 rounded-r-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clipRule="evenodd" />
                </svg>
              </button>
            </nav>
          </div>
        </div>
      </div>
    );
  };

  const renderStats = () => {
    if (!searchResponse?.stats) return null;

    const { stats } = searchResponse;
    return (
      <div className="bg-gray-50 px-4 py-3 border-b border-gray-200">
        <div className="flex flex-wrap gap-4 text-sm text-gray-600">
          <span>Total: <strong>{stats.totalRdqs}</strong></span>
          <span>Planifiés: <strong className="text-blue-600">{stats.rdqsPlanifies}</strong></span>
          <span>En cours: <strong className="text-yellow-600">{stats.rdqsEnCours}</strong></span>
          <span>Terminés: <strong className="text-green-600">{stats.rdqsTermines}</strong></span>
          {stats.rdqsAnnules > 0 && (
            <span>Annulés: <strong className="text-red-600">{stats.rdqsAnnules}</strong></span>
          )}
          {stats.rdqsClos > 0 && (
            <span>Clos: <strong className="text-gray-600">{stats.rdqsClos}</strong></span>
          )}
        </div>
      </div>
    );
  };

  if (error) {
    return (
      <div className="bg-white rounded-lg shadow-sm border border-gray-200">
        <div className="p-6 text-center">
          <div className="text-red-500 text-lg mb-2">❌ Erreur</div>
          <p className="text-gray-600">{error}</p>
        </div>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="bg-white rounded-lg shadow-sm border border-gray-200">
        <div className="p-6 text-center">
          <div className="animate-spin inline-block w-8 h-8 border-4 border-current border-t-transparent text-blue-600 rounded-full mb-4"></div>
          <p className="text-gray-600">Recherche en cours...</p>
        </div>
      </div>
    );
  }

  if (!rdqs.length) {
    return (
      <div className="bg-white rounded-lg shadow-sm border border-gray-200">
        <div className="p-6 text-center">
          <div className="text-gray-400 text-6xl mb-4">📭</div>
          <h3 className="text-lg font-medium text-gray-900 mb-2">Aucun résultat trouvé</h3>
          <p className="text-gray-600">
            Essayez de modifier vos critères de recherche ou de supprimer certains filtres.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200">
      {/* Header avec métadonnées */}
      <div className="px-4 py-3 border-b border-gray-200">
        <div className="flex justify-between items-center">
          <h3 className="text-lg font-semibold text-gray-900">
            📋 Résultats de recherche ({searchResponse?.totalElements || rdqs.length})
          </h3>
          {searchResponse?.searchDuration && (
            <span className="text-sm text-gray-500">
              en {searchResponse.searchDuration}ms
            </span>
          )}
        </div>
        {searchResponse?.appliedFiltersDescription && (
          <p className="text-sm text-gray-600 mt-1">
            {searchResponse.appliedFiltersDescription}
          </p>
        )}
      </div>

      {/* Statistiques */}
      {renderStats()}

      {/* Liste des RDQ */}
      <div className="divide-y divide-gray-200">
        {rdqs.map(rdq => (
          <div
            key={rdq.idRdq}
            className={`p-4 hover:bg-gray-50 ${onRdqClick ? 'cursor-pointer' : ''}`}
            onClick={() => onRdqClick?.(rdq)}
          >
            <div className="flex items-start justify-between">
              <div className="flex-1 min-w-0">
                {/* Titre et statut */}
                <div className="flex items-center space-x-3 mb-2">
                  <h4 className="text-lg font-medium text-gray-900 truncate">
                    {rdq.titre}
                  </h4>
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(rdq.statut)}`}>
                    {getStatusLabel(rdq.statut)}
                  </span>
                  <span className="text-sm text-gray-500">
                    {getModeIcon(rdq.mode)} {rdq.mode}
                  </span>
                </div>

                {/* Détails */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 text-sm text-gray-600">
                  <div>
                    <span className="font-medium">📅 Date:</span><br />
                    {formatDate(rdq.dateHeure)}
                  </div>
                  <div>
                    <span className="font-medium">👤 Manager:</span><br />
                    {rdq.manager?.nom || 'Non assigné'}
                  </div>
                  <div>
                    <span className="font-medium">🏢 Client:</span><br />
                    {rdq.projet?.client || 'Non défini'}
                  </div>
                  <div>
                    <span className="font-medium">📂 Projet:</span><br />
                    {rdq.projet?.nom || 'Non défini'}
                  </div>
                </div>

                {/* Collaborateurs */}
                {rdq.collaborateurs && rdq.collaborateurs.length > 0 && (
                  <div className="mt-2">
                    <span className="font-medium text-sm text-gray-600">👥 Collaborateurs:</span>
                    <div className="flex flex-wrap gap-1 mt-1">
                      {rdq.collaborateurs.map(collab => (
                        <span
                          key={collab.idCollaborateur}
                          className="inline-flex items-center px-2 py-1 rounded-md text-xs font-medium bg-blue-50 text-blue-700"
                        >
                          {collab.nom}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                {/* Description */}
                {rdq.description && (
                  <div className="mt-2">
                    <p className="text-sm text-gray-600 line-clamp-2">
                      {rdq.description}
                    </p>
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Pagination */}
      {renderPagination()}
    </div>
  );
};