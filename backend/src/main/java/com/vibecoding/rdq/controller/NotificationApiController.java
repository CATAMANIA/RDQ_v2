package com.vibecoding.rdq.controller;

import com.vibecoding.rdq.dto.NotificationListResponse;
import com.vibecoding.rdq.dto.NotificationPreferenceDto;
import com.vibecoding.rdq.dto.NotificationResponse;
import com.vibecoding.rdq.entity.Notification;
import com.vibecoding.rdq.entity.NotificationPreference;
import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.enums.TypeNotification;
import com.vibecoding.rdq.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des notifications - API v1
 * Endpoints sécurisés avec contrôle d'accès par utilisateur authentifié
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications Management", description = "API de gestion des notifications RDQ")
@SecurityRequirement(name = "bearerAuth")
public class NotificationApiController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Récupère les notifications de l'utilisateur connecté
     */
    @GetMapping
    @Operation(summary = "Récupérer les notifications", 
               description = "Récupère les notifications de l'utilisateur connecté avec pagination")
    @ApiResponse(responseCode = "200", description = "Notifications récupérées avec succès")
    @ApiResponse(responseCode = "401", description = "Non authentifié")
    public ResponseEntity<NotificationListResponse> getUserNotifications(
            @Parameter(description = "Numéro de page (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Taille de la page", example = "20")
            @RequestParam(defaultValue = "20") int size,
            
            @Parameter(description = "Filtrer par statut de lecture")
            @RequestParam(required = false) Boolean unreadOnly,
            
            @Parameter(description = "Filtrer par type de notification")
            @RequestParam(required = false) TypeNotification type) {
        
        try {
            User currentUser = getCurrentUser();
            Pageable pageable = PageRequest.of(page, size);
            
            Page<Notification> notificationsPage;
            if (unreadOnly != null && unreadOnly) {
                notificationsPage = notificationService.getUserNotifications(currentUser, pageable)
                    .map(notification -> notification.isRead() ? null : notification);
            } else {
                notificationsPage = notificationService.getUserNotifications(currentUser, pageable);
            }
            
            // Conversion en DTOs
            List<NotificationResponse> notificationResponses = notificationsPage.getContent().stream()
                .filter(notification -> notification != null)
                .map(this::mapToNotificationResponse)
                .toList();
            
            // Statistiques
            NotificationService.NotificationStats stats = notificationService.getNotificationStats(currentUser);
            
            NotificationListResponse response = new NotificationListResponse(
                notificationResponses,
                stats.getTotal(),
                stats.getUnread(),
                stats.getCriticalUnread(),
                page,
                size,
                notificationsPage.getTotalPages(),
                notificationsPage.hasNext(),
                notificationsPage.hasPrevious()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère le nombre de notifications non lues
     */
    @GetMapping("/unread-count")
    @Operation(summary = "Compteur de notifications non lues", 
               description = "Récupère le nombre de notifications non lues de l'utilisateur")
    @ApiResponse(responseCode = "200", description = "Compteur récupéré avec succès")
    public ResponseEntity<Map<String, Object>> getUnreadCount() {
        try {
            User currentUser = getCurrentUser();
            long unreadCount = notificationService.getUnreadCount(currentUser);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "unreadCount", unreadCount
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Marque une notification comme lue
     */
    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Marquer comme lue", 
               description = "Marque une notification spécifique comme lue")
    @ApiResponse(responseCode = "200", description = "Notification marquée comme lue")
    @ApiResponse(responseCode = "404", description = "Notification non trouvée")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @Parameter(description = "ID de la notification")
            @PathVariable Long notificationId) {
        
        try {
            User currentUser = getCurrentUser();
            boolean success = notificationService.markAsRead(notificationId, currentUser);
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notification marquée comme lue"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", "Notification non trouvée"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Marque toutes les notifications comme lues
     */
    @PutMapping("/mark-all-read")
    @Operation(summary = "Marquer toutes comme lues", 
               description = "Marque toutes les notifications de l'utilisateur comme lues")
    @ApiResponse(responseCode = "200", description = "Toutes les notifications marquées comme lues")
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        try {
            User currentUser = getCurrentUser();
            int updatedCount = notificationService.markAllAsRead(currentUser);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", updatedCount + " notifications marquées comme lues",
                "updatedCount", updatedCount
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Supprime une notification
     */
    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Supprimer une notification", 
               description = "Supprime une notification spécifique")
    @ApiResponse(responseCode = "200", description = "Notification supprimée")
    @ApiResponse(responseCode = "404", description = "Notification non trouvée")
    public ResponseEntity<Map<String, Object>> deleteNotification(
            @Parameter(description = "ID de la notification")
            @PathVariable Long notificationId) {
        
        try {
            User currentUser = getCurrentUser();
            boolean success = notificationService.deleteNotification(notificationId, currentUser);
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Notification supprimée"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "error", "Notification non trouvée"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Récupère les préférences de notification de l'utilisateur
     */
    @GetMapping("/preferences")
    @Operation(summary = "Récupérer les préférences", 
               description = "Récupère les préférences de notification de l'utilisateur")
    @ApiResponse(responseCode = "200", description = "Préférences récupérées avec succès")
    public ResponseEntity<Map<String, Object>> getUserPreferences() {
        try {
            User currentUser = getCurrentUser();
            
            // Initialiser les préférences par défaut si nécessaire
            notificationService.initializeDefaultPreferences(currentUser);
            
            List<NotificationPreference> preferences = notificationService.getUserPreferences(currentUser);
            List<NotificationPreferenceDto> preferenceDtos = preferences.stream()
                .map(this::mapToPreferenceDto)
                .toList();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "preferences", preferenceDtos
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Met à jour une préférence de notification
     */
    @PutMapping("/preferences/{type}")
    @Operation(summary = "Mettre à jour une préférence", 
               description = "Met à jour une préférence de notification spécifique")
    @ApiResponse(responseCode = "200", description = "Préférence mise à jour")
    public ResponseEntity<Map<String, Object>> updatePreference(
            @Parameter(description = "Type de notification")
            @PathVariable TypeNotification type,
            
            @Parameter(description = "Notifications in-app activées")
            @RequestParam boolean enabled,
            
            @Parameter(description = "Notifications email activées")
            @RequestParam(defaultValue = "false") boolean emailEnabled) {
        
        try {
            User currentUser = getCurrentUser();
            NotificationPreference updatedPreference = notificationService.updatePreference(
                currentUser, type, enabled, emailEnabled
            );
            
            NotificationPreferenceDto preferenceDto = mapToPreferenceDto(updatedPreference);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Préférence mise à jour",
                "preference", preferenceDto
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Recherche dans les notifications
     */
    @GetMapping("/search")
    @Operation(summary = "Rechercher des notifications", 
               description = "Recherche dans le titre et le contenu des notifications")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche")
    public ResponseEntity<Map<String, Object>> searchNotifications(
            @Parameter(description = "Terme de recherche")
            @RequestParam String query) {
        
        try {
            User currentUser = getCurrentUser();
            List<Notification> notifications = notificationService.searchNotifications(currentUser, query);
            
            List<NotificationResponse> notificationResponses = notifications.stream()
                .map(this::mapToNotificationResponse)
                .toList();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "results", notificationResponses,
                "count", notificationResponses.size()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // === MÉTHODES UTILITAIRES ===

    /**
     * Récupère l'utilisateur actuellement connecté
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }

    /**
     * Convertit une entité Notification en DTO
     */
    private NotificationResponse mapToNotificationResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse(
            notification.getIdNotification(),
            notification.getType(),
            notification.getTitle(),
            notification.getMessage(),
            notification.isRead(),
            notification.getTimestamp(),
            notification.isCritical()
        );
        
        response.setMetadata(notification.getMetadata());
        response.setCreatedAt(notification.getCreatedAt());
        response.setUpdatedAt(notification.getUpdatedAt());
        
        // Informations RDQ si applicable
        if (notification.getRdq() != null) {
            NotificationResponse.RdqInfo rdqInfo = new NotificationResponse.RdqInfo(
                notification.getRdq().getIdRdq(),
                notification.getRdq().getTitre(),
                notification.getRdq().getStatut().name(),
                notification.getRdq().getDateHeure()
            );
            response.setRdq(rdqInfo);
        }
        
        return response;
    }

    /**
     * Convertit une entité NotificationPreference en DTO
     */
    private NotificationPreferenceDto mapToPreferenceDto(NotificationPreference preference) {
        return new NotificationPreferenceDto(
            preference.getIdPreference(),
            preference.getNotificationType(),
            preference.isEnabled(),
            preference.isEmailEnabled()
        );
    }
}