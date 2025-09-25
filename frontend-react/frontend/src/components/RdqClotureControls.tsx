/**
 * Composant pour la gestion de la clôture et réouverture des RDQ
 * TM-39 - US07 - Clôture et réouverture des RDQ
 */

import React, { useState } from 'react';
import { Button } from './ui/button';
import { Alert, AlertDescription } from './ui/alert';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { 
  Lock, 
  Unlock, 
  CheckCircle2, 
  AlertCircle, 
  Loader2,
  Shield
} from 'lucide-react';
import { RDQ } from '../types';

interface RdqClotureControlsProps {
  rdq: RDQ;
  onStatusChange: (newStatus: string) => void;
  userRole: 'admin' | 'manager' | 'collaborateur';
}

interface ClotureStatus {
  peutCloture: boolean;
  peutRouvrir: boolean;
}

export const RdqClotureControls: React.FC<RdqClotureControlsProps> = ({
  rdq,
  onStatusChange,
  userRole
}) => {
  const [loading, setLoading] = useState(false);
  const [clotureStatus, setClotureStatus] = useState<ClotureStatus | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Vérifier les permissions (seuls les managers peuvent clôturer/rouvrir)
  const canManageRdq = userRole === 'admin' || userRole === 'manager';

  // Vérifier le statut de clôture
  const checkClotureStatus = async () => {
    try {
      const response = await fetch(`/api/v1/rdq/${rdq.idRdq}/peut-cloturer`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        }
      });

      if (response.ok) {
        const data = await response.json();
        setClotureStatus({
          peutCloture: data.peutCloture,
          peutRouvrir: data.peutRouvrir
        });
      }
    } catch (err) {
      console.error('Erreur lors de la vérification du statut:', err);
    }
  };

  // Initialiser la vérification du statut
  React.useEffect(() => {
    if (rdq.idRdq) {
      checkClotureStatus();
    }
  }, [rdq.idRdq]);

  // Clôturer le RDQ
  const handleCloturer = async () => {
    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      const response = await fetch(`/api/v1/rdq/${rdq.idRdq}/cloturer`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        }
      });

      const data = await response.json();

      if (response.ok) {
        setSuccess('RDQ clôturé avec succès');
        onStatusChange('CLOS');
        await checkClotureStatus(); // Recharger le statut
      } else {
        setError(data.error || 'Erreur lors de la clôture');
      }
    } catch (err) {
      setError('Erreur réseau lors de la clôture');
    } finally {
      setLoading(false);
    }
  };

  // Rouvrir le RDQ
  const handleRouvrir = async () => {
    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      const response = await fetch(`/api/v1/rdq/${rdq.idRdq}/rouvrir`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        }
      });

      const data = await response.json();

      if (response.ok) {
        setSuccess('RDQ rouvert avec succès');
        onStatusChange('EN_COURS');
        await checkClotureStatus(); // Recharger le statut
      } else {
        setError(data.error || 'Erreur lors de la réouverture');
      }
    } catch (err) {
      setError('Erreur réseau lors de la réouverture');
    } finally {
      setLoading(false);
    }
  };

  // Si l'utilisateur n'est pas manager, ne pas afficher les contrôles
  if (!canManageRdq) {
    return null;
  }

  return (
    <Card className="border-dashed">
      <CardHeader className="pb-3">
        <CardTitle className="flex items-center text-sm font-medium">
          <Shield className="mr-2 h-4 w-4" />
          Gestion du cycle de vie
        </CardTitle>
        <CardDescription className="text-xs">
          Clôture et réouverture du RDQ (Manager uniquement)
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-3">
        {/* Messages de feedback */}
        {error && (
          <Alert variant="destructive" className="py-2">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription className="text-sm">{error}</AlertDescription>
          </Alert>
        )}

        {success && (
          <Alert className="py-2 border-green-200 bg-green-50">
            <CheckCircle2 className="h-4 w-4 text-green-600" />
            <AlertDescription className="text-sm text-green-800">{success}</AlertDescription>
          </Alert>
        )}

        {/* Indicateur visuel pour RDQ clos */}
        {rdq.statut === 'CLOS' && (
          <Alert className="py-2 border-gray-200 bg-gray-50">
            <Lock className="h-4 w-4 text-gray-600" />
            <AlertDescription className="text-sm text-gray-700">
              Ce RDQ est <strong>clôturé</strong> et n'est plus modifiable
            </AlertDescription>
          </Alert>
        )}

        {/* Boutons d'action */}
        <div className="flex space-x-2">
          {/* Bouton Clôturer */}
          {clotureStatus?.peutCloture && rdq.statut !== 'CLOS' && (
            <Button
              onClick={handleCloturer}
              disabled={loading}
              size="sm"
              className="bg-red-600 hover:bg-red-700 text-white"
            >
              {loading ? (
                <Loader2 className="mr-2 h-3 w-3 animate-spin" />
              ) : (
                <Lock className="mr-2 h-3 w-3" />
              )}
              Clôturer
            </Button>
          )}

          {/* Bouton Rouvrir */}
          {clotureStatus?.peutRouvrir && rdq.statut === 'CLOS' && (
            <Button
              onClick={handleRouvrir}
              disabled={loading}
              size="sm"
              variant="outline"
              className="border-blue-200 text-blue-700 hover:bg-blue-50"
            >
              {loading ? (
                <Loader2 className="mr-2 h-3 w-3 animate-spin" />
              ) : (
                <Unlock className="mr-2 h-3 w-3" />
              )}
              Rouvrir
            </Button>
          )}
        </div>

        {/* Messages d'information */}
        {clotureStatus && !clotureStatus.peutCloture && rdq.statut !== 'CLOS' && (
          <Alert variant="destructive" className="py-2">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription className="text-sm">
              Les deux bilans (manager et collaborateur) sont requis pour clôturer ce RDQ
            </AlertDescription>
          </Alert>
        )}

        {/* Légende des statuts */}
        <div className="text-xs text-gray-500 pt-2 border-t">
          <p><strong>Clôturé</strong> : RDQ finalisé avec les deux bilans</p>
          <p><strong>Rouvert</strong> : RDQ redevient modifiable</p>
        </div>
      </CardContent>
    </Card>
  );
};