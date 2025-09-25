/**
 * Composant ExternalActionsPanel
 * Affiche un panneau d'actions rapides pour les intégrations externes (email, GPS, calendrier)
 * et la gestion des bilans post-entretien (TM-38)
 */

import React, { useState } from 'react';
import { RDQ } from '../types';
import { ExternalIntegrationService, ExternalActionData } from '../services/ExternalIntegrationService';
import { Button } from './ui/button';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './ui/tabs';
import { 
  Mail, 
  MapPin, 
  Calendar, 
  ExternalLink,
  AlertTriangle,
  ClipboardList
} from 'lucide-react';
import BilanForm from './BilanForm';
import BilanDisplay from './BilanDisplay';
import { useBilans } from '../hooks/useBilans';
import { Bilan } from '../types';

interface ExternalActionsPanelProps {
  rdq: RDQ;
  className?: string;
}

interface ActionButtonProps {
  action: ExternalActionData;
  rdq: RDQ;
  icon: React.ReactNode;
  label: string;
}

const ActionButton: React.FC<ActionButtonProps> = ({ action, rdq, icon, label }) => {
  const handleClick = () => {
    ExternalIntegrationService.handleExternalAction(action, rdq);
  };

  return (
    <Button
      variant={action.enabled ? "default" : "secondary"}
      size="sm"
      className={`flex items-center space-x-2 ${
        action.enabled 
          ? 'hover:bg-blue-600 transition-colors' 
          : 'opacity-50 cursor-not-allowed'
      }`}
      onClick={handleClick}
      disabled={!action.enabled}
      title={action.tooltip}
    >
      {icon}
      <span className="hidden sm:inline">{label}</span>
      {action.enabled && <ExternalLink className="h-3 w-3 ml-1" />}
    </Button>
  );
};

