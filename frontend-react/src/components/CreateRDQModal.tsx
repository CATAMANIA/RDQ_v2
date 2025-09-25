import React, { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from './ui/dialog';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Textarea } from './ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { RDQ } from '../types';
import { useManagerData } from '../hooks/useApiData';

interface CreateRDQModalProps {
  onClose: () => void;
  onCreate: (rdq: Omit<RDQ, 'idRDQ' | 'dateCreation' | 'dateModification' | 'manager' | 'collaborateur' | 'client' | 'projet' | 'documents' | 'bilans'>) => void;
}

export const CreateRDQModal: React.FC<CreateRDQModalProps> = ({ onClose, onCreate }) => {
  const { data: apiData, loading } = useManagerData();
  
  // Gestion du chargement TM-45
  if (loading) {
    return (
      <Dialog open={true} onOpenChange={onClose}>
        <DialogContent>
          <div className="flex items-center justify-center p-8">
            <div className="text-lg">Chargement des données...</div>
          </div>
        </DialogContent>
      </Dialog>
    );
  }

  const collaborateurs = apiData?.collaborateurs || [];
  const clients = apiData?.clients || [];
  const projets: any[] = []; // TODO TM-45: Implémenter la gestion des projets via API
  const [formData, setFormData] = useState({
    titre: '',
    dateHeure: '',
    adresse: '',
    mode: 'PRESENTIEL' as 'PRESENTIEL' | 'DISTANCIEL' | 'HYBRIDE',
    indicationsManager: '',
    statut: 'EN_COURS' as 'EN_COURS',
    idManager: 1,
    idCollaborateur: 0,
    idClient: 0,
    idProjet: 0
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const validateForm = () => {
    const newErrors: Record<string, string> = {};

    if (!formData.titre.trim()) {
      newErrors.titre = 'Le titre est obligatoire';
    }

    if (!formData.dateHeure) {
      newErrors.dateHeure = 'La date et l\'heure sont obligatoires';
    }

    if (formData.idCollaborateur === 0) {
      newErrors.idCollaborateur = 'Veuillez sélectionner un collaborateur';
    }

    if (formData.idClient === 0) {
      newErrors.idClient = 'Veuillez sélectionner un client';
    }

    if (formData.mode === 'PRESENTIEL' && !formData.adresse.trim()) {
      newErrors.adresse = 'L\'adresse est requise pour les RDQ en présentiel';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    onCreate({
      ...formData,
      idRdq: 0, // Temporaire - sera assigné par l'API
      dateHeure: formData.dateHeure,
      idProjet: formData.idProjet || undefined
    });
  };

  return (
    <Dialog open onOpenChange={onClose}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Créer un nouveau RDQ</DialogTitle>
          <DialogDescription>
            Remplissez les informations nécessaires pour créer un nouveau rendez-vous qualifié.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="titre">Titre *</Label>
              <Input
                id="titre"
                value={formData.titre}
                onChange={(e) => setFormData({ ...formData, titre: e.target.value })}
                placeholder="Ex: Entretien Développeur Senior"
                className={errors.titre ? 'border-red-500' : ''}
              />
              {errors.titre && <p className="text-sm text-red-500">{errors.titre}</p>}
            </div>

            <div className="space-y-2">
              <Label htmlFor="dateHeure">Date et heure *</Label>
              <Input
                id="dateHeure"
                type="datetime-local"
                value={formData.dateHeure}
                onChange={(e) => setFormData({ ...formData, dateHeure: e.target.value })}
                className={errors.dateHeure ? 'border-red-500' : ''}
              />
              {errors.dateHeure && <p className="text-sm text-red-500">{errors.dateHeure}</p>}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label>Collaborateur *</Label>
              <Select 
                value={formData.idCollaborateur > 0 ? formData.idCollaborateur.toString() : undefined} 
                onValueChange={(value: string) => setFormData({ ...formData, idCollaborateur: parseInt(value) })}
              >
                <SelectTrigger className={errors.idCollaborateur ? 'border-red-500' : ''}>
                  <SelectValue placeholder="Sélectionner un collaborateur" />
                </SelectTrigger>
                <SelectContent>
                  {collaborateurs.map((collaborateur: any) => (
                    <SelectItem key={collaborateur.id} value={collaborateur.id.toString()}>
                      {collaborateur.prenom} {collaborateur.nom}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {errors.idCollaborateur && <p className="text-sm text-red-500">{errors.idCollaborateur}</p>}
            </div>

            <div className="space-y-2">
              <Label>Client *</Label>
              <Select 
                value={formData.idClient > 0 ? formData.idClient.toString() : undefined} 
                onValueChange={(value: string) => setFormData({ ...formData, idClient: parseInt(value) })}
              >
                <SelectTrigger className={errors.idClient ? 'border-red-500' : ''}>
                  <SelectValue placeholder="Sélectionner un client" />
                </SelectTrigger>
                <SelectContent>
                  {clients.map((client: any) => (
                    <SelectItem key={client.idClient} value={client.idClient.toString()}>
                      {client.nom}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {errors.idClient && <p className="text-sm text-red-500">{errors.idClient}</p>}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label>Mode</Label>
              <Select 
                value={formData.mode} 
                onValueChange={(value: string) => setFormData({ ...formData, mode: value as 'PRESENTIEL' | 'DISTANCIEL' | 'HYBRIDE' })}
              >
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

            <div className="space-y-2">
              <Label>Projet</Label>
              <Select 
                value={formData.idProjet > 0 ? formData.idProjet.toString() : "none"} 
                onValueChange={(value: string) => setFormData({ ...formData, idProjet: value === "none" ? 0 : parseInt(value) })}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Sélectionner un projet (optionnel)" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="none">Aucun projet</SelectItem>
                  {projets.map((projet: any) => (
                    <SelectItem key={projet.idProjet} value={projet.idProjet.toString()}>
                      {projet.nom}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>

          {formData.mode === 'PRESENTIEL' && (
            <div className="space-y-2">
              <Label htmlFor="adresse">Adresse *</Label>
              <Textarea
                id="adresse"
                value={formData.adresse}
                onChange={(e) => setFormData({ ...formData, adresse: e.target.value })}
                placeholder="Adresse complète du rendez-vous"
                rows={2}
                className={errors.adresse ? 'border-red-500' : ''}
              />
              {errors.adresse && <p className="text-sm text-red-500">{errors.adresse}</p>}
            </div>
          )}

          <div className="space-y-2">
            <Label htmlFor="indications">Indications particulières</Label>
            <Textarea
              id="indications"
              value={formData.indicationsManager}
              onChange={(e) => setFormData({ ...formData, indicationsManager: e.target.value })}
              placeholder="Informations importantes pour le collaborateur..."
              rows={3}
            />
          </div>

          <div className="flex justify-end space-x-2 pt-4 border-t">
            <Button type="button" variant="outline" onClick={onClose}>
              Annuler
            </Button>
            <Button type="submit">
              Créer le RDQ
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
};