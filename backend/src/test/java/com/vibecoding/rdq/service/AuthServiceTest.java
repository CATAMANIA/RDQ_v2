package com.vibecoding.rdq.service;

import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.enums.Role;
import com.vibecoding.rdq.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le service d'authentification TM-33
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Nettoyage de la base de données
        userRepository.deleteAll();

        // Création d'un utilisateur de test
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@rdq.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRole(Role.MANAGER);
        testUser.setEnabled(true);

        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("Création d'utilisateur réussie")
    void testCreateUser_Success() {
        // Test de création d'un nouvel utilisateur
        User newUser = authService.createUser("newuser", "newuser@rdq.com", "password123", Role.COLLABORATEUR);

        assertNotNull(newUser);
        assertEquals("newuser", newUser.getUsername());
        assertEquals("newuser@rdq.com", newUser.getEmail());
        assertEquals(Role.COLLABORATEUR, newUser.getRole());
        assertTrue(newUser.isEnabled());
        assertTrue(passwordEncoder.matches("password123", newUser.getPassword()));
    }

    @Test
    @DisplayName("Création d'utilisateur échoue avec username existant")
    void testCreateUser_DuplicateUsername_ThrowsException() {
        // Tentative de création avec un username déjà existant
        assertThrows(RuntimeException.class, () -> {
            authService.createUser("testuser", "other@rdq.com", "password123", Role.COLLABORATEUR);
        });
    }

    @Test
    @DisplayName("Création d'utilisateur échoue avec email existant")
    void testCreateUser_DuplicateEmail_ThrowsException() {
        // Tentative de création avec un email déjà existant
        assertThrows(RuntimeException.class, () -> {
            authService.createUser("otheruser", "test@rdq.com", "password123", Role.COLLABORATEUR);
        });
    }

    @Test
    @DisplayName("Changement de statut utilisateur")
    void testToggleUserStatus() {
        // Vérification du statut initial
        assertTrue(testUser.isEnabled());

        // Désactivation de l'utilisateur
        authService.toggleUserStatus(testUser.getIdUser(), false);
        
        User updatedUser = userRepository.findById(testUser.getIdUser()).orElse(null);
        assertNotNull(updatedUser);
        assertFalse(updatedUser.isEnabled());

        // Réactivation de l'utilisateur
        authService.toggleUserStatus(testUser.getIdUser(), true);
        
        updatedUser = userRepository.findById(testUser.getIdUser()).orElse(null);
        assertNotNull(updatedUser);
        assertTrue(updatedUser.isEnabled());
    }

    @Test
    @DisplayName("Récupération d'utilisateurs par rôle")
    void testGetUsersByRole() {
        // Création d'utilisateurs avec différents rôles
        authService.createUser("admin1", "admin1@rdq.com", "password123", Role.ADMIN);
        authService.createUser("collab1", "collab1@rdq.com", "password123", Role.COLLABORATEUR);
        authService.createUser("collab2", "collab2@rdq.com", "password123", Role.COLLABORATEUR);

        // Test de récupération par rôle
        var managers = authService.getUsersByRole(Role.MANAGER);
        assertEquals(1, managers.size());
        assertEquals("testuser", managers.get(0).getUsername());

        var admins = authService.getUsersByRole(Role.ADMIN);
        assertEquals(1, admins.size());
        assertEquals("admin1", admins.get(0).getUsername());

        var collaborateurs = authService.getUsersByRole(Role.COLLABORATEUR);
        assertEquals(2, collaborateurs.size());
    }

    @Test
    @DisplayName("Récupération de tous les utilisateurs")
    void testGetAllUsers() {
        // Création d'utilisateurs supplémentaires
        authService.createUser("admin1", "admin1@rdq.com", "password123", Role.ADMIN);
        authService.createUser("collab1", "collab1@rdq.com", "password123", Role.COLLABORATEUR);

        // Test de récupération de tous les utilisateurs
        var allUsers = authService.getAllUsers();
        assertEquals(3, allUsers.size());
    }
}