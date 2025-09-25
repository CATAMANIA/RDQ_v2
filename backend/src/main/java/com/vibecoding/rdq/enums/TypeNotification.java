package com.vibecoding.rdq.enums;

/**
 * Types de notifications disponibles dans le système RDQ
 * Utilisé pour catégoriser les différents événements qui génèrent des notifications
 */
public enum TypeNotification {
    
    /**
     * Création d'un nouveau RDQ
     */
    RDQ_CREATED("RDQ créé"),
    
    /**
     * Modification d'un RDQ existant
     */
    RDQ_UPDATED("RDQ modifié"),
    
    /**
     * Assignation d'un collaborateur à un RDQ
     */
    RDQ_ASSIGNED("RDQ assigné"),
    
    /**
     * Changement de statut d'un RDQ
     */
    RDQ_STATUS_CHANGED("Statut RDQ modifié"),
    
    /**
     * RDQ approchant de sa date d'échéance (24h avant)
     */
    RDQ_DEADLINE_APPROACHING("Échéance RDQ proche"),
    
    /**
     * RDQ échu (date passée)
     */
    RDQ_DEADLINE_PASSED("Échéance RDQ dépassée"),
    
    /**
     * Annulation d'un RDQ
     */
    RDQ_CANCELLED("RDQ annulé"),
    
    /**
     * Clôture d'un RDQ
     */
    RDQ_CLOSED("RDQ clôturé"),
    
    /**
     * Ajout d'un document à un RDQ
     */
    RDQ_DOCUMENT_ADDED("Document ajouté au RDQ"),
    
    /**
     * Création d'un bilan pour un RDQ
     */
    RDQ_BILAN_CREATED("Bilan créé pour le RDQ"),
    
    /**
     * Notification système générale
     */
    SYSTEM_NOTIFICATION("Notification système");

    private final String displayName;

    TypeNotification(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Retourne le nom d'affichage du type de notification
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Détermine si le type de notification est critique (nécessite une attention immédiate)
     */
    public boolean isCritical() {
        return this == RDQ_DEADLINE_PASSED || 
               this == RDQ_CANCELLED || 
               this == SYSTEM_NOTIFICATION;
    }

    /**
     * Détermine si le type de notification est lié à un RDQ
     */
    public boolean isRdqRelated() {
        return this != SYSTEM_NOTIFICATION;
    }
}