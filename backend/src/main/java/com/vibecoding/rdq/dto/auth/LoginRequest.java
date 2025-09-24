package com.vibecoding.rdq.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour la requête de connexion
 * Supporte connexion par nom d'utilisateur ou email
 */
public class LoginRequest {

    @NotBlank(message = "L'identifiant est obligatoire")
    @Size(min = 3, max = 100, message = "L'identifiant doit contenir entre 3 et 100 caractères")
    private String identifier; // username ou email

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, max = 100, message = "Le mot de passe doit contenir entre 6 et 100 caractères")
    private String password;

    private boolean rememberMe = false;

    // Constructeurs
    public LoginRequest() {}

    public LoginRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public LoginRequest(String identifier, String password, boolean rememberMe) {
        this.identifier = identifier;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    // Getters et Setters
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "identifier='" + identifier + '\'' +
                ", rememberMe=" + rememberMe +
                '}';
    }
}