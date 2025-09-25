import React, { useState } from 'react';
import { Alert, AlertDescription } from './ui/alert';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Separator } from './ui/separator';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './ui/tabs';
import { 
  AlertTriangle, 
  Star, 
  User, 
  Calendar, 
  MessageSquare, 
  BarChart3,
  Edit,
  Trash2
} from 'lucide-react';
import { Bilan, TypeAuteur } from '../types';

interface BilanDisplayProps {
  rdqId: number;
  bilans: Bilan[];
  onBilanUpdated?: (bilan: Bilan) => void;
  onBilanDeleted?: (bilanId: number) => void;
  loading?: boolean;
}

interface BilanStats {
  noteMoyenneGlobale: number;
  noteMoyenneManager: number;
  noteMoyenneCollaborateur: number;
  aDesBilans: boolean;
}

/**
 * Composant d'affichage des bilans post-entretien (TM-38)
 * Affiche la liste des bilans avec filtrage par type d'auteur et statistiques
 */
const BilanDisplay: React.FC<BilanDisplayProps> = ({ 
  rdqId, 
  bilans, 
  onBilanUpdated, 
  onBilanDeleted, 
  loading = false 
}) => {
  const [stats, setStats] = useState<BilanStats | null>(null);
  const [statsLoading, setStatsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Filtrer les bilans par type d'auteur
  const bilansByType = {
    all: bilans,
    manager: bilans.filter(b => b.typeAuteur === 'MANAGER'),
    collaborateur: bilans.filter(b => b.typeAuteur === 'COLLABORATEUR')
  };

  // Charger les statistiques
  const loadStats = async () => {
    if (statsLoading || !bilans.length) return;

    setStatsLoading(true);
    setError(null);

    try {
      const response = await fetch(`/api/rdqs/${rdqId}/bilans/stats`);
      if (!response.ok) {
        throw new Error('Erreur lors du chargement des statistiques');
      }
      const statsData: BilanStats = await response.json();
      setStats(statsData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Erreur lors du chargement des statistiques');
    } finally {
      setStatsLoading(false);
    }
  };

  // Calculer la couleur du badge selon la note
  const getNoteVariant = (note: number): "default" | "secondary" | "destructive" | "outline" => {
    if (note >= 8) return "default"; // Vert pour excellent
    if (note >= 6) return "secondary"; // Bleu pour bon
    if (note >= 4) return "outline"; // Gris pour moyen
    return "destructive"; // Rouge pour faible
  };

  // Formater la date
  const formatDate = (dateString: string): string => {
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
      return dateString;
    }
  };

  // Composant d'un bilan individuel
  const BilanCard: React.FC<{ bilan: Bilan }> = ({ bilan }) => (
    <Card className="mb-3">
      <CardContent className="pt-4">
        <div className="flex justify-between items-start mb-3">
          <div className="flex items-center gap-2">
            <Badge variant={getNoteVariant(bilan.note)} className="text-sm">
              <Star className="h-3 w-3 mr-1" />
              {bilan.note}/10
            </Badge>
            <Badge variant="outline">
              {bilan.typeAuteur === 'MANAGER' ? 'Manager' : 'Collaborateur'}
            </Badge>
          </div>
          <div className="flex gap-1">
            {onBilanUpdated && (
              <Button variant="ghost" size="sm">
                <Edit className="h-4 w-4" />
              </Button>
            )}
            {onBilanDeleted && (
              <Button variant="ghost" size="sm">
                <Trash2 className="h-4 w-4" />
              </Button>
            )}
          </div>
        </div>

        <div className="space-y-2">
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            <User className="h-4 w-4" />
            <span>{bilan.auteur}</span>
          </div>
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            <Calendar className="h-4 w-4" />
            <span>{formatDate(bilan.dateCreation)}</span>
          </div>
        </div>

        {bilan.commentaire && (
          <>
            <Separator className="my-3" />
            <div className="space-y-2">
              <div className="flex items-center gap-2 text-sm font-medium">
                <MessageSquare className="h-4 w-4" />
                Commentaire
              </div>
              <p className="text-sm text-muted-foreground leading-relaxed">
                {bilan.commentaire}
              </p>
            </div>
          </>
        )}
      </CardContent>
    </Card>
  );

  // Composant des statistiques
  const StatsCard: React.FC = () => (
    <Card className="mb-4">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <BarChart3 className="h-5 w-5" />
          Statistiques des évaluations
          <Button 
            variant="ghost" 
            size="sm" 
            onClick={loadStats}
            disabled={statsLoading}
          >
            {statsLoading ? 'Chargement...' : 'Actualiser'}
          </Button>
        </CardTitle>
      </CardHeader>
      <CardContent>
        {stats ? (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="text-center p-3 bg-muted rounded-lg">
              <div className="text-2xl font-bold">{stats.noteMoyenneGlobale.toFixed(1)}</div>
              <div className="text-sm text-muted-foreground">Note moyenne globale</div>
            </div>
            <div className="text-center p-3 bg-muted rounded-lg">
              <div className="text-2xl font-bold">{stats.noteMoyenneManager.toFixed(1)}</div>
              <div className="text-sm text-muted-foreground">Moyenne Manager</div>
            </div>
            <div className="text-center p-3 bg-muted rounded-lg">
              <div className="text-2xl font-bold">{stats.noteMoyenneCollaborateur.toFixed(1)}</div>
              <div className="text-sm text-muted-foreground">Moyenne Collaborateur</div>
            </div>
          </div>
        ) : (
          <p className="text-muted-foreground text-center">
            Cliquez sur "Actualiser" pour charger les statistiques
          </p>
        )}
      </CardContent>
    </Card>
  );

  if (loading) {
    return (
      <Card>
        <CardContent className="text-center py-8">
          <p className="text-muted-foreground">Chargement des bilans...</p>
        </CardContent>
      </Card>
    );
  }

  if (!bilans.length) {
    return (
      <Card>
        <CardContent className="text-center py-8">
          <MessageSquare className="h-12 w-12 mx-auto mb-4 text-muted-foreground" />
          <p className="text-muted-foreground">Aucun bilan disponible pour ce RDQ</p>
          <p className="text-sm text-muted-foreground mt-2">
            Les bilans post-entretien apparaîtront ici une fois créés
          </p>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      {error && (
        <Alert variant="destructive">
          <AlertTriangle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {bilans.length > 0 && <StatsCard />}

      <Tabs defaultValue="all" className="w-full">
        <TabsList className="grid w-full grid-cols-3">
          <TabsTrigger value="all">
            Tous ({bilansByType.all.length})
          </TabsTrigger>
          <TabsTrigger value="manager">
            Manager ({bilansByType.manager.length})
          </TabsTrigger>
          <TabsTrigger value="collaborateur">
            Collaborateur ({bilansByType.collaborateur.length})
          </TabsTrigger>
        </TabsList>

        <TabsContent value="all" className="space-y-4">
          {bilansByType.all.map(bilan => (
            <BilanCard key={bilan.idBilan} bilan={bilan} />
          ))}
        </TabsContent>

        <TabsContent value="manager" className="space-y-4">
          {bilansByType.manager.length > 0 ? (
            bilansByType.manager.map(bilan => (
              <BilanCard key={bilan.idBilan} bilan={bilan} />
            ))
          ) : (
            <Card>
              <CardContent className="text-center py-8">
                <p className="text-muted-foreground">Aucun bilan manager disponible</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>

        <TabsContent value="collaborateur" className="space-y-4">
          {bilansByType.collaborateur.length > 0 ? (
            bilansByType.collaborateur.map(bilan => (
              <BilanCard key={bilan.idBilan} bilan={bilan} />
            ))
          ) : (
            <Card>
              <CardContent className="text-center py-8">
                <p className="text-muted-foreground">Aucun bilan collaborateur disponible</p>
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default BilanDisplay;