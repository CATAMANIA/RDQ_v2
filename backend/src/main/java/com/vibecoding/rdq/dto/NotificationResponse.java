package com.vibecoding.rdq.dto;

import com.vibecoding.rdq.enums.TypeNotification;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO pour la réponse des notifications
 * Contient toutes les informations d'une notification pour l'API
 */
@Schema(description = "Réponse contenant les informations d'une notification")
public class NotificationResponse {

    @Schema(description = "ID unique de la notification", example = "1")
    private Long idNotification;

    @Schema(description = "Type de notification", example = "RDQ_ASSIGNED")
    private TypeNotification type;

    @Schema(description = "Titre de la notification", example = "Nouveau RDQ assigné")
    private String title;

    @Schema(description = "Message de la notification", example = "Un nouveau RDQ vous a été assigné")
    private String message;

    @Schema(description = "Statut de lecture", example = "false")
    private boolean isRead;

    @Schema(description = "Date et heure de création de la notification")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "Informations du RDQ associé (si applicable)")
    private RdqInfo rdq;

    @Schema(description = "Métadonnées additionnelles")
    private String metadata;

    @Schema(description = "Indique si la notification est critique")
    private boolean critical;

    @Schema(description = "Date de création")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Date de dernière modification")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Constructeurs
    public NotificationResponse() {}

    public NotificationResponse(Long idNotification, TypeNotification type, String title, String message,
                              boolean isRead, LocalDateTime timestamp, boolean critical) {
        this.idNotification = idNotification;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.timestamp = timestamp;
        this.critical = critical;
    }

    // Classe interne pour les informations RDQ
    @Schema(description = "Informations de base du RDQ associé")
    public static class RdqInfo {
        @Schema(description = "ID du RDQ", example = "1")
        private Long idRdq;

        @Schema(description = "Titre du RDQ", example = "Réunion client ABC")
        private String titre;

        @Schema(description = "Statut du RDQ", example = "PLANIFIE")
        private String statut;

        @Schema(description = "Date du RDQ")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime dateHeure;

        // Constructeurs
        public RdqInfo() {}

        public RdqInfo(Long idRdq, String titre, String statut, LocalDateTime dateHeure) {
            this.idRdq = idRdq;
            this.titre = titre;
            this.statut = statut;
            this.dateHeure = dateHeure;
        }

        // Getters et Setters
        public Long getIdRdq() { return idRdq; }
        public void setIdRdq(Long idRdq) { this.idRdq = idRdq; }

        public String getTitre() { return titre; }
        public void setTitre(String titre) { this.titre = titre; }

        public String getStatut() { return statut; }
        public void setStatut(String statut) { this.statut = statut; }

        public LocalDateTime getDateHeure() { return dateHeure; }
        public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }
    }

    // Getters et Setters
    public Long getIdNotification() { return idNotification; }
    public void setIdNotification(Long idNotification) { this.idNotification = idNotification; }

    public TypeNotification getType() { return type; }
    public void setType(TypeNotification type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public RdqInfo getRdq() { return rdq; }
    public void setRdq(RdqInfo rdq) { this.rdq = rdq; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public boolean isCritical() { return critical; }
    public void setCritical(boolean critical) { this.critical = critical; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}