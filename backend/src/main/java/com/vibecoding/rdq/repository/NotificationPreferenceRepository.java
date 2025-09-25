package com.vibecoding.rdq.repository;

import com.vibecoding.rdq.entity.NotificationPreference;
import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.enums.TypeNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des préférences de notification
 * Fournit les méthodes d'accès aux données pour les préférences utilisateur
 */
@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    /**
     * Trouve toutes les préférences d'un utilisateur
     */
    List<NotificationPreference> findByUser(User user);

    /**
     * Trouve une préférence spécifique pour un utilisateur et un type de notification
     */
    Optional<NotificationPreference> findByUserAndNotificationType(User user, TypeNotification notificationType);

    /**
     * Trouve les préférences activées pour un utilisateur
     */
    List<NotificationPreference> findByUserAndEnabledTrue(User user);

    /**
     * Trouve les préférences avec email activé pour un utilisateur
     */
    List<NotificationPreference> findByUserAndEmailEnabledTrue(User user);

    /**
     * Trouve les préférences activées (app ou email) pour un utilisateur
     */
    @Query("SELECT np FROM NotificationPreference np WHERE np.user = :user AND (np.enabled = true OR np.emailEnabled = true)")
    List<NotificationPreference> findActivePreferencesByUser(@Param("user") User user);

    /**
     * Vérifie si un utilisateur a activé un type de notification
     */
    @Query("SELECT CASE WHEN COUNT(np) > 0 THEN true ELSE false END FROM NotificationPreference np WHERE np.user = :user AND np.notificationType = :type AND np.enabled = true")
    boolean isNotificationEnabledForUser(@Param("user") User user, @Param("type") TypeNotification type);

    /**
     * Vérifie si un utilisateur a activé les emails pour un type de notification
     */
    @Query("SELECT CASE WHEN COUNT(np) > 0 THEN true ELSE false END FROM NotificationPreference np WHERE np.user = :user AND np.notificationType = :type AND np.emailEnabled = true")
    boolean isEmailEnabledForUser(@Param("user") User user, @Param("type") TypeNotification type);

    /**
     * Trouve les types de notifications non configurés pour un utilisateur
     */
    @Query("SELECT t FROM TypeNotification t WHERE t NOT IN (SELECT np.notificationType FROM NotificationPreference np WHERE np.user = :user)")
    List<TypeNotification> findUnconfiguredTypesForUser(@Param("user") User user);

    /**
     * Compte le nombre total de préférences d'un utilisateur
     */
    long countByUser(User user);

    /**
     * Supprime toutes les préférences d'un utilisateur
     */
    void deleteByUser(User user);

    /**
     * Trouve tous les utilisateurs ayant activé un type de notification spécifique
     */
    @Query("SELECT np.user FROM NotificationPreference np WHERE np.notificationType = :type AND np.enabled = true")
    List<User> findUsersWithEnabledNotificationType(@Param("type") TypeNotification type);

    /**
     * Trouve tous les utilisateurs ayant activé les emails pour un type de notification spécifique
     */
    @Query("SELECT np.user FROM NotificationPreference np WHERE np.notificationType = :type AND np.emailEnabled = true")
    List<User> findUsersWithEmailEnabledForType(@Param("type") TypeNotification type);
}