package com.vibecoding.rdq.enums;

import org.springframework.security.core.GrantedAuthority;

/**
 * Énumération des rôles utilisateur dans l'application RDQ
 * Utilisée pour la gestion des autorisations avec Spring Security
 */
public enum Role implements GrantedAuthority {
    
    /**
     * Administrateur système - Accès complet
     */
    ADMIN("ROLE_ADMIN"),
    
    /**
     * Manager - Gestion des RDQ et des équipes
     */
    MANAGER("ROLE_MANAGER"),
    
    /**
     * Collaborateur - Accès aux RDQ assignés
     */
    COLLABORATEUR("ROLE_COLLABORATEUR");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    /**
     * Retourne le nom du rôle sans le préfixe ROLE_
     */
    public String getRoleName() {
        return this.name();
    }

    /**
     * Vérifie si le rôle a au moins le niveau d'autorisation spécifié
     */
    public boolean hasAtLeastAuthority(Role requiredRole) {
        return this.ordinal() <= requiredRole.ordinal();
    }
}