export const ExternalActionsPanel: React.FC<ExternalActionsPanelProps> = ({ 
  rdq, 
  className = "" 
}) => {
  const [showBilanForm, setShowBilanForm] = useState(false);
  const { bilans, loading, error, addBilan, updateBilan, removeBilan, setError } = useBilans(rdq.idRdq);
  
  const actions = ExternalIntegrationService.generateExternalActions(rdq);
  const validation = ExternalIntegrationService.validateRdqForExternalActions(rdq);
  
  const emailAction = actions.find(a => a.type === 'email');
  const mapsAction = actions.find(a => a.type === 'maps');
  const calendarAction = actions.find(a => a.type === 'calendar');

  const hasAnyEnabledAction = actions.some(action => action.enabled);

  const handleBilanCreated = (newBilan: Bilan) => {
    addBilan(newBilan);
    setShowBilanForm(false);
  };

  const handleBilanUpdated = (updatedBilan: Bilan) => {
    updateBilan(updatedBilan);
  };

  const handleBilanDeleted = (bilanId: number) => {
    removeBilan(bilanId);
  };

  return (
    <Card className={`${className}`}>
      <CardHeader className="pb-3">
        <CardTitle className="flex items-center text-lg">
          <ExternalLink className="h-5 w-5 mr-2" />
          Actions & Bilans
        </CardTitle>
        <p className="text-sm text-gray-600">
          Intégrations externes et gestion des bilans post-entretien
        </p>
      </CardHeader>
      
      <CardContent>
        <Tabs defaultValue="actions" className="w-full">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="actions">Actions rapides</TabsTrigger>
            <TabsTrigger value="bilans" className="flex items-center gap-2">
              <ClipboardList className="h-4 w-4" />
              Bilans ({bilans.length})
            </TabsTrigger>
          </TabsList>

          <TabsContent value="actions" className="space-y-4 mt-4">
            {/* Boutons d'actions */}
            <div className="flex flex-wrap gap-3">
              {emailAction && (
                <ActionButton
                  action={emailAction}
                  rdq={rdq}
                  icon={<Mail className="h-4 w-4" />}
                  label="Email"
                />
              )}
              
              {mapsAction && (
                <ActionButton
                  action={mapsAction}
                  rdq={rdq}
                  icon={<MapPin className="h-4 w-4" />}
                  label="Maps"
                />
              )}
              
              {calendarAction && (
                <ActionButton
                  action={calendarAction}
                  rdq={rdq}
                  icon={<Calendar className="h-4 w-4" />}
                  label="Calendrier"
                />
              )}
            </div>

            {/* Avertissements si aucune action disponible */}
            {!hasAnyEnabledAction && (
              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-3">
                <div className="flex items-start space-x-2">
                  <AlertTriangle className="h-4 w-4 text-yellow-600 mt-0.5 flex-shrink-0" />
                  <div className="text-sm">
                    <p className="font-medium text-yellow-800 mb-1">
                      Aucune intégration externe disponible
                    </p>
                    <p className="text-yellow-700">
                      Certaines informations sont manquantes pour activer les intégrations.
                    </p>
                  </div>
                </div>
              </div>
            )}

            {/* Détails des limitations */}
            {validation.warnings.length > 0 && hasAnyEnabledAction && (
              <div className="bg-blue-50 border border-blue-200 rounded-lg p-3">
                <div className="flex items-start space-x-2">
                  <AlertTriangle className="h-4 w-4 text-blue-600 mt-0.5 flex-shrink-0" />
                  <div className="text-sm">
                    <p className="font-medium text-blue-800 mb-1">
                      Informations complémentaires
                    </p>
                    <ul className="text-blue-700 space-y-1">
                      {validation.warnings.map((warning, index) => (
                        <li key={index} className="text-xs">• {warning}</li>
                      ))}
                    </ul>
                  </div>
                </div>
              </div>
            )}

            {/* Détails des actions disponibles */}
            {hasAnyEnabledAction && (
              <div className="bg-gray-50 rounded-lg p-3">
                <h4 className="text-sm font-medium text-gray-800 mb-2">
                  Actions disponibles :
                </h4>
                <div className="space-y-1 text-xs text-gray-600">
                  {emailAction?.enabled && (
                    <div className="flex items-center space-x-2">
                      <Mail className="h-3 w-3" />
                      <span>Email vers {rdq.manager?.email}</span>
                    </div>
                  )}
                  {mapsAction?.enabled && (
                    <div className="flex items-center space-x-2">
                      <MapPin className="h-3 w-3" />
                      <span>Navigation vers {rdq.adresse}</span>
                    </div>
                  )}
                  {calendarAction?.enabled && (
                    <div className="flex items-center space-x-2">
                      <Calendar className="h-3 w-3" />
                      <span>Événement le {new Date(rdq.dateHeure).toLocaleDateString('fr-FR')}</span>
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* Note sur l'intégration téléphone */}
            <div className="bg-amber-50 border border-amber-200 rounded-lg p-3">
              <div className="flex items-start space-x-2">
                <AlertTriangle className="h-4 w-4 text-amber-600 mt-0.5 flex-shrink-0" />
                <div className="text-sm">
                  <p className="font-medium text-amber-800 mb-1">
                    📞 Intégration téléphone
                  </p>
                  <p className="text-amber-700 text-xs">
                    L'intégration directe avec l'application téléphone n'est pas possible 
                    depuis une application web pour des raisons de sécurité. 
                    <br />
                    <strong>Solution mobile à venir</strong> : Une application mobile native 
                    permettra cette fonctionnalité.
                  </p>
                </div>
              </div>
            </div>
          </TabsContent>

          <TabsContent value="bilans" className="space-y-4 mt-4">
            {/* Bouton pour afficher/masquer le formulaire */}
            <div className="flex justify-between items-center">
              <h3 className="text-lg font-medium">Bilans post-entretien</h3>
              <Button 
                onClick={() => setShowBilanForm(!showBilanForm)}
                variant={showBilanForm ? "outline" : "default"}
                size="sm"
              >
                {showBilanForm ? 'Annuler' : 'Nouveau bilan'}
              </Button>
            </div>

            {/* Formulaire de création */}
            {showBilanForm && (
              <BilanForm
                rdqId={rdq.idRdq}
                onBilanCreated={handleBilanCreated}
                onCancel={() => setShowBilanForm(false)}
              />
            )}

            {/* Affichage des bilans */}
            <BilanDisplay
              rdqId={rdq.idRdq}
              bilans={bilans}
              onBilanUpdated={handleBilanUpdated}
              onBilanDeleted={handleBilanDeleted}
              loading={loading}
            />

            {/* Affichage des erreurs */}
            {error && (
              <div className="bg-red-50 border border-red-200 rounded-lg p-3">
                <div className="flex items-start space-x-2">
                  <AlertTriangle className="h-4 w-4 text-red-600 mt-0.5 flex-shrink-0" />
                  <div className="text-sm">
                    <p className="font-medium text-red-800 mb-1">
                      Erreur lors du chargement des bilans
                    </p>
                    <p className="text-red-700">{error}</p>
                    <Button 
                      variant="outline" 
                      size="sm" 
                      className="mt-2"
                      onClick={() => setError(null)}
                    >
                      Fermer
                    </Button>
                  </div>
                </div>
              </div>
            )}
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  );
};

export default ExternalActionsPanel;