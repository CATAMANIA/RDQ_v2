package com.vibecoding.rdq.controller;

import com.vibecoding.rdq.entity.Bilan;
import com.vibecoding.rdq.entity.Bilan.TypeAuteur;
import com.vibecoding.rdq.service.BilanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des bilans post-entretien (TM-38)
 * Fournit les endpoints pour créer, consulter et modifier les bilans
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000") // Pour le frontend React
public class BilanController {

    @Autowired
    private BilanService bilanService;

    /**
     * DTO pour la création d'un bilan
     */
    public static class BilanCreateRequest {
        private Long rdqId;
        private Integer note;
        private String commentaire;
        private String auteur;
        private TypeAuteur typeAuteur;

        // Constructeurs
        public BilanCreateRequest() {}

        public BilanCreateRequest(Long rdqId, Integer note, String commentaire, String auteur, TypeAuteur typeAuteur) {
            this.rdqId = rdqId;
            this.note = note;
            this.commentaire = commentaire;
            this.auteur = auteur;
            this.typeAuteur = typeAuteur;
        }

        // Getters et Setters
        public Long getRdqId() { return rdqId; }
        public void setRdqId(Long rdqId) { this.rdqId = rdqId; }

        public Integer getNote() { return note; }
        public void setNote(Integer note) { this.note = note; }

        public String getCommentaire() { return commentaire; }
        public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

        public String getAuteur() { return auteur; }
        public void setAuteur(String auteur) { this.auteur = auteur; }

        public TypeAuteur getTypeAuteur() { return typeAuteur; }
        public void setTypeAuteur(TypeAuteur typeAuteur) { this.typeAuteur = typeAuteur; }
    }

    /**
     * DTO pour la modification d'un bilan
     */
    public static class BilanUpdateRequest {
        private Integer note;
        private String commentaire;

        // Constructeurs
        public BilanUpdateRequest() {}

        public BilanUpdateRequest(Integer note, String commentaire) {
            this.note = note;
            this.commentaire = commentaire;
        }

        // Getters et Setters
        public Integer getNote() { return note; }
        public void setNote(Integer note) { this.note = note; }

        public String getCommentaire() { return commentaire; }
        public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    }

    /**
     * Crée un nouveau bilan post-entretien
     * POST /api/bilans
     */
    @PostMapping("/bilans")
    public ResponseEntity<?> creerBilan(@Valid @RequestBody BilanCreateRequest request) {
        try {
            Bilan bilan = bilanService.creerBilan(
                request.getRdqId(),
                request.getNote(),
                request.getCommentaire(),
                request.getAuteur(),
                request.getTypeAuteur()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(bilan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la création du bilan"));
        }
    }

    /**
     * Récupère tous les bilans d'un RDQ
     * GET /api/rdqs/{rdqId}/bilans
     */
    @GetMapping("/rdqs/{rdqId}/bilans")
    public ResponseEntity<?> getBilansParRdq(@PathVariable Long rdqId) {
        try {
            List<Bilan> bilans = bilanService.getBilansParRdq(rdqId);
            return ResponseEntity.ok(bilans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération des bilans"));
        }
    }

    /**
     * Récupère les bilans d'un RDQ filtrés par type d'auteur
     * GET /api/rdqs/{rdqId}/bilans?typeAuteur=MANAGER|COLLABORATEUR
     */
    @GetMapping("/rdqs/{rdqId}/bilans/by-type")
    public ResponseEntity<?> getBilansParRdqEtType(
            @PathVariable Long rdqId,
            @RequestParam TypeAuteur typeAuteur) {
        try {
            List<Bilan> bilans = bilanService.getBilansParRdqEtType(rdqId, typeAuteur);
            return ResponseEntity.ok(bilans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération des bilans"));
        }
    }

    /**
     * Met à jour un bilan existant
     * PUT /api/bilans/{bilanId}
     */
    @PutMapping("/bilans/{bilanId}")
    public ResponseEntity<?> modifierBilan(
            @PathVariable Long bilanId,
            @Valid @RequestBody BilanUpdateRequest request) {
        try {
            Bilan bilan = bilanService.modifierBilan(bilanId, request.getNote(), request.getCommentaire());
            return ResponseEntity.ok(bilan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la modification du bilan"));
        }
    }

    /**
     * Supprime un bilan
     * DELETE /api/bilans/{bilanId}
     */
    @DeleteMapping("/bilans/{bilanId}")
    public ResponseEntity<?> supprimerBilan(@PathVariable Long bilanId) {
        try {
            bilanService.supprimerBilan(bilanId);
            return ResponseEntity.ok(Map.of("message", "Bilan supprimé avec succès"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la suppression du bilan"));
        }
    }

    /**
     * Récupère les statistiques des bilans pour un RDQ
     * GET /api/rdqs/{rdqId}/bilans/stats
     */
    @GetMapping("/rdqs/{rdqId}/bilans/stats")
    public ResponseEntity<?> getStatistiquesBilans(@PathVariable Long rdqId) {
        try {
            Double noteMoyenne = bilanService.getNoteMoyenne(rdqId);
            Double noteMoyenneManager = bilanService.getNoteMoyenneParType(rdqId, TypeAuteur.MANAGER);
            Double noteMoyenneCollaborateur = bilanService.getNoteMoyenneParType(rdqId, TypeAuteur.COLLABORATEUR);
            boolean aDesBilans = bilanService.rdqADesBilans(rdqId);

            Map<String, Object> stats = Map.of(
                "noteMoyenneGlobale", noteMoyenne != null ? noteMoyenne : 0.0,
                "noteMoyenneManager", noteMoyenneManager != null ? noteMoyenneManager : 0.0,
                "noteMoyenneCollaborateur", noteMoyenneCollaborateur != null ? noteMoyenneCollaborateur : 0.0,
                "aDesBilans", aDesBilans
            );

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du calcul des statistiques"));
        }
    }

    /**
     * Récupère un bilan spécifique par son ID
     * GET /api/bilans/{bilanId}
     */
    @GetMapping("/bilans/{bilanId}")
    public ResponseEntity<?> getBilan(@PathVariable Long bilanId) {
        try {
            // Cette méthode nécessiterait un ajout au service
            // Pour l'instant, on retourne une erreur 501 Not Implemented
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(Map.of("error", "Méthode non implémentée - TODO: ajouter au service"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la récupération du bilan"));
        }
    }
}