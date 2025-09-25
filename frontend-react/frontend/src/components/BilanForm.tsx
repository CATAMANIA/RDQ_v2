import React, { useState } from 'react';
import { Alert, AlertDescription } from './ui/alert';
import { Button } from './ui/button';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Textarea } from './ui/textarea';
import { AlertTriangle, Check, X, Plus, Loader2 } from 'lucide-react';
import { Bilan, TypeAuteur } from '../types';

interface BilanFormProps {
  rdqId: number;
  onBilanCreated: (bilan: Bilan) => void;
  onCancel?: () => void;
}

interface BilanFormData {
  note: string;
  commentaire: string;
  auteur: string;
  typeAuteur: TypeAuteur;
}

/**
 * Composant de formulaire pour créer un bilan post-entretien (TM-38)
 * Permet de saisir une évaluation avec distinction manager/collaborateur
 */
const BilanForm: React.FC<BilanFormProps> = ({ rdqId, onBilanCreated, onCancel }) => {
  const [formData, setFormData] = useState<BilanFormData>({
    note: '',
    commentaire: '',
    auteur: '',
    typeAuteur: 'MANAGER'
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleInputChange = (field: keyof BilanFormData, value: string | TypeAuteur) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    setError(null);
  };

  const validateForm = (): boolean => {
    const noteNum = parseInt(formData.note);
    
    if (!formData.note || isNaN(noteNum) || noteNum < 1 || noteNum > 10) {
      setError('La note doit être un nombre entre 1 et 10');
      return false;
    }

    if (!formData.auteur.trim()) {
      setError('Le nom de l\'auteur est obligatoire');
      return false;
    }

    if (!formData.typeAuteur) {
      setError('Le type d\'auteur doit être sélectionné');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const bilanData = {
        rdqId,
        note: parseInt(formData.note),
        commentaire: formData.commentaire.trim() || undefined,
        auteur: formData.auteur.trim(),
        typeAuteur: formData.typeAuteur
      };

      const response = await fetch('/api/bilans', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(bilanData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Erreur lors de la création du bilan');
      }

      const newBilan: Bilan = await response.json();
      onBilanCreated(newBilan);

      // Réinitialiser le formulaire
      setFormData({
        note: '',
        commentaire: '',
        auteur: '',
        typeAuteur: 'MANAGER'
      });

    } catch (err) {
      setError(err instanceof Error ? err.message : 'Une erreur est survenue');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className="mb-4">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Plus className="h-5 w-5" />
          Nouveau Bilan Post-Entretien
        </CardTitle>
      </CardHeader>
      <CardContent>
        {error && (
          <Alert className="mb-4" variant="destructive">
            <AlertTriangle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="typeAuteur">
                Type d'évaluation <span className="text-red-500">*</span>
              </Label>
              <Select 
                value={formData.typeAuteur} 
                onValueChange={(value: TypeAuteur) => handleInputChange('typeAuteur', value)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Sélectionner le type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="MANAGER">Manager</SelectItem>
                  <SelectItem value="COLLABORATEUR">Collaborateur</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label htmlFor="auteur">
                Nom de l'évaluateur <span className="text-red-500">*</span>
              </Label>
              <Input
                id="auteur"
                type="text"
                value={formData.auteur}
                onChange={(e) => handleInputChange('auteur', e.target.value)}
                placeholder="Nom et prénom"
                maxLength={100}
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="note">
              Note sur 10 <span className="text-red-500">*</span>
            </Label>
            <Input
              id="note"
              type="number"
              min="1"
              max="10"
              value={formData.note}
              onChange={(e) => handleInputChange('note', e.target.value)}
              placeholder="Note entre 1 et 10"
            />
            <p className="text-sm text-muted-foreground">
              Évaluation de 1 (très insatisfaisant) à 10 (excellent)
            </p>
          </div>

          <div className="space-y-2">
            <Label htmlFor="commentaire">Commentaire détaillé</Label>
            <Textarea
              id="commentaire"
              value={formData.commentaire}
              onChange={(e) => handleInputChange('commentaire', e.target.value)}
              placeholder="Commentaires et observations sur l'entretien..."
              rows={4}
              maxLength={1000}
            />
            <p className="text-sm text-muted-foreground">
              {formData.commentaire.length}/1000 caractères
            </p>
          </div>

          <div className="flex justify-end gap-2 pt-4">
            {onCancel && (
              <Button 
                type="button"
                variant="outline" 
                onClick={onCancel}
                disabled={loading}
              >
                <X className="h-4 w-4 mr-2" />
                Annuler
              </Button>
            )}
            <Button 
              type="submit" 
              disabled={loading}
            >
              {loading ? (
                <>
                  <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                  Création...
                </>
              ) : (
                <>
                  <Check className="h-4 w-4 mr-2" />
                  Créer le bilan
                </>
              )}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
};

export default BilanForm;