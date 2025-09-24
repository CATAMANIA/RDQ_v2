package com.vibecoding.rdq.repository;

import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des utilisateurs
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouve un utilisateur par son nom d'utilisateur
     */
    Optional<User> findByUsername(String username);

    /**
     * Trouve un utilisateur par son email
     */
    Optional<User> findByEmail(String email);

    /**
     * Trouve un utilisateur par son nom d'utilisateur ou son email
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    /**
     * Vérifie si un nom d'utilisateur existe déjà
     */
    boolean existsByUsername(String username);

    /**
     * Vérifie si un email existe déjà
     */
    boolean existsByEmail(String email);

    /**
     * Trouve tous les utilisateurs par rôle
     */
    List<User> findByRole(Role role);

    /**
     * Trouve tous les utilisateurs actifs
     */
    List<User> findByEnabledTrue();

    /**
     * Trouve tous les utilisateurs avec un rôle spécifique et actifs
     */
    List<User> findByRoleAndEnabledTrue(Role role);

    /**
     * Met à jour la date de dernière connexion
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.idUser = :userId")
    void updateLastLogin(@Param("userId") Long userId, @Param("lastLogin") LocalDateTime lastLogin);

    /**
     * Active ou désactive un utilisateur
     */
    @Modifying
    @Query("UPDATE User u SET u.enabled = :enabled WHERE u.idUser = :userId")
    void updateUserStatus(@Param("userId") Long userId, @Param("enabled") boolean enabled);

    /**
     * Compte le nombre d'utilisateurs par rôle
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);

    /**
     * Trouve tous les utilisateurs sans Manager ou Collaborateur associé
     */
    @Query("SELECT u FROM User u WHERE u.manager IS NULL AND u.collaborateur IS NULL")
    List<User> findUsersWithoutProfile();
}