import React, { useState, useEffect } from 'react';
import { RDQ } from '../../types';

interface SearchFilterPanelProps {
  onSearch: (criteria: SearchCriteria) => void;
  onClear: () => void;
  isLoading?: boolean;
  initialCriteria?: SearchCriteria;
}

export interface SearchCriteria {
  clientNom?: string;
  clientId?: number;
  collaborateurNom?: string;
  collaborateurId?: number;
  managerId?: number;
  projetNom?: string;
  projetId?: number;
  statuts?: string[];
  modes?: string[];
  dateDebut?: string; // ISO string
  dateFin?: string;   // ISO string
  searchTerm?: string;
  includeHistory?: boolean;
  includeDocuments?: boolean;
  includeBilans?: boolean;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

const STATUT_OPTIONS = [
  { value: 'PLANIFIE', label: 'Planifié' },
  { value: 'EN_COURS', label: 'En cours' },
  { value: 'TERMINE', label: 'Terminé' },
  { value: 'ANNULE', label: 'Annulé' },
  { value: 'CLOS', label: 'Clos' }
];

const MODE_OPTIONS = [
  { value: 'PRESENTIEL', label: 'Présentiel' },
  { value: 'DISTANCIEL', label: 'Distanciel' },
  { value: 'HYBRIDE', label: 'Hybride' }
];

const SORT_OPTIONS = [
  { value: 'dateHeure', label: 'Date de RDQ' },
  { value: 'titre', label: 'Titre' },
  { value: 'statut', label: 'Statut' },
  { value: 'client', label: 'Client' },
  { value: 'dateCreation', label: 'Date de création' }
];

/**
 * Panneau de filtrage avancé pour la recherche de RDQ
 * Implémentation pour TM-41 - US09 Historique et filtrage des RDQ
 */
export const SearchFilterPanel: React.FC<SearchFilterPanelProps> = ({
  onSearch,
  onClear,
  isLoading = false,
  initialCriteria = {}
}) => {
  const [isExpanded, setIsExpanded] = useState(false);
  const [criteria, setCriteria] = useState<SearchCriteria>(initialCriteria);

  // Initialiser les critères quand les props changent
  useEffect(() => {
    setCriteria(initialCriteria);
  }, [initialCriteria]);

  const handleInputChange = (field: keyof SearchCriteria, value: any) => {
    setCriteria(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleMultiSelectChange = (field: 'statuts' | 'modes', value: string) => {
    setCriteria(prev => {
      const currentArray = prev[field] || [];
      const updatedArray = currentArray.includes(value)
        ? currentArray.filter(item => item !== value)
        : [...currentArray, value];
      
      return {
        ...prev,
        [field]: updatedArray.length > 0 ? updatedArray : undefined
      };
    });
  };

  const handleSearch = () => {
    onSearch(criteria);
  };

  const handleClear = () => {
    const clearedCriteria: SearchCriteria = {
      sortBy: 'dateHeure',
      sortDirection: 'DESC',
      includeHistory: false
    };
    setCriteria(clearedCriteria);
    onClear();
  };

  const hasActiveFilters = () => {
    return Object.keys(criteria).some(key => {
      const value = criteria[key as keyof SearchCriteria];
      if (key === 'sortBy' || key === 'sortDirection' || key === 'includeHistory') return false;
      return value !== undefined && value !== '' && 
             (Array.isArray(value) ? value.length > 0 : true);
    });
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 mb-6">
      {/* Header avec bouton d'expansion */}
      <div className="flex items-center justify-between p-4 border-b border-gray-200">
        <div className="flex items-center space-x-3">
          <h3 className="text-lg font-semibold text-gray-900">
            🔍 Recherche et filtrage
          </h3>
          {hasActiveFilters() && (
            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
              Filtres actifs
            </span>
          )}
        </div>
        <button
          onClick={() => setIsExpanded(!isExpanded)}
          className="flex items-center space-x-2 px-3 py-1.5 text-gray-600 hover:text-gray-900 rounded-md hover:bg-gray-50"
        >
          <span>{isExpanded ? 'Réduire' : 'Développer'}</span>
          <svg
            className={`w-5 h-5 transform transition-transform ${isExpanded ? 'rotate-180' : ''}`}
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
          </svg>
        </button>
      </div>

      {/* Recherche textuelle toujours visible */}
      <div className="p-4 bg-gray-50">
        <div className="flex space-x-3">
          <div className="flex-1">
            <input
              type="text"
              placeholder="Rechercher dans titre, description, indications..."
              value={criteria.searchTerm || ''}
              onChange={(e) => handleInputChange('searchTerm', e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
            />
          </div>
          <button
            onClick={handleSearch}
            disabled={isLoading}
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isLoading ? 'Recherche...' : 'Rechercher'}
          </button>
          <button
            onClick={handleClear}
            className="px-4 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-gray-500"
          >
            Effacer
          </button>
        </div>
      </div>

      {/* Filtres avancés (collapsibles) */}
      {isExpanded && (
        <div className="p-4 space-y-4 border-t border-gray-200">
          {/* Ligne 1: Client et Collaborateur */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Client
              </label>
              <input
                type="text"
                placeholder="Nom du client"
                value={criteria.clientNom || ''}
                onChange={(e) => handleInputChange('clientNom', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Collaborateur
              </label>
              <input
                type="text"
                placeholder="Nom du collaborateur"
                value={criteria.collaborateurNom || ''}
                onChange={(e) => handleInputChange('collaborateurNom', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>

          {/* Ligne 2: Projet et Dates */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Projet
              </label>
              <input
                type="text"
                placeholder="Nom du projet"
                value={criteria.projetNom || ''}
                onChange={(e) => handleInputChange('projetNom', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Date début
              </label>
              <input
                type="datetime-local"
                value={criteria.dateDebut || ''}
                onChange={(e) => handleInputChange('dateDebut', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Date fin
              </label>
              <input
                type="datetime-local"
                value={criteria.dateFin || ''}
                onChange={(e) => handleInputChange('dateFin', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>
          </div>

          {/* Ligne 3: Statuts (checkboxes) */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Statuts
            </label>
            <div className="flex flex-wrap gap-2">
              {STATUT_OPTIONS.map(option => (
                <label key={option.value} className="inline-flex items-center">
                  <input
                    type="checkbox"
                    checked={(criteria.statuts || []).includes(option.value)}
                    onChange={() => handleMultiSelectChange('statuts', option.value)}
                    className="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
                  />
                  <span className="ml-2 text-sm text-gray-700">{option.label}</span>
                </label>
              ))}
            </div>
          </div>

          {/* Ligne 4: Modes (checkboxes) */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Modes
            </label>
            <div className="flex flex-wrap gap-2">
              {MODE_OPTIONS.map(option => (
                <label key={option.value} className="inline-flex items-center">
                  <input
                    type="checkbox"
                    checked={(criteria.modes || []).includes(option.value)}
                    onChange={() => handleMultiSelectChange('modes', option.value)}
                    className="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
                  />
                  <span className="ml-2 text-sm text-gray-700">{option.label}</span>
                </label>
              ))}
            </div>
          </div>

          {/* Ligne 5: Options et Tri */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="inline-flex items-center">
                <input
                  type="checkbox"
                  checked={criteria.includeHistory || false}
                  onChange={(e) => handleInputChange('includeHistory', e.target.checked)}
                  className="rounded border-gray-300 text-blue-600 shadow-sm focus:border-blue-300 focus:ring focus:ring-blue-200 focus:ring-opacity-50"
                />
                <span className="ml-2 text-sm text-gray-700">Inclure l'historique</span>
              </label>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Trier par
              </label>
              <select
                value={criteria.sortBy || 'dateHeure'}
                onChange={(e) => handleInputChange('sortBy', e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                {SORT_OPTIONS.map(option => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Direction
              </label>
              <select
                value={criteria.sortDirection || 'DESC'}
                onChange={(e) => handleInputChange('sortDirection', e.target.value as 'ASC' | 'DESC')}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="DESC">Décroissant</option>
                <option value="ASC">Croissant</option>
              </select>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};