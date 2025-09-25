package com.vibecoding.rdq.service;

import com.vibecoding.rdq.entity.Notification;
import com.vibecoding.rdq.entity.NotificationPreference;
import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.enums.TypeNotification;
import com.vibecoding.rdq.repository.NotificationRepository;
import com.vibecoding.rdq.repository.NotificationPreferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des notifications pour le système RDQ
 * Gère la création, l'envoi, la lecture et les préférences de notifications
 */
@Service
@Transactional
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    // Types de notifications critiques
    private static final List<TypeNotification> CRITICAL_TYPES = Arrays.asList(
        TypeNotification.RDQ_DEADLINE_PASSED,
        TypeNotification.RDQ_CANCELLED,
        TypeNotification.SYSTEM_NOTIFICATION
    );

    /**
     * Crée une nouvelle notification
     */
    public Notification createNotification(TypeNotification type, String title, String message, User user) {
        return createNotification(type, title, message, user, null, null);
    }

    /**
     * Crée une nouvelle notification liée à un RDQ
     */
    public Notification createNotification(TypeNotification type, String title, String message, User user, RDQ rdq) {
        return createNotification(type, title, message, user, rdq, null);
    }

    /**
     * Crée une nouvelle notification avec métadonnées
     */
    public Notification createNotification(TypeNotification type, String title, String message, 
                                         User user, RDQ rdq, String metadata) {
        // Vérifier si l'utilisateur a activé ce type de notification
        if (!isNotificationEnabledForUser(user, type)) {
            logger.debug("Notification désactivée pour l'utilisateur {} et le type {}", user.getUsername(), type);
            return null;
        }

        Notification notification = new Notification(type, title, message, user, rdq);
        if (metadata != null) {
            notification.setMetadata(metadata);
        }

        Notification savedNotification = notificationRepository.save(notification);
        logger.info("Notification créée: {} pour l'utilisateur {}", type, user.getUsername());

        return savedNotification;
    }

    /**
     * Notifie plusieurs utilisateurs simultanément
     */
    public List<Notification> notifyUsers(List<User> users, TypeNotification type, String title, String message) {
        return notifyUsers(users, type, title, message, null, null);
    }

    /**
     * Notifie plusieurs utilisateurs avec un RDQ associé
     */
    public List<Notification> notifyUsers(List<User> users, TypeNotification type, String title, 
                                        String message, RDQ rdq, String metadata) {
        return users.stream()
            .map(user -> createNotification(type, title, message, user, rdq, metadata))
            .filter(notification -> notification != null)
            .toList();
    }

    /**
     * Récupère toutes les notifications d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByTimestampDesc(user);
    }

    /**
     * Récupère les notifications d'un utilisateur avec pagination
     */
    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUserOrderByTimestampDesc(user, pageable);
    }

    /**
     * Récupère les notifications non lues d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderByTimestampDesc(user);
    }

    /**
     * Compte les notifications non lues d'un utilisateur
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    /**
     * Récupère les notifications critiques non lues
     */
    @Transactional(readOnly = true)
    public List<Notification> getCriticalUnreadNotifications(User user) {
        return notificationRepository.findCriticalUnreadByUser(user, CRITICAL_TYPES);
    }

    /**
     * Marque une notification comme lue
     */
    public boolean markAsRead(Long notificationId, User user) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            logger.warn("Notification {} non trouvée", notificationId);
            return false;
        }

        Notification notification = notificationOpt.get();
        if (!notification.getUser().getIdUser().equals(user.getIdUser())) {
            logger.warn("Tentative de marquage d'une notification d'un autre utilisateur par {}", user.getUsername());
            return false;
        }

        notification.markAsRead();
        notificationRepository.save(notification);
        logger.debug("Notification {} marquée comme lue pour {}", notificationId, user.getUsername());
        return true;
    }

    /**
     * Marque toutes les notifications d'un utilisateur comme lues
     */
    public int markAllAsRead(User user) {
        int updated = notificationRepository.markAllAsReadForUser(user, LocalDateTime.now());
        logger.info("{} notifications marquées comme lues pour {}", updated, user.getUsername());
        return updated;
    }

    /**
     * Marque toutes les notifications d'un type comme lues
     */
    public int markAsReadByType(User user, TypeNotification type) {
        int updated = notificationRepository.markAsReadByTypeForUser(user, type, LocalDateTime.now());
        logger.info("{} notifications de type {} marquées comme lues pour {}", updated, type, user.getUsername());
        return updated;
    }

    /**
     * Supprime une notification
     */
    public boolean deleteNotification(Long notificationId, User user) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            return false;
        }

        Notification notification = notificationOpt.get();
        if (!notification.getUser().getIdUser().equals(user.getIdUser())) {
            logger.warn("Tentative de suppression d'une notification d'un autre utilisateur par {}", user.getUsername());
            return false;
        }

        notificationRepository.delete(notification);
        logger.info("Notification {} supprimée pour {}", notificationId, user.getUsername());
        return true;
    }

    /**
     * Nettoie les anciennes notifications (plus de 30 jours)
     */
    public int cleanupOldNotifications() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        int deleted = notificationRepository.deleteOldNotifications(cutoffDate);
        logger.info("{} anciennes notifications supprimées", deleted);
        return deleted;
    }

    // === GESTION DES PRÉFÉRENCES ===

    /**
     * Récupère les préférences d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<NotificationPreference> getUserPreferences(User user) {
        return preferenceRepository.findByUser(user);
    }

    /**
     * Initialise les préférences par défaut pour un utilisateur
     */
    public void initializeDefaultPreferences(User user) {
        // Vérifier si l'utilisateur a déjà des préférences
        if (preferenceRepository.countByUser(user) > 0) {
            return;
        }

        // Créer les préférences par défaut pour tous les types
        for (TypeNotification type : TypeNotification.values()) {
            NotificationPreference preference = new NotificationPreference(user, type);
            preferenceRepository.save(preference);
        }

        logger.info("Préférences par défaut initialisées pour {}", user.getUsername());
    }

    /**
     * Met à jour une préférence de notification
     */
    public NotificationPreference updatePreference(User user, TypeNotification type, boolean enabled, boolean emailEnabled) {
        Optional<NotificationPreference> existingOpt = preferenceRepository.findByUserAndNotificationType(user, type);
        
        NotificationPreference preference;
        if (existingOpt.isPresent()) {
            preference = existingOpt.get();
        } else {
            preference = new NotificationPreference(user, type);
        }

        preference.setEnabled(enabled);
        preference.setEmailEnabled(emailEnabled);
        
        NotificationPreference saved = preferenceRepository.save(preference);
        logger.info("Préférence mise à jour pour {} - Type: {}, App: {}, Email: {}", 
                   user.getUsername(), type, enabled, emailEnabled);
        
        return saved;
    }

    /**
     * Vérifie si un type de notification est activé pour un utilisateur
     */
    @Transactional(readOnly = true)
    public boolean isNotificationEnabledForUser(User user, TypeNotification type) {
        return preferenceRepository.findByUserAndNotificationType(user, type)
            .map(NotificationPreference::isEnabled)
            .orElse(true); // Par défaut activé si pas de préférence
    }

    /**
     * Vérifie si les emails sont activés pour un type de notification
     */
    @Transactional(readOnly = true)
    public boolean isEmailEnabledForUser(User user, TypeNotification type) {
        return preferenceRepository.findByUserAndNotificationType(user, type)
            .map(NotificationPreference::isEmailEnabled)
            .orElse(type.isCritical()); // Par défaut activé pour les notifications critiques
    }

    /**
     * Recherche de notifications par terme
     */
    @Transactional(readOnly = true)
    public List<Notification> searchNotifications(User user, String searchTerm) {
        return notificationRepository.searchNotifications(user, searchTerm);
    }

    /**
     * Récupère les statistiques de notifications pour un utilisateur
     */
    @Transactional(readOnly = true)
    public NotificationStats getNotificationStats(User user) {
        Object[] stats = notificationRepository.getNotificationStatsForUser(user, CRITICAL_TYPES);
        if (stats.length > 0) {
            return new NotificationStats(
                ((Number) stats[0]).longValue(), // total
                ((Number) stats[1]).longValue(), // unread
                ((Number) stats[2]).longValue()  // criticalUnread
            );
        }
        return new NotificationStats(0, 0, 0);
    }

    /**
     * Classe interne pour les statistiques de notification
     */
    public static class NotificationStats {
        private final long total;
        private final long unread;
        private final long criticalUnread;

        public NotificationStats(long total, long unread, long criticalUnread) {
            this.total = total;
            this.unread = unread;
            this.criticalUnread = criticalUnread;
        }

        public long getTotal() { return total; }
        public long getUnread() { return unread; }
        public long getCriticalUnread() { return criticalUnread; }
    }
}