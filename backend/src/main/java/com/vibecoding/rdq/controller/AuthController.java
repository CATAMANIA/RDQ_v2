package com.vibecoding.rdq.controller;

import com.vibecoding.rdq.dto.auth.*;
import com.vibecoding.rdq.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur d'authentification selon les critères TM-33
 * Gère la connexion, déconnexion, profils utilisateur et tokens JWT
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "API d'authentification et de gestion des utilisateurs")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Connexion utilisateur
     */
    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et retourne les tokens JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion réussie"),
        @ApiResponse(responseCode = "401", description = "Identifiants incorrects"),
        @ApiResponse(responseCode = "403", description = "Compte désactivé"),
        @ApiResponse(responseCode = "400", description = "Données de requête invalides")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.debug("Tentative de connexion pour: {}", loginRequest.getIdentifier());
        
        try {
            LoginResponse response = authService.authenticate(loginRequest);
            logger.info("Connexion réussie pour: {}", loginRequest.getIdentifier());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warn("Échec de connexion pour: {} - {}", loginRequest.getIdentifier(), e.getMessage());
            throw e;
        }
    }

    /**
     * Rafraîchissement du token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Rafraîchir le token", description = "Génère un nouveau token d'accès à partir du refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token rafraîchi avec succès"),
        @ApiResponse(responseCode = "401", description = "Refresh token invalide ou expiré"),
        @ApiResponse(responseCode = "400", description = "Données de requête invalides")
    })
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        logger.debug("Tentative de rafraîchissement de token");
        
        try {
            LoginResponse response = authService.refreshToken(request);
            logger.debug("Token rafraîchi avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.warn("Échec du rafraîchissement de token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Profil de l'utilisateur connecté
     */
    @GetMapping("/profile")
    @Operation(summary = "Profil utilisateur", description = "Récupère le profil complet de l'utilisateur connecté")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profil récupéré avec succès"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<UserProfileResponse> getCurrentProfile() {
        logger.debug("Récupération du profil utilisateur");
        
        UserProfileResponse profile = authService.getCurrentUserProfile();
        logger.debug("Profil récupéré pour: {}", profile.getUsername());
        return ResponseEntity.ok(profile);
    }

    /**
     * Profil d'un utilisateur spécifique (admin uniquement)
     */
    @GetMapping("/profile/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Profil utilisateur par ID", description = "Récupère le profil d'un utilisateur spécifique (admin uniquement)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profil récupéré avec succès"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Admin requis"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long userId) {
        logger.debug("Récupération du profil utilisateur ID: {}", userId);
        
        UserProfileResponse profile = authService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Déconnexion (côté serveur pour logging)
     */
    @PostMapping("/logout")
    @Operation(summary = "Déconnexion utilisateur", description = "Déconnecte l'utilisateur (côté client en supprimant le token)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Déconnexion réussie"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public ResponseEntity<Map<String, String>> logout() {
        logger.debug("Déconnexion utilisateur");
        
        authService.logout();
        
        return ResponseEntity.ok(Map.of(
            "message", "Déconnexion réussie",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }

    /**
     * Validation du token (endpoint utilitaire)
     */
    @GetMapping("/validate")
    @Operation(summary = "Valider le token", description = "Vérifie si le token JWT est valide")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token valide"),
        @ApiResponse(responseCode = "401", description = "Token invalide")
    })
    public ResponseEntity<Map<String, Object>> validateToken() {
        logger.debug("Validation de token");
        
        UserProfileResponse profile = authService.getCurrentUserProfile();
        
        return ResponseEntity.ok(Map.of(
            "valid", true,
            "user", Map.of(
                "id", profile.getId(),
                "username", profile.getUsername(),
                "role", profile.getRole(),
                "enabled", profile.isEnabled()
            ),
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }

    /**
     * Information sur les capacités d'authentification (endpoint public)
     */
    @GetMapping("/info")
    @Operation(summary = "Informations d'authentification", description = "Récupère les informations publiques sur l'authentification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informations récupérées")
    })
    public ResponseEntity<Map<String, Object>> getAuthInfo() {
        return ResponseEntity.ok(Map.of(
            "supportedMethods", new String[]{"JWT", "Bearer Token"},
            "tokenType", "Bearer",
            "loginEndpoint", "/api/auth/login",
            "refreshEndpoint", "/api/auth/refresh",
            "supportedRoles", new String[]{"ADMIN", "MANAGER", "COLLABORATEUR"},
            "features", Map.of(
                "rememberMe", true,
                "refreshToken", true,
                "profileManagement", true,
                "roleBasedAccess", true
            )
        ));
    }
}