package com.vibecoding.rdq.service;

import com.vibecoding.rdq.entity.Bilan;
import com.vibecoding.rdq.entity.Bilan.TypeAuteur;
import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.repository.BilanRepository;
import com.vibecoding.rdq.repository.RDQRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des bilans post-entretien (TM-38)
 * Permet la création, consultation et modification des bilans
 * avec distinction entre évaluations manager et collaborateur
 */
@Service
@Transactional
public class BilanService {

    @Autowired
    private BilanRepository bilanRepository;

    @Autowired
    private RDQRepository rdqRepository;

    /**
     * Crée un nouveau bilan post-entretien
     * @param rdqId ID du RDQ associé
     * @param note Note sur 10 (1-10)
     * @param commentaire Commentaire détaillé
     * @param auteur Nom de l'auteur du bilan
     * @param typeAuteur Type d'auteur (MANAGER ou COLLABORATEUR)
     * @return Le bilan créé
     * @throws IllegalArgumentException si le RDQ n'existe pas ou les paramètres sont invalides
     */
    public Bilan creerBilan(Long rdqId, Integer note, String commentaire, String auteur, TypeAuteur typeAuteur) {
        // Validation des paramètres
        if (rdqId == null || note == null || auteur == null || typeAuteur == null) {
            throw new IllegalArgumentException("Tous les paramètres obligatoires doivent être fournis");
        }

        if (note < 1 || note > 10) {
            throw new IllegalArgumentException("La note doit être comprise entre 1 et 10");
        }

        if (auteur.trim().isEmpty()) {
            throw new IllegalArgumentException("L'auteur ne peut pas être vide");
        }

        // Vérification de l'existence du RDQ
        Optional<RDQ> rdqOpt = rdqRepository.findById(rdqId);
        if (rdqOpt.isEmpty()) {
            throw new IllegalArgumentException("RDQ non trouvé avec l'ID: " + rdqId);
        }

        RDQ rdq = rdqOpt.get();

        // Création du bilan
        Bilan bilan = new Bilan(note, commentaire, typeAuteur, auteur, rdq);
        bilan.setDateCreation(LocalDateTime.now());

        return bilanRepository.save(bilan);
    }

    /**
     * Récupère tous les bilans d'un RDQ
     * @param rdqId ID du RDQ
     * @return Liste des bilans triés par date de création décroissante
     */
    @Transactional(readOnly = true)
    public List<Bilan> getBilansParRdq(Long rdqId) {
        return bilanRepository.findByRdqIdOrderByDateCreationDesc(rdqId);
    }

    /**
     * Récupère tous les bilans d'un type d'auteur pour un RDQ
     * @param rdqId ID du RDQ
     * @param typeAuteur Type d'auteur (MANAGER ou COLLABORATEUR)
     * @return Liste des bilans filtrés par type d'auteur
     */
    @Transactional(readOnly = true)
    public List<Bilan> getBilansParRdqEtType(Long rdqId, TypeAuteur typeAuteur) {
        // Pour l'instant, on récupère tous les bilans et on filtre en mémoire
        // TODO: Ajouter une méthode spécifique au repository plus tard
        return bilanRepository.findByRdqIdOrderByDateCreationDesc(rdqId)
                .stream()
                .filter(bilan -> bilan.getTypeAuteur().equals(typeAuteur))
                .toList();
    }

    /**
     * Met à jour un bilan existant
     * @param bilanId ID du bilan à modifier
     * @param note Nouvelle note (optionnelle)
     * @param commentaire Nouveau commentaire (optionnel)
     * @return Le bilan mis à jour
     * @throws IllegalArgumentException si le bilan n'existe pas
     */
    public Bilan modifierBilan(Long bilanId, Integer note, String commentaire) {
        Optional<Bilan> bilanOpt = bilanRepository.findById(bilanId);
        if (bilanOpt.isEmpty()) {
            throw new IllegalArgumentException("Bilan non trouvé avec l'ID: " + bilanId);
        }

        Bilan bilan = bilanOpt.get();

        // Mise à jour des champs si fournis
        if (note != null) {
            if (note < 1 || note > 10) {
                throw new IllegalArgumentException("La note doit être comprise entre 1 et 10");
            }
            bilan.setNote(note);
        }

        if (commentaire != null) {
            bilan.setCommentaire(commentaire);
        }

        return bilanRepository.save(bilan);
    }

    /**
     * Supprime un bilan
     * @param bilanId ID du bilan à supprimer
     * @throws IllegalArgumentException si le bilan n'existe pas
     */
    public void supprimerBilan(Long bilanId) {
        if (!bilanRepository.existsById(bilanId)) {
            throw new IllegalArgumentException("Bilan non trouvé avec l'ID: " + bilanId);
        }
        
        bilanRepository.deleteById(bilanId);
    }

    /**
     * Calcule la note moyenne des bilans pour un RDQ
     * @param rdqId ID du RDQ
     * @return Note moyenne ou null si aucun bilan
     */
    @Transactional(readOnly = true)
    public Double getNoteMoyenne(Long rdqId) {
        return bilanRepository.getAverageNoteByRdqId(rdqId);
    }

    /**
     * Calcule la note moyenne par type d'auteur pour un RDQ
     * @param rdqId ID du RDQ
     * @param typeAuteur Type d'auteur
     * @return Note moyenne pour ce type d'auteur
     */
    @Transactional(readOnly = true)
    public Double getNoteMoyenneParType(Long rdqId, TypeAuteur typeAuteur) {
        List<Bilan> bilans = getBilansParRdqEtType(rdqId, typeAuteur);
        if (bilans.isEmpty()) {
            return null;
        }

        return bilans.stream()
                .mapToInt(Bilan::getNote)
                .average()
                .orElse(0.0);
    }

    /**
     * Vérifie si un RDQ a des bilans
     * @param rdqId ID du RDQ
     * @return true si le RDQ a au moins un bilan
     */
    @Transactional(readOnly = true)
    public boolean rdqADesBilans(Long rdqId) {
        return bilanRepository.countByRdqId(rdqId) > 0;
    }
}