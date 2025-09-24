package com.vibecoding.rdq.service;

import com.vibecoding.rdq.dto.auth.*;
import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.enums.Role;
import com.vibecoding.rdq.repository.UserRepository;
import com.vibecoding.rdq.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service d'authentification selon les critères TM-33
 * Gère la connexion, déconnexion, profils utilisateur et tokens JWT
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Authentifie un utilisateur et génère les tokens JWT
     */
    public LoginResponse authenticate(LoginRequest loginRequest) {
        logger.debug("Tentative de connexion pour: {}", loginRequest.getIdentifier());

        try {
            // Authentification via Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getIdentifier(),
                    loginRequest.getPassword()
                )
            );

            User user = (User) authentication.getPrincipal();

            // Vérification que le compte est activé
            if (!user.isEnabled()) {
                logger.warn("Tentative de connexion d'un compte désactivé: {}", user.getUsername());
                throw new DisabledException("Compte utilisateur désactivé");
            }

            // Génération des tokens
            String accessToken = jwtService.generateToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(user.getUsername());

            // Mise à jour de la dernière connexion
            user.updateLastLogin();
            userRepository.save(user);

            // Construction de la réponse
            LoginResponse.UserProfile userProfile = new LoginResponse.UserProfile(
                user.getIdUser(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled(),
                user.getLastLogin()
            );

            LoginResponse response = new LoginResponse(
                accessToken,
                refreshToken,
                jwtService.getExpirationMs() / 1000, // en secondes
                userProfile
            );

            logger.info("Connexion réussie pour: {} ({})", user.getUsername(), user.getRole());
            return response;

        } catch (BadCredentialsException e) {
            logger.warn("Échec de connexion - Identifiants incorrects pour: {}", loginRequest.getIdentifier());
            throw new BadCredentialsException("Identifiants incorrects");
        } catch (DisabledException e) {
            logger.warn("Échec de connexion - Compte désactivé pour: {}", loginRequest.getIdentifier());
            throw e;
        } catch (Exception e) {
            logger.error("Erreur lors de l'authentification pour: {}", loginRequest.getIdentifier(), e);
            throw new RuntimeException("Erreur lors de l'authentification", e);
        }
    }

    /**
     * Rafraîchit le token d'accès
     */
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        logger.debug("Tentative de rafraîchissement de token");

        try {
            String refreshToken = request.getRefreshToken();

            // Validation du refresh token
            if (!jwtService.validateToken(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
                logger.warn("Refresh token invalide ou expiré");
                throw new BadCredentialsException("Refresh token invalide");
            }

            String username = jwtService.getUsernameFromToken(refreshToken);
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));

            if (!user.isEnabled()) {
                logger.warn("Tentative de rafraîchissement pour un compte désactivé: {}", username);
                throw new DisabledException("Compte utilisateur désactivé");
            }

            // Génération des nouveaux tokens
            String newAccessToken = jwtService.generateToken(username);
            String newRefreshToken = jwtService.generateRefreshToken(username);

            // Construction de la réponse
            LoginResponse.UserProfile userProfile = new LoginResponse.UserProfile(
                user.getIdUser(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled(),
                user.getLastLogin()
            );

            LoginResponse response = new LoginResponse(
                newAccessToken,
                newRefreshToken,
                jwtService.getExpirationMs() / 1000,
                userProfile
            );

            logger.debug("Token rafraîchi avec succès pour: {}", username);
            return response;

        } catch (Exception e) {
            logger.error("Erreur lors du rafraîchissement du token", e);
            throw new BadCredentialsException("Erreur lors du rafraîchissement du token");
        }
    }

    /**
     * Récupère le profil de l'utilisateur connecté
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        User user = (User) authentication.getPrincipal();
        return buildUserProfileResponse(user);
    }

    /**
     * Récupère le profil d'un utilisateur par son ID (admin uniquement)
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        return buildUserProfileResponse(user);
    }

    /**
     * Construit la réponse de profil utilisateur avec les informations métier
     */
    private UserProfileResponse buildUserProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getIdUser());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setEnabled(user.isEnabled());
        response.setCreatedAt(user.getCreatedAt());
        response.setLastLogin(user.getLastLogin());

        // Ajout des informations du profil métier selon le rôle
        if (user.getManager() != null) {
            var manager = user.getManager();
            response.setManager(new UserProfileResponse.ManagerProfile(
                manager.getIdManager(),
                manager.getNom(),
                manager.getPrenom(),
                manager.getTelephone(),
                manager.getRdqs() != null ? manager.getRdqs().size() : 0
            ));
        }

        if (user.getCollaborateur() != null) {
            var collaborateur = user.getCollaborateur();
            response.setCollaborateur(new UserProfileResponse.CollaborateurProfile(
                collaborateur.getIdCollaborateur(),
                collaborateur.getNom(),
                collaborateur.getPrenom(),
                collaborateur.getTelephone(),
                collaborateur.getRdqs() != null ? collaborateur.getRdqs().size() : 0
            ));
        }

        return response;
    }

    /**
     * Déconnecte l'utilisateur (côté client)
     * Note: Avec JWT stateless, la déconnexion se fait côté client en supprimant le token
     */
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            logger.info("Déconnexion de l'utilisateur: {}", username);
        }
        SecurityContextHolder.clearContext();
    }

    /**
     * Crée un nouvel utilisateur (admin uniquement)
     */
    public User createUser(String username, String email, String password, Role role) {
        logger.debug("Création d'un nouvel utilisateur: {} avec le rôle: {}", username, role);

        // Vérification d'unicité
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Le nom d'utilisateur existe déjà: " + username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("L'email existe déjà: " + email);
        }

        // Création de l'utilisateur
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        User savedUser = userRepository.save(user);
        logger.info("Utilisateur créé avec succès: {} (ID: {})", username, savedUser.getIdUser());

        return savedUser;
    }

    /**
     * Active/désactive un utilisateur (admin uniquement)
     */
    public void toggleUserStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        user.setEnabled(enabled);
        userRepository.save(user);

        logger.info("Statut utilisateur modifié: {} -> {}", user.getUsername(), enabled ? "activé" : "désactivé");
    }

    /**
     * Liste tous les utilisateurs (admin uniquement)
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Liste les utilisateurs par rôle
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
}