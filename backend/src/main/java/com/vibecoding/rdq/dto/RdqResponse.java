package com.vibecoding.rdq.dto;

import com.vibecoding.rdq.entity.RDQ.ModeRDQ;
import com.vibecoding.rdq.entity.RDQ.StatutRDQ;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour la réponse de création/récupération d'un RDQ
 */
@Schema(description = "Réponse contenant les informations d'un RDQ")
public class RdqResponse {

    @Schema(description = "ID unique du RDQ", example = "1")
    private Long idRdq;

    @Schema(description = "Titre du RDQ", example = "RDQ Société ABC - Développement App Mobile")
    private String titre;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Date et heure du RDQ", example = "2025-10-15 14:30:00")
    private LocalDateTime dateHeure;

    @Schema(description = "Adresse du RDQ", example = "123 Rue de la Paix, 75001 Paris")
    private String adresse;

    @Schema(description = "Mode du RDQ", example = "PRESENTIEL")
    private ModeRDQ mode;

    @Schema(description = "Statut actuel du RDQ", example = "PLANIFIE")
    private StatutRDQ statut;

    @Schema(description = "Description détaillée du RDQ", example = "Rencontre pour évaluer les besoins en développement mobile")
    private String description;

    @Schema(description = "Informations du manager responsable")
    private ManagerInfo manager;

    @Schema(description = "Informations du projet associé")
    private ProjetInfo projet;

    @Schema(description = "Liste des collaborateurs assignés")
    private List<CollaborateurInfo> collaborateurs;

    @Schema(description = "Indications supplémentaires", example = "Prévoir matériel de présentation")
    private String indications;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Date de création du RDQ")
    private LocalDateTime dateCreation;

    @Schema(description = "Liste des documents attachés au RDQ")
    private List<DocumentResponse> documents;

    // Nested DTOs
    @Schema(description = "Informations du manager")
    public static class ManagerInfo {
        @Schema(description = "ID du manager", example = "1")
        private Long idManager;
        
        @Schema(description = "Nom complet du manager", example = "Jean Dupont")
        private String nom;
        
        @Schema(description = "Email du manager", example = "jean.dupont@rdq.com")
        private String email;

        public ManagerInfo() {}

        public ManagerInfo(Long idManager, String nom, String email) {
            this.idManager = idManager;
            this.nom = nom;
            this.email = email;
        }

        // Getters and setters
        public Long getIdManager() { return idManager; }
        public void setIdManager(Long idManager) { this.idManager = idManager; }
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @Schema(description = "Informations du projet")
    public static class ProjetInfo {
        @Schema(description = "ID du projet", example = "1")
        private Long idProjet;
        
        @Schema(description = "Nom du projet", example = "Application Mobile ABC")
        private String nom;
        
        @Schema(description = "Nom du client", example = "Société ABC")
        private String nomClient;

        public ProjetInfo() {}

        public ProjetInfo(Long idProjet, String nom, String nomClient) {
            this.idProjet = idProjet;
            this.nom = nom;
            this.nomClient = nomClient;
        }

        // Getters and setters
        public Long getIdProjet() { return idProjet; }
        public void setIdProjet(Long idProjet) { this.idProjet = idProjet; }
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        public String getNomClient() { return nomClient; }
        public void setNomClient(String nomClient) { this.nomClient = nomClient; }
    }

    @Schema(description = "Informations du collaborateur")
    public static class CollaborateurInfo {
        @Schema(description = "ID du collaborateur", example = "1")
        private Long idCollaborateur;
        
        @Schema(description = "Nom complet du collaborateur", example = "Marie Martin")
        private String nom;
        
        @Schema(description = "Email du collaborateur", example = "marie.martin@rdq.com")
        private String email;

        public CollaborateurInfo() {}

        public CollaborateurInfo(Long idCollaborateur, String nom, String email) {
            this.idCollaborateur = idCollaborateur;
            this.nom = nom;
            this.email = email;
        }

        // Getters and setters
        public Long getIdCollaborateur() { return idCollaborateur; }
        public void setIdCollaborateur(Long idCollaborateur) { this.idCollaborateur = idCollaborateur; }
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    // Constructors
    public RdqResponse() {}

    // Getters and Setters
    public Long getIdRdq() {
        return idRdq;
    }

    public void setIdRdq(Long idRdq) {
        this.idRdq = idRdq;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public ModeRDQ getMode() {
        return mode;
    }

    public void setMode(ModeRDQ mode) {
        this.mode = mode;
    }

    public StatutRDQ getStatut() {
        return statut;
    }

    public void setStatut(StatutRDQ statut) {
        this.statut = statut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ManagerInfo getManager() {
        return manager;
    }

    public void setManager(ManagerInfo manager) {
        this.manager = manager;
    }

    public ProjetInfo getProjet() {
        return projet;
    }

    public void setProjet(ProjetInfo projet) {
        this.projet = projet;
    }

    public List<CollaborateurInfo> getCollaborateurs() {
        return collaborateurs;
    }

    public void setCollaborateurs(List<CollaborateurInfo> collaborateurs) {
        this.collaborateurs = collaborateurs;
    }

    public String getIndications() {
        return indications;
    }

    public void setIndications(String indications) {
        this.indications = indications;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<DocumentResponse> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentResponse> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "RdqResponse{" +
                "idRdq=" + idRdq +
                ", titre='" + titre + '\'' +
                ", dateHeure=" + dateHeure +
                ", adresse='" + adresse + '\'' +
                ", mode=" + mode +
                ", statut=" + statut +
                ", description='" + description + '\'' +
                ", manager=" + manager +
                ", projet=" + projet +
                ", collaborateurs=" + collaborateurs +
                ", indications='" + indications + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}