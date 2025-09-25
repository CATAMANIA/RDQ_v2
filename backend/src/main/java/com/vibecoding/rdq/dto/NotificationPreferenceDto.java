package com.vibecoding.rdq.dto;

import com.vibecoding.rdq.enums.TypeNotification;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour les préférences de notification utilisateur
 * Permet aux utilisateurs de configurer leurs préférences de notification
 */
@Schema(description = "Préférences de notification utilisateur")
public class NotificationPreferenceDto {

    @Schema(description = "ID de la préférence", example = "1")
    private Long idPreference;

    @Schema(description = "Type de notification", example = "RDQ_ASSIGNED")
    private TypeNotification notificationType;

    @Schema(description = "Nom d'affichage du type de notification", example = "RDQ assigné")
    private String displayName;

    @Schema(description = "Notifications in-app activées", example = "true")
    private boolean enabled;

    @Schema(description = "Notifications email activées", example = "false")
    private boolean emailEnabled;

    @Schema(description = "Indique si ce type de notification est critique", example = "false")
    private boolean critical;

    @Schema(description = "Description du type de notification")
    private String description;

    // Constructeurs
    public NotificationPreferenceDto() {}

    public NotificationPreferenceDto(TypeNotification notificationType, boolean enabled, boolean emailEnabled) {
        this.notificationType = notificationType;
        this.enabled = enabled;
        this.emailEnabled = emailEnabled;
        this.displayName = notificationType.getDisplayName();
        this.critical = notificationType.isCritical();
        this.description = generateDescription(notificationType);
    }

    public NotificationPreferenceDto(Long idPreference, TypeNotification notificationType, 
                                   boolean enabled, boolean emailEnabled) {
        this(notificationType, enabled, emailEnabled);
        this.idPreference = idPreference;
    }

    /**
     * Génère une description pour le type de notification
     */
    private String generateDescription(TypeNotification type) {
        return switch (type) {
            case RDQ_CREATED -> "Notification lors de la création d'un nouveau RDQ";
            case RDQ_UPDATED -> "Notification lors de la modification d'un RDQ";
            case RDQ_ASSIGNED -> "Notification lorsqu'un RDQ vous est assigné";
            case RDQ_STATUS_CHANGED -> "Notification lors du changement de statut d'un RDQ";
            case RDQ_DEADLINE_APPROACHING -> "Notification 24h avant un RDQ";
            case RDQ_DEADLINE_PASSED -> "Notification lorsqu'un RDQ est en retard";
            case RDQ_CANCELLED -> "Notification lors de l'annulation d'un RDQ";
            case RDQ_CLOSED -> "Notification lors de la clôture d'un RDQ";
            case RDQ_DOCUMENT_ADDED -> "Notification lors de l'ajout d'un document";
            case RDQ_BILAN_CREATED -> "Notification lors de la création d'un bilan";
            case SYSTEM_NOTIFICATION -> "Notifications système importantes";
        };
    }

    // Getters et Setters
    public Long getIdPreference() { return idPreference; }
    public void setIdPreference(Long idPreference) { this.idPreference = idPreference; }

    public TypeNotification getNotificationType() { return notificationType; }
    public void setNotificationType(TypeNotification notificationType) { 
        this.notificationType = notificationType;
        if (notificationType != null) {
            this.displayName = notificationType.getDisplayName();
            this.critical = notificationType.isCritical();
            this.description = generateDescription(notificationType);
        }
    }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }

    public boolean isCritical() { return critical; }
    public void setCritical(boolean critical) { this.critical = critical; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}