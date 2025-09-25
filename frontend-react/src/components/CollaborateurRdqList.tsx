/**
 * Composant de liste des RDQ pour le collaborateur
 * Utilise l'API REST pour récupérer les données
 */

import React, { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { useRdqs } from '../hooks/useRdqs';
import { RdqStatusUtils } from '../services/rdqApi';
import { Input } from './ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Checkbox } from './ui/checkbox';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Search, RefreshCw, Calendar, MapPin, User, FileText, AlertCircle } from 'lucide-react';
import { RDQ } from '../types';

export const CollaborateurRdqList: React.FC = () => {
  const {
    filteredRdqs,
    loading,
    error,
    stats,
    searchTerm,
    setSearchTerm,
    showHistory,
    setShowHistory,
    statusFilter,
    setStatusFilter,
    refreshRdqs,
  } = useRdqs();

  const [selectedRdq, setSelectedRdq] = useState<RDQ | null>(null);

  const handleRefresh = async () => {
    await refreshRdqs();
  };

  if (error) {
    return (
      <div className="space-y-6">
        <div className="text-center py-8">
          <AlertCircle className="mx-auto h-12 w-12 text-red-500 mb-4" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">Erreur de chargement</h3>
          <p className="text-gray-600 mb-4">{error}</p>
          <Button onClick={handleRefresh}>
            <RefreshCw className="mr-2 h-4 w-4" />
            Réessayer
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* En-tête avec statistiques */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <div className="flex justify-between items-center">
          <motion.div
            initial={{ x: -20, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            transition={{ duration: 0.4, delay: 0.1 }}
          >
            <h2 className="text-2xl font-bold text-gray-900">Mes Rendez-vous Qualifiés</h2>
          </motion.div>
          <Button 
            onClick={handleRefresh} 
            disabled={loading}
            variant="outline"
            size="sm"
          >
            <RefreshCw className={`mr-2 h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
            Actualiser
          </Button>
        </div>
        
        {/* Statistiques */}
        <motion.div
          className="flex flex-wrap gap-2 sm:gap-4 mt-4"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5, delay: 0.2 }}
        >
          <motion.div whileHover={{ scale: 1.05 }}>
            <Badge variant="default">{stats.enCours} En cours</Badge>
          </motion.div>
          <motion.div whileHover={{ scale: 1.05 }}>
            <Badge variant="secondary">{stats.termines} Terminés</Badge>
          </motion.div>
          {stats.annules > 0 && (
            <motion.div whileHover={{ scale: 1.05 }}>
              <Badge variant="destructive">{stats.annules} Annulés</Badge>
            </motion.div>
          )}
          {stats.bilanManquant > 0 && (
            <motion.div 
              whileHover={{ scale: 1.05 }}
              animate={{ x: [0, -2, 2, 0] }}
              transition={{ 
                scale: { type: "spring", stiffness: 400 },
                x: { repeat: Infinity, duration: 1.5, repeatDelay: 3 }
              }}
            >
              <Badge variant="destructive">
                {stats.bilanManquant} bilan(s) à compléter
              </Badge>
            </motion.div>
          )}
        </motion.div>
      </motion.div>

      {/* Filtres */}
      <motion.div
        className="flex flex-col sm:flex-row gap-4 p-4 bg-gray-50 rounded-lg"
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4, delay: 0.3 }}
      >
        {/* Recherche */}
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
          <Input
            placeholder="Rechercher par titre, projet..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="pl-10"
          />
        </div>

        {/* Filtre par statut */}
        <Select value={statusFilter} onValueChange={setStatusFilter}>
          <SelectTrigger className="w-full sm:w-48">
            <SelectValue placeholder="Filtrer par statut" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">Tous les statuts</SelectItem>
            <SelectItem value="PLANIFIE">Planifiés</SelectItem>
            <SelectItem value="EN_COURS">En cours</SelectItem>
            <SelectItem value="TERMINE">Terminés</SelectItem>
            <SelectItem value="ANNULE">Annulés</SelectItem>
          </SelectContent>
        </Select>

        {/* Inclure historique */}
        <div className="flex items-center space-x-2">
          <Checkbox
            id="show-history"
            checked={showHistory}
            onCheckedChange={setShowHistory}
          />
          <label htmlFor="show-history" className="text-sm font-medium">
            Afficher l'historique
          </label>
        </div>
      </motion.div>

      {/* Liste des RDQ */}
      <motion.div
        className="space-y-4"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5, delay: 0.4 }}
      >
        {loading ? (
          <div className="text-center py-8">
            <RefreshCw className="mx-auto h-8 w-8 animate-spin text-blue-500 mb-4" />
            <p className="text-gray-600">Chargement des RDQ...</p>
          </div>
        ) : filteredRdqs.length === 0 ? (
          <div className="text-center py-8">
            <Calendar className="mx-auto h-12 w-12 text-gray-400 mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">Aucun RDQ trouvé</h3>
            <p className="text-gray-600">
              {searchTerm || statusFilter !== 'all' ? 
                'Aucun RDQ ne correspond à vos critères de recherche.' :
                'Vous n\'avez pas encore de RDQ assignés.'}
            </p>
          </div>
        ) : (
          <AnimatePresence>
            {filteredRdqs.map((rdq, index) => (
              <RdqCard
                key={rdq.idRdq}
                rdq={rdq}
                index={index}
                onClick={() => setSelectedRdq(rdq)}
              />
            ))}
          </AnimatePresence>
        )}
      </motion.div>
    </div>
  );
};

interface RdqCardProps {
  rdq: RDQ;
  index: number;
  onClick: () => void;
}

const RdqCard: React.FC<RdqCardProps> = ({ rdq, index, onClick }) => {
  const dateTime = new Date(rdq.dateHeure);
  const isUpcoming = dateTime > new Date();
  const isPassed = dateTime < new Date();

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      transition={{ duration: 0.3, delay: index * 0.1 }}
      whileHover={{ scale: 1.02 }}
      className="cursor-pointer"
      onClick={onClick}
    >
      <Card className="hover:shadow-lg transition-shadow duration-200">
        <CardHeader className="pb-3">
          <div className="flex justify-between items-start">
            <div className="flex-1">
              <CardTitle className="text-lg mb-1">{rdq.titre}</CardTitle>
              <CardDescription className="flex items-center gap-2">
                <User className="h-4 w-4" />
                {rdq.manager?.nom || 'Manager non défini'}
              </CardDescription>
            </div>
            <Badge variant={RdqStatusUtils.getStatusBadgeVariant(rdq.statut || '')}>
              {RdqStatusUtils.translateStatus(rdq.statut || '')}
            </Badge>
          </div>
        </CardHeader>
        
        <CardContent className="pt-0">
          <div className="space-y-2">
            {/* Date et heure */}
            <div className="flex items-center gap-2 text-sm">
              <Calendar className="h-4 w-4 text-gray-500" />
              <span className={isUpcoming ? 'text-green-600 font-medium' : isPassed ? 'text-gray-500' : ''}>
                {dateTime.toLocaleDateString('fr-FR', {
                  weekday: 'long',
                  year: 'numeric',
                  month: 'long',
                  day: 'numeric',
                  hour: '2-digit',
                  minute: '2-digit'
                })}
              </span>
            </div>

            {/* Adresse */}
            {rdq.adresse && (
              <div className="flex items-center gap-2 text-sm text-gray-600">
                <MapPin className="h-4 w-4 text-gray-500" />
                <span>{rdq.adresse}</span>
              </div>
            )}

            {/* Projet */}
            {rdq.projet && (
              <div className="flex items-center gap-2 text-sm text-gray-600">
                <FileText className="h-4 w-4 text-gray-500" />
                <span>{rdq.projet.nom}</span>
                {rdq.projet.client && <span className="text-gray-400">• {rdq.projet.client}</span>}
              </div>
            )}

            {/* Mode */}
            <div className="flex items-center gap-2">
              <Badge variant="outline" className="text-xs">
                {rdq.mode === 'PRESENTIEL' ? '📍 Présentiel' :
                 rdq.mode === 'DISTANCIEL' ? '💻 Distanciel' :
                 rdq.mode === 'HYBRIDE' ? '🔄 Hybride' : rdq.mode}
              </Badge>
              
              {rdq.documents && rdq.documents.length > 0 && (
                <Badge variant="outline" className="text-xs">
                  📎 {rdq.documents.length} document(s)
                </Badge>
              )}
            </div>
          </div>
        </CardContent>
      </Card>
    </motion.div>
  );
};

export default CollaborateurRdqList;