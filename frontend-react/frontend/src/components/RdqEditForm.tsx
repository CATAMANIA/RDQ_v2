import React, { useState, useEffect } from 'react';
import { RDQ, Projet, CollaborateurInfo } from '../types';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Textarea } from './ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Alert, AlertDescription } from './ui/alert';
import { Save, X, Calendar, Clock, MapPin, Users, Building, AlertCircle } from 'lucide-react';

interface RdqEditFormProps {
  rdq: RDQ;
  projets: Projet[];
  collaborateurs: CollaborateurInfo[];
  onSave: (updatedRdq: Partial<RDQ>) => Promise<void>;
  onCancel: () => void;
  isLoading?: boolean;
}

export const RdqEditForm: React.FC<RdqEditFormProps> = ({
  rdq,
  projets,
  collaborateurs,
  onSave,
  onCancel,
  isLoading = false
}) => {
  // États du formulaire
  const [formData, setFormData] = useState({
    titre: rdq.titre || '',
    dateHeure: rdq.dateHeure ? rdq.dateHeure.substring(0, 16) : '', // Format datetime-local
    adresse: rdq.adresse || '',
    mode: rdq.mode || 'PRESENTIEL',
    description: rdq.description || '',
    projetId: rdq.projet?.idProjet || '',
    collaborateurIds: rdq.collaborateurs?.map(c => c.idCollaborateur) || []
  });

  // États de validation et feedback
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [hasChanges, setHasChanges] = useState(false);
  const [submitAttempted, setSubmitAttempted] = useState(false);

  // Vérifier s'il y a des changements
  useEffect(() => {
    const originalData = {
      titre: rdq.titre || '',
      dateHeure: rdq.dateHeure ? rdq.dateHeure.substring(0, 16) : '',
      adresse: rdq.adresse || '',
      mode: rdq.mode || 'PRESENTIEL',
      description: rdq.description || '',
      projetId: rdq.projet?.idProjet || '',
      collaborateurIds: rdq.collaborateurs?.map(c => c.idCollaborateur) || []
    };

    const isChanged = JSON.stringify(formData) !== JSON.stringify(originalData);
    setHasChanges(isChanged);
  }, [formData, rdq]);

  // Validation des champs
  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.titre.trim()) {
      newErrors.titre = 'Le titre est obligatoire';
    } else if (formData.titre.length > 255) {
      newErrors.titre = 'Le titre ne peut pas dépasser 255 caractères';
    }

    if (!formData.dateHeure) {
      newErrors.dateHeure = 'La date et heure sont obligatoires';
    } else {
      const selectedDate = new Date(formData.dateHeure);
      const now = new Date();
      if (selectedDate <= now) {
        newErrors.dateHeure = 'La date doit être dans le futur';
      }
    }

    if (formData.adresse && formData.adresse.length > 500) {
      newErrors.adresse = "L'adresse ne peut pas dépasser 500 caractères";
    }

    if (!formData.projetId) {
      newErrors.projetId = 'Le projet est obligatoire';
    }

    if (formData.collaborateurIds.length === 0) {
      newErrors.collaborateurIds = 'Au moins un collaborateur doit être assigné';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // Gestionnaire de changement des champs
  const handleChange = (field: string, value: any) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));

    // Supprimer l'erreur du champ modifié
    if (errors[field]) {
      setErrors(prev => ({
        ...prev,
        [field]: ''
      }));
    }
  };

  // Gestionnaire de soumission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitAttempted(true);

    if (!validateForm()) {
      return;
    }

    try {
      // Préparer les données de modification
      const updateData: Partial<RDQ> = {};

      // Inclure seulement les champs modifiés
      if (formData.titre !== (rdq.titre || '')) {
        updateData.titre = formData.titre;
      }
      
      if (formData.dateHeure !== (rdq.dateHeure ? rdq.dateHeure.substring(0, 16) : '')) {
        updateData.dateHeure = formData.dateHeure;
      }
      
      if (formData.adresse !== (rdq.adresse || '')) {
        updateData.adresse = formData.adresse;
      }
      
      if (formData.mode !== (rdq.mode || 'PRESENTIEL')) {
        updateData.mode = formData.mode as 'PRESENTIEL' | 'DISTANCIEL' | 'HYBRIDE';
      }
      
      if (formData.description !== (rdq.description || '')) {
        updateData.description = formData.description;
      }
      
      if (formData.projetId !== (rdq.projet?.idProjet || '')) {
        const selectedProjet = projets.find(p => p.idProjet === Number(formData.projetId));
        if (selectedProjet) {
          updateData.projet = { idProjet: selectedProjet.idProjet, nom: selectedProjet.nom };
        }
      }
      
      const originalCollabIds = rdq.collaborateurs?.map(c => c.idCollaborateur).sort() || [];
      const newCollabIds = [...formData.collaborateurIds].sort();
      if (JSON.stringify(originalCollabIds) !== JSON.stringify(newCollabIds)) {
        updateData.collaborateurs = formData.collaborateurIds.map(id => {
          const collab = collaborateurs.find(c => c.idCollaborateur === id);
          return collab || { idCollaborateur: id, nom: '', email: '' };
        });
      }

      await onSave(updateData);
    } catch (error) {
      console.error('Erreur lors de la sauvegarde:', error);
    }
  };

  return (
    <Card className="w-full max-w-4xl">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Building className="h-5 w-5" />
          Modifier le RDQ
        </CardTitle>
        <CardDescription>
          Modifiez les informations du RDQ. Seuls les champs modifiés seront sauvegardés.
        </CardDescription>
        
        {hasChanges && (
          <Alert>
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>
              Vous avez des modifications non sauvegardées
            </AlertDescription>
          </Alert>
        )}
      </CardHeader>

      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Titre */}
          <div className="space-y-2">
            <Label htmlFor="titre" className="flex items-center gap-2">
              <Building className="h-4 w-4" />
              Titre du RDQ *
            </Label>
            <Input
              id="titre"
              value={formData.titre}
              onChange={(e) => handleChange('titre', e.target.value)}
              placeholder="Titre du rendez-vous qualifié"
              className={errors.titre ? 'border-red-500' : ''}
            />
            {errors.titre && (
              <p className="text-sm text-red-500">{errors.titre}</p>
            )}
          </div>

          {/* Date et heure */}
          <div className="space-y-2">
            <Label htmlFor="dateHeure" className="flex items-center gap-2">
              <Calendar className="h-4 w-4" />
              Date et heure *
            </Label>
            <Input
              id="dateHeure"
              type="datetime-local"
              value={formData.dateHeure}
              onChange={(e) => handleChange('dateHeure', e.target.value)}
              className={errors.dateHeure ? 'border-red-500' : ''}
            />
            {errors.dateHeure && (
              <p className="text-sm text-red-500">{errors.dateHeure}</p>
            )}
          </div>

          {/* Adresse */}
          <div className="space-y-2">
            <Label htmlFor="adresse" className="flex items-center gap-2">
              <MapPin className="h-4 w-4" />
              Adresse
            </Label>
            <Input
              id="adresse"
              value={formData.adresse}
              onChange={(e) => handleChange('adresse', e.target.value)}
              placeholder="Adresse du rendez-vous"
              className={errors.adresse ? 'border-red-500' : ''}
            />
            {errors.adresse && (
              <p className="text-sm text-red-500">{errors.adresse}</p>
            )}
          </div>

          {/* Mode */}
          <div className="space-y-2">
            <Label className="flex items-center gap-2">
              <Clock className="h-4 w-4" />
              Mode
            </Label>
            <Select value={formData.mode} onValueChange={(value: string) => handleChange('mode', value)}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="PRESENTIEL">Présentiel</SelectItem>
                <SelectItem value="DISTANCIEL">Distanciel</SelectItem>
                <SelectItem value="HYBRIDE">Hybride</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {/* Projet */}
          <div className="space-y-2">
            <Label className="flex items-center gap-2">
              <Building className="h-4 w-4" />
              Projet *
            </Label>
            <Select 
              value={formData.projetId.toString()} 
              onValueChange={(value: string) => handleChange('projetId', Number(value))}
            >
              <SelectTrigger className={errors.projetId ? 'border-red-500' : ''}>
                <SelectValue placeholder="Sélectionner un projet" />
              </SelectTrigger>
              <SelectContent>
                {projets.map((projet) => (
                  <SelectItem key={projet.idProjet} value={projet.idProjet.toString()}>
                    {projet.nom}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            {errors.projetId && (
              <p className="text-sm text-red-500">{errors.projetId}</p>
            )}
          </div>

          {/* Collaborateurs */}
          <div className="space-y-2">
            <Label className="flex items-center gap-2">
              <Users className="h-4 w-4" />
              Collaborateurs assignés *
            </Label>
            <div className="space-y-2">
              {collaborateurs.map((collaborateur) => (
                <div key={collaborateur.idCollaborateur} className="flex items-center space-x-2">
                  <input
                    type="checkbox"
                    id={`collab-${collaborateur.idCollaborateur}`}
                    checked={formData.collaborateurIds.includes(collaborateur.idCollaborateur)}
                    onChange={(e) => {
                      const newIds = e.target.checked
                        ? [...formData.collaborateurIds, collaborateur.idCollaborateur]
                        : formData.collaborateurIds.filter(id => id !== collaborateur.idCollaborateur);
                      handleChange('collaborateurIds', newIds);
                    }}
                    className="rounded border-gray-300"
                  />
                  <Label htmlFor={`collab-${collaborateur.idCollaborateur}`} className="text-sm">
                    {collaborateur.nom} ({collaborateur.email})
                  </Label>
                </div>
              ))}
            </div>
            {errors.collaborateurIds && (
              <p className="text-sm text-red-500">{errors.collaborateurIds}</p>
            )}
          </div>

          {/* Description */}
          <div className="space-y-2">
            <Label htmlFor="description">
              Description
            </Label>
            <Textarea
              id="description"
              value={formData.description}
              onChange={(e) => handleChange('description', e.target.value)}
              placeholder="Description détaillée du RDQ"
              rows={4}
            />
          </div>

          {/* Boutons d'action */}
          <div className="flex gap-4 pt-4">
            <Button
              type="submit"
              disabled={!hasChanges || isLoading}
              className="flex items-center gap-2"
            >
              {isLoading ? (
                <>
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                  Sauvegarde...
                </>
              ) : (
                <>
                  <Save className="h-4 w-4" />
                  Sauvegarder
                </>
              )}
            </Button>
            
            <Button
              type="button"
              variant="outline"
              onClick={onCancel}
              disabled={isLoading}
              className="flex items-center gap-2"
            >
              <X className="h-4 w-4" />
              Annuler
            </Button>
          </div>

          {/* Indicateur de changements */}
          {hasChanges && (
            <div className="flex items-center gap-2 text-sm text-amber-600">
              <AlertCircle className="h-4 w-4" />
              Modifications en cours - pensez à sauvegarder
            </div>
          )}
        </form>
      </CardContent>
    </Card>
  );
};

export default RdqEditForm;