/**
 * Composant de détail d'un RDQ pour le collaborateur
 * Affiche toutes les informations d'un RDQ avec accès aux pièces jointes
 */

import React from 'react';
import { useRdq } from '../hooks/useRdqs';
import { RdqStatusUtils } from '../services/rdqApi';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Separator } from './ui/separator';
import { ExternalActionsPanel } from './ExternalActionsPanel';
import { RdqClotureControls } from './RdqClotureControls';
import { 
  Calendar, 
  MapPin, 
  User, 
  Building, 
  FileText, 
  Download, 
  Eye, 
  ArrowLeft,
  Clock,
  Mail,
  Phone,
  AlertCircle,
  RefreshCw
} from 'lucide-react';
import { RDQ } from '../types';

interface RdqDetailProps {
  rdqId: number;
  onBack?: () => void;
}

export const RdqDetail: React.FC<RdqDetailProps> = ({ rdqId, onBack }) => {
  const { rdq, loading, error, refresh } = useRdq(rdqId);

  if (loading) {
    return (
      <div className="text-center py-8">
        <RefreshCw className="mx-auto h-8 w-8 animate-spin text-blue-500 mb-4" />
        <p className="text-gray-600">Chargement du RDQ...</p>
      </div>
    );
  }

  if (error || !rdq) {
    return (
      <div className="text-center py-8">
        <AlertCircle className="mx-auto h-12 w-12 text-red-500 mb-4" />
        <h3 className="text-lg font-medium text-gray-900 mb-2">Erreur de chargement</h3>
        <p className="text-gray-600 mb-4">{error || 'RDQ non trouvé'}</p>
        <div className="space-x-2">
          <Button onClick={refresh} variant="outline">
            <RefreshCw className="mr-2 h-4 w-4" />
            Réessayer
          </Button>
          {onBack && (
            <Button onClick={onBack} variant="secondary">
              <ArrowLeft className="mr-2 h-4 w-4" />
              Retour
            </Button>
          )}
        </div>
      </div>
    );
  }

  const dateTime = new Date(rdq.dateHeure);
  const isUpcoming = dateTime > new Date();
  const isPassed = dateTime < new Date();
  const canModify = rdq.statut === 'PLANIFIE' || rdq.statut === 'EN_COURS';
  const isClosed = rdq.statut === 'CLOS';

  return (
    <div className="space-y-6">
      {/* En-tête */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          {onBack && (
            <Button onClick={onBack} variant="ghost" size="sm">
              <ArrowLeft className="h-4 w-4" />
            </Button>
          )}
          <div>
            <h1 className="text-2xl font-bold text-gray-900">{rdq.titre}</h1>
            <p className="text-gray-600">RDQ #{rdq.idRdq}</p>
          </div>
        </div>
        <Badge variant={RdqStatusUtils.getStatusBadgeVariant(rdq.statut || '')} className="text-sm">
          {RdqStatusUtils.translateStatus(rdq.statut || '')}
        </Badge>
      </div>

      {/* Informations principales */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Détails du RDQ */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center">
              <Calendar className="h-5 w-5 mr-2" />
              Détails du rendez-vous
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* Date et heure */}
            <div className="flex items-start space-x-3">
              <Clock className="h-5 w-5 text-gray-500 mt-0.5" />
              <div>
                <p className={`font-medium ${isUpcoming ? 'text-green-600' : isPassed ? 'text-gray-500' : ''}`}>
                  {dateTime.toLocaleDateString('fr-FR', {
                    weekday: 'long',
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric'
                  })}
                </p>
                <p className="text-sm text-gray-600">
                  {dateTime.toLocaleTimeString('fr-FR', {
                    hour: '2-digit',
                    minute: '2-digit'
                  })}
                </p>
              </div>
            </div>

            {/* Mode */}
            <div className="flex items-center space-x-3">
              <div className="h-5 w-5 flex items-center justify-center">
                {rdq.mode === 'PRESENTIEL' ? '📍' :
                 rdq.mode === 'DISTANCIEL' ? '💻' :
                 rdq.mode === 'HYBRIDE' ? '🔄' : '📍'}
              </div>
              <div>
                <p className="font-medium">
                  {rdq.mode === 'PRESENTIEL' ? 'Présentiel' :
                   rdq.mode === 'DISTANCIEL' ? 'Distanciel' :
                   rdq.mode === 'HYBRIDE' ? 'Hybride' : rdq.mode}
                </p>
              </div>
            </div>

            {/* Adresse */}
            {rdq.adresse && (
              <div className="flex items-start space-x-3">
                <MapPin className="h-5 w-5 text-gray-500 mt-0.5" />
                <div>
                  <p className="font-medium">Adresse</p>
                  <p className="text-sm text-gray-600">{rdq.adresse}</p>
                </div>
              </div>
            )}

            {/* Description */}
            {rdq.description && (
              <div className="pt-2">
                <h4 className="font-medium mb-2">Description</h4>
                <p className="text-sm text-gray-600 whitespace-pre-wrap">
                  {rdq.description}
                </p>
              </div>
            )}

            {/* Indications */}
            {rdq.indications && (
              <div className="pt-2">
                <h4 className="font-medium mb-2">Indications du manager</h4>
                <p className="text-sm text-gray-600 whitespace-pre-wrap">
                  {rdq.indications}
                </p>
              </div>
            )}
          </CardContent>
        </Card>

        {/* Intervenants */}
        <div className="space-y-6">
          {/* Manager */}
          {rdq.manager && (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <User className="h-5 w-5 mr-2" />
                  Manager responsable
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  <p className="font-medium">{rdq.manager.nom}</p>
                  <div className="flex items-center space-x-2 text-sm text-gray-600">
                    <Mail className="h-4 w-4" />
                    <a href={`mailto:${rdq.manager.email}`} className="hover:text-blue-600">
                      {rdq.manager.email}
                    </a>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {/* Projet */}
          {rdq.projet && (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center">
                  <Building className="h-5 w-5 mr-2" />
                  Projet concerné
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  <p className="font-medium">{rdq.projet.nom}</p>
                  {rdq.projet.client && (
                    <p className="text-sm text-gray-600">Client: {rdq.projet.client}</p>
                  )}
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      </div>

      {/* Pièces jointes */}
      {rdq.documents && rdq.documents.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center">
              <FileText className="h-5 w-5 mr-2" />
              Pièces jointes ({rdq.documents.length})
            </CardTitle>
            <CardDescription>
              Documents et fichiers liés à ce RDQ
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {rdq.documents.map((document) => (
                <DocumentItem 
                  key={document.idDocument || document.nomFichier} 
                  document={document} 
                />
              ))}
            </div>
          </CardContent>
        </Card>
      )}

      {/* Actions externes */}
      <ExternalActionsPanel rdq={rdq} />

      {/* Contrôles de clôture/réouverture */}
      <RdqClotureControls 
        rdq={rdq} 
        onStatusChange={(newStatus) => {
          // Mettre à jour le statut du RDQ et recharger
          refresh();
        }}
        userRole="manager" // TODO: Récupérer le rôle depuis le contexte d'auth
      />

      {/* Actions */}
      {(!canModify || isClosed) && (
        <Card className={isClosed ? "border-gray-300 bg-gray-50" : "border-yellow-200 bg-yellow-50"}>
          <CardContent className="pt-6">
            <div className={`flex items-center space-x-2 ${isClosed ? 'text-gray-700' : 'text-yellow-800'}`}>
              <AlertCircle className="h-5 w-5" />
              <p className="text-sm">
                {rdq.statut === 'CLOS' ? 
                  'Ce RDQ est clôturé et ne peut plus être modifié.' :
                  rdq.statut === 'TERMINE' ? 
                  'Ce RDQ est terminé et ne peut plus être modifié.' :
                  rdq.statut === 'ANNULE' ?
                  'Ce RDQ a été annulé.' :
                  'Ce RDQ ne peut plus être modifié.'}
              </p>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
};

interface DocumentItemProps {
  document: {
    idDocument?: number;
    nomFichier: string;
    type?: string;
    url?: string;
  };
}

const DocumentItem: React.FC<DocumentItemProps> = ({ document }) => {
  const handleView = () => {
    if (document.url) {
      window.open(document.url, '_blank');
    }
  };

  const handleDownload = () => {
    if (document.url) {
      const link = window.document.createElement('a');
      link.href = document.url;
      link.download = document.nomFichier;
      window.document.body.appendChild(link);
      link.click();
      window.document.body.removeChild(link);
    }
  };

  const getFileIcon = (fileName: string) => {
    const extension = fileName.split('.').pop()?.toLowerCase();
    switch (extension) {
      case 'pdf':
        return '📄';
      case 'doc':
      case 'docx':
        return '📝';
      case 'xls':
      case 'xlsx':
        return '📊';
      case 'ppt':
      case 'pptx':
        return '📈';
      case 'jpg':
      case 'jpeg':
      case 'png':
      case 'gif':
        return '🖼️';
      default:
        return '📁';
    }
  };

  return (
    <div className="flex items-center justify-between p-3 border rounded-lg hover:bg-gray-50">
      <div className="flex items-center space-x-3">
        <span className="text-2xl">{getFileIcon(document.nomFichier)}</span>
        <div>
          <p className="font-medium text-sm">{document.nomFichier}</p>
          {document.type && (
            <p className="text-xs text-gray-500 capitalize">{document.type}</p>
          )}
        </div>
      </div>
      
      <div className="flex items-center space-x-2">
        {document.url && (
          <>
            <Button size="sm" variant="ghost" onClick={handleView}>
              <Eye className="h-4 w-4" />
            </Button>
            <Button size="sm" variant="ghost" onClick={handleDownload}>
              <Download className="h-4 w-4" />
            </Button>
          </>
        )}
      </div>
    </div>
  );
};

export default RdqDetail;