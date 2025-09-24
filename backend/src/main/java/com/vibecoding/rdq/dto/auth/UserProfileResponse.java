package com.vibecoding.rdq.dto.auth;

import com.vibecoding.rdq.enums.Role;

import java.time.LocalDateTime;

/**
 * DTO pour le profil utilisateur complet
 * Utilisé pour les endpoints de profil et d'information utilisateur
 */
public class UserProfileResponse {

    private Long id;
    private String username;
    private String email;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    
    // Informations du profil métier (Manager ou Collaborateur)
    private ManagerProfile manager;
    private CollaborateurProfile collaborateur;

    // Constructeurs
    public UserProfileResponse() {}

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public ManagerProfile getManager() {
        return manager;
    }

    public void setManager(ManagerProfile manager) {
        this.manager = manager;
    }

    public CollaborateurProfile getCollaborateur() {
        return collaborateur;
    }

    public void setCollaborateur(CollaborateurProfile collaborateur) {
        this.collaborateur = collaborateur;
    }

    /**
     * Classe interne pour le profil Manager
     */
    public static class ManagerProfile {
        private Long id;
        private String nom;
        private String prenom;
        private String telephone;
        private int nbRdqs;

        // Constructeurs
        public ManagerProfile() {}

        public ManagerProfile(Long id, String nom, String prenom, String telephone, int nbRdqs) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.telephone = telephone;
            this.nbRdqs = nbRdqs;
        }

        // Getters et Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public int getNbRdqs() {
            return nbRdqs;
        }

        public void setNbRdqs(int nbRdqs) {
            this.nbRdqs = nbRdqs;
        }

        public String getFullName() {
            return prenom + " " + nom;
        }
    }

    /**
     * Classe interne pour le profil Collaborateur
     */
    public static class CollaborateurProfile {
        private Long id;
        private String nom;
        private String prenom;
        private String telephone;
        private int nbRdqs;

        // Constructeurs
        public CollaborateurProfile() {}

        public CollaborateurProfile(Long id, String nom, String prenom, String telephone, int nbRdqs) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.telephone = telephone;
            this.nbRdqs = nbRdqs;
        }

        // Getters et Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public int getNbRdqs() {
            return nbRdqs;
        }

        public void setNbRdqs(int nbRdqs) {
            this.nbRdqs = nbRdqs;
        }

        public String getFullName() {
            return prenom + " " + nom;
        }
    }
}