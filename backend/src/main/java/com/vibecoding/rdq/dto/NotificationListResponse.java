package com.vibecoding.rdq.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO pour la réponse paginée des notifications
 * Encapsule les notifications avec métadonnées de pagination
 */
@Schema(description = "Réponse paginée des notifications")
public class NotificationListResponse {

    @Schema(description = "Liste des notifications")
    private List<NotificationResponse> notifications;

    @Schema(description = "Nombre total de notifications")
    private long totalNotifications;

    @Schema(description = "Nombre de notifications non lues")
    private long unreadCount;

    @Schema(description = "Nombre de notifications critiques non lues")
    private long criticalUnreadCount;

    @Schema(description = "Page actuelle", example = "0")
    private int currentPage;

    @Schema(description = "Taille de la page", example = "20")
    private int pageSize;

    @Schema(description = "Nombre total de pages", example = "5")
    private int totalPages;

    @Schema(description = "Indique s'il y a une page suivante")
    private boolean hasNext;

    @Schema(description = "Indique s'il y a une page précédente")
    private boolean hasPrevious;

    // Constructeurs
    public NotificationListResponse() {}

    public NotificationListResponse(List<NotificationResponse> notifications, 
                                  long totalNotifications, long unreadCount, long criticalUnreadCount,
                                  int currentPage, int pageSize, int totalPages,
                                  boolean hasNext, boolean hasPrevious) {
        this.notifications = notifications;
        this.totalNotifications = totalNotifications;
        this.unreadCount = unreadCount;
        this.criticalUnreadCount = criticalUnreadCount;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    // Getters et Setters
    public List<NotificationResponse> getNotifications() { return notifications; }
    public void setNotifications(List<NotificationResponse> notifications) { this.notifications = notifications; }

    public long getTotalNotifications() { return totalNotifications; }
    public void setTotalNotifications(long totalNotifications) { this.totalNotifications = totalNotifications; }

    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }

    public long getCriticalUnreadCount() { return criticalUnreadCount; }
    public void setCriticalUnreadCount(long criticalUnreadCount) { this.criticalUnreadCount = criticalUnreadCount; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }

    public boolean isHasPrevious() { return hasPrevious; }
    public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }
}

/**
 * DTO pour les requêtes de mise à jour des préférences
 */
@Schema(description = "Requête de mise à jour des préférences de notification")
class UpdateNotificationPreferencesRequest {

    @Schema(description = "Liste des préférences à mettre à jour")
    private List<NotificationPreferenceDto> preferences;

    // Constructeurs
    public UpdateNotificationPreferencesRequest() {}

    public UpdateNotificationPreferencesRequest(List<NotificationPreferenceDto> preferences) {
        this.preferences = preferences;
    }

    // Getters et Setters
    public List<NotificationPreferenceDto> getPreferences() { return preferences; }
    public void setPreferences(List<NotificationPreferenceDto> preferences) { this.preferences = preferences; }
}

/**
 * DTO pour les requêtes de marquage de notifications
 */
@Schema(description = "Requête de marquage de notifications comme lues")
class MarkNotificationsRequest {

    @Schema(description = "Liste des IDs de notifications à marquer comme lues")
    private List<Long> notificationIds;

    @Schema(description = "Marquer toutes les notifications comme lues", example = "false")
    private boolean markAll;

    // Constructeurs
    public MarkNotificationsRequest() {}

    public MarkNotificationsRequest(List<Long> notificationIds) {
        this.notificationIds = notificationIds;
        this.markAll = false;
    }

    public MarkNotificationsRequest(boolean markAll) {
        this.markAll = markAll;
    }

    // Getters et Setters
    public List<Long> getNotificationIds() { return notificationIds; }
    public void setNotificationIds(List<Long> notificationIds) { this.notificationIds = notificationIds; }

    public boolean isMarkAll() { return markAll; }
    public void setMarkAll(boolean markAll) { this.markAll = markAll; }
}