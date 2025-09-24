package com.vibecoding.rdq.dto.auth;

import com.vibecoding.rdq.enums.Role;

import java.time.LocalDateTime;

/**
 * DTO pour la réponse de connexion
 * Contient les informations JWT et utilisateur
 */
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserProfile user;

    // Constructeurs
    public LoginResponse() {}

    public LoginResponse(String accessToken, String refreshToken, long expiresIn, UserProfile user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    // Getters et Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    /**
     * Classe interne pour le profil utilisateur dans la réponse
     */
    public static class UserProfile {
        private Long id;
        private String username;
        private String email;
        private Role role;
        private boolean enabled;
        private LocalDateTime lastLogin;

        // Constructeurs
        public UserProfile() {}

        public UserProfile(Long id, String username, String email, Role role, boolean enabled, LocalDateTime lastLogin) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
            this.enabled = enabled;
            this.lastLogin = lastLogin;
        }

        // Getters et Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public LocalDateTime getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(LocalDateTime lastLogin) {
            this.lastLogin = lastLogin;
        }
    }
}