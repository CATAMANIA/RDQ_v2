package com.vibecoding.rdq.entity;

import com.vibecoding.rdq.enums.TypeNotification;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entité NotificationPreference pour gérer les préférences utilisateur de notifications
 * Permet aux utilisateurs de configurer quels types de notifications ils souhaitent recevoir
 */
@Entity
@Table(name = "notification_preferences", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "notification_type"}))
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_preference")
    private Long idPreference;

    @NotNull(message = "L'utilisateur est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Le type de notification est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private TypeNotification notificationType;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "email_enabled", nullable = false)
    private boolean emailEnabled = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructeurs
    public NotificationPreference() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public NotificationPreference(User user, TypeNotification notificationType) {
        this();
        this.user = user;
        this.notificationType = notificationType;
        // Par défaut, les notifications critiques ont l'email activé
        this.emailEnabled = notificationType.isCritical();
    }

    public NotificationPreference(User user, TypeNotification notificationType, boolean enabled, boolean emailEnabled) {
        this(user, notificationType);
        this.enabled = enabled;
        this.emailEnabled = emailEnabled;
    }

    // Méthodes utilitaires
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Active les notifications pour ce type
     */
    public void enable() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Désactive les notifications pour ce type
     */
    public void disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Active les notifications email pour ce type
     */
    public void enableEmail() {
        this.emailEnabled = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Désactive les notifications email pour ce type
     */
    public void disableEmail() {
        this.emailEnabled = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Vérifie si les notifications sont entièrement désactivées
     */
    public boolean isCompletelyDisabled() {
        return !enabled && !emailEnabled;
    }

    /**
     * Vérifie si au moins un canal de notification est activé
     */
    public boolean hasActiveChannel() {
        return enabled || emailEnabled;
    }

    // Getters et Setters
    public Long getIdPreference() {
        return idPreference;
    }

    public void setIdPreference(Long idPreference) {
        this.idPreference = idPreference;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TypeNotification getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(TypeNotification notificationType) {
        this.notificationType = notificationType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "NotificationPreference{" +
                "idPreference=" + idPreference +
                ", userId=" + (user != null ? user.getIdUser() : null) +
                ", notificationType=" + notificationType +
                ", enabled=" + enabled +
                ", emailEnabled=" + emailEnabled +
                '}';
    }
}