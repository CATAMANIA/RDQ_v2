package com.vibecoding.rdq.repository;

import com.vibecoding.rdq.entity.Notification;
import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.enums.TypeNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour la gestion des notifications
 * Fournit les méthodes d'accès aux données pour les notifications
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Trouve toutes les notifications d'un utilisateur, triées par date décroissante
     */
    List<Notification> findByUserOrderByTimestampDesc(User user);

    /**
     * Trouve toutes les notifications d'un utilisateur avec pagination
     */
    Page<Notification> findByUserOrderByTimestampDesc(User user, Pageable pageable);

    /**
     * Trouve les notifications non lues d'un utilisateur
     */
    List<Notification> findByUserAndIsReadFalseOrderByTimestampDesc(User user);

    /**
     * Compte le nombre de notifications non lues d'un utilisateur
     */
    long countByUserAndIsReadFalse(User user);

    /**
     * Trouve les notifications par type pour un utilisateur
     */
    List<Notification> findByUserAndTypeOrderByTimestampDesc(User user, TypeNotification type);

    /**
     * Trouve les notifications d'un utilisateur par statut de lecture
     */
    Page<Notification> findByUserAndIsReadOrderByTimestampDesc(User user, boolean isRead, Pageable pageable);

    /**
     * Trouve les notifications liées à un RDQ spécifique
     */
    @Query("SELECT n FROM Notification n WHERE n.rdq.idRdq = :rdqId ORDER BY n.timestamp DESC")
    List<Notification> findByRdqId(@Param("rdqId") Long rdqId);

    /**
     * Trouve les notifications liées à un RDQ pour un utilisateur spécifique
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.rdq.idRdq = :rdqId ORDER BY n.timestamp DESC")
    List<Notification> findByUserAndRdqId(@Param("user") User user, @Param("rdqId") Long rdqId);

    /**
     * Trouve les notifications critiques non lues d'un utilisateur
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = false AND n.type IN :criticalTypes ORDER BY n.timestamp DESC")
    List<Notification> findCriticalUnreadByUser(@Param("user") User user, @Param("criticalTypes") List<TypeNotification> criticalTypes);

    /**
     * Marque toutes les notifications d'un utilisateur comme lues
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.updatedAt = :updateTime WHERE n.user = :user AND n.isRead = false")
    int markAllAsReadForUser(@Param("user") User user, @Param("updateTime") LocalDateTime updateTime);

    /**
     * Marque les notifications d'un type spécifique comme lues pour un utilisateur
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.updatedAt = :updateTime WHERE n.user = :user AND n.type = :type AND n.isRead = false")
    int markAsReadByTypeForUser(@Param("user") User user, @Param("type") TypeNotification type, @Param("updateTime") LocalDateTime updateTime);

    /**
     * Supprime les anciennes notifications (plus de X jours)
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.timestamp < :cutoffDate")
    int deleteOldNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Trouve les notifications récentes (dernières 24h) d'un utilisateur
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.timestamp >= :since ORDER BY n.timestamp DESC")
    List<Notification> findRecentByUser(@Param("user") User user, @Param("since") LocalDateTime since);

    /**
     * Statistiques de notifications pour un utilisateur
     */
    @Query("""
        SELECT 
            COUNT(n) as total,
            SUM(CASE WHEN n.isRead = false THEN 1 ELSE 0 END) as unread,
            SUM(CASE WHEN n.type IN :criticalTypes AND n.isRead = false THEN 1 ELSE 0 END) as criticalUnread
        FROM Notification n 
        WHERE n.user = :user
        """)
    Object[] getNotificationStatsForUser(@Param("user") User user, @Param("criticalTypes") List<TypeNotification> criticalTypes);

    /**
     * Trouve les notifications par plage de dates
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.timestamp BETWEEN :startDate AND :endDate ORDER BY n.timestamp DESC")
    List<Notification> findByUserAndDateRange(@Param("user") User user, 
                                            @Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);

    /**
     * Recherche textuelle dans les notifications
     */
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(n.message) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) ORDER BY n.timestamp DESC")
    List<Notification> searchNotifications(@Param("user") User user, @Param("searchTerm") String searchTerm);

    /**
     * Vérifie si un utilisateur a des notifications non lues
     */
    boolean existsByUserAndIsReadFalse(User user);
}