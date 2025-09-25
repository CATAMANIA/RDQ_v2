package com.vibecoding.rdq.dto;

import com.vibecoding.rdq.entity.RDQ.StatutRDQ;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour les critères de recherche des RDQ
 * Support de filtrage avancé avec pagination
 * Utilisé pour l'implémentation TM-41 - US09 Historique et filtrage des RDQ
 */
public class RdqSearchCriteria {

    // Filtrage par client
    private String clientNom;
    private Long clientId;

    // Filtrage par collaborateur
    private String collaborateurNom;
    private Long collaborateurId;

    // Filtrage par manager
    private Long managerId;

    // Filtrage par projet
    private String projetNom;
    private Long projetId;

    // Filtrage par statut
    private List<StatutRDQ> statuts;

    // Filtrage par mode
    private List<String> modes; // PRESENTIEL, DISTANCIEL, HYBRIDE

    // Filtrage par date
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    // Recherche textuelle
    private String searchTerm; // recherche dans titre, description, indications

    // Pagination
    @Min(0)
    private Integer page = 0;

    @Min(1)
    @Max(100)
    private Integer size = 10;

    // Tri
    private String sortBy = "dateHeure"; // dateHeure, titre, statut, client
    private String sortDirection = "DESC"; // ASC, DESC

    // Options d'inclusion
    private Boolean includeHistory = false; // inclure les RDQ terminés/annulés
    private Boolean includeDocuments = false;
    private Boolean includeBilans = false;

    // Filtres spécifiques manager
    private Boolean myRdqsOnly = false; // uniquement les RDQ du manager connecté

    // Filtres spécifiques collaborateur
    private Boolean myAssignmentsOnly = false; // uniquement les RDQ assignés au collaborateur

    // Champ pour compatibilité avec le mode unique (String au lieu de List<String>)
    private String mode;

    /**
     * Vérifie si les critères de recherche sont vides
     */
    public boolean isEmpty() {
        return clientNom == null && clientId == null &&
               collaborateurNom == null && collaborateurId == null &&
               managerId == null &&
               projetNom == null && projetId == null &&
               (statuts == null || statuts.isEmpty()) &&
               (modes == null || modes.isEmpty()) &&
               dateDebut == null && dateFin == null &&
               (searchTerm == null || searchTerm.trim().isEmpty());
    }

    /**
     * Retourne les critères appliqués sous forme de description
     */
    public String getAppliedFiltersDescription() {
        StringBuilder sb = new StringBuilder();
        
        if (clientNom != null) sb.append("Client: ").append(clientNom).append("; ");
        if (collaborateurNom != null) sb.append("Collaborateur: ").append(collaborateurNom).append("; ");
        if (projetNom != null) sb.append("Projet: ").append(projetNom).append("; ");
        if (statuts != null && !statuts.isEmpty()) sb.append("Statuts: ").append(statuts).append("; ");
        if (searchTerm != null) sb.append("Recherche: ").append(searchTerm).append("; ");
        
        return sb.toString();
    }

    // Constructeurs
    public RdqSearchCriteria() {}

    public RdqSearchCriteria(String clientNom, String collaborateurNom, String projetNom, List<StatutRDQ> statuts,
                           LocalDateTime dateDebut, LocalDateTime dateFin, String mode, String searchTerm,
                           Integer page, Integer size, String sortBy, String sortDirection) {
        this.clientNom = clientNom;
        this.collaborateurNom = collaborateurNom;
        this.projetNom = projetNom;
        this.statuts = statuts;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.mode = mode;
        this.searchTerm = searchTerm;
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    // Getters et Setters
    public String getClientNom() { return clientNom; }
    public void setClientNom(String clientNom) { this.clientNom = clientNom; }

    public String getCollaborateurNom() { return collaborateurNom; }
    public void setCollaborateurNom(String collaborateurNom) { this.collaborateurNom = collaborateurNom; }

    public String getProjetNom() { return projetNom; }
    public void setProjetNom(String projetNom) { this.projetNom = projetNom; }

    public List<StatutRDQ> getStatuts() { return statuts; }
    public void setStatuts(List<StatutRDQ> statuts) { this.statuts = statuts; }

    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }

    // Getters/Setters pour les champs manquants
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Long getCollaborateurId() { return collaborateurId; }
    public void setCollaborateurId(Long collaborateurId) { this.collaborateurId = collaborateurId; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    public Long getProjetId() { return projetId; }
    public void setProjetId(Long projetId) { this.projetId = projetId; }

    public List<String> getModes() { return modes; }
    public void setModes(List<String> modes) { this.modes = modes; }

    public Boolean getIncludeHistory() { return includeHistory; }
    public void setIncludeHistory(Boolean includeHistory) { this.includeHistory = includeHistory; }

    public Boolean getMyRdqsOnly() { return myRdqsOnly; }
    public void setMyRdqsOnly(Boolean myRdqsOnly) { this.myRdqsOnly = myRdqsOnly; }

    public Boolean getMyAssignmentsOnly() { return myAssignmentsOnly; }
    public void setMyAssignmentsOnly(Boolean myAssignmentsOnly) { this.myAssignmentsOnly = myAssignmentsOnly; }

    // Méthode builder statique pour compatibilité
    public static RdqSearchCriteria builder() {
        return new RdqSearchCriteria();
    }

    // Méthodes fluent API pour compatibilité builder
    public RdqSearchCriteria clientNom(String clientNom) { this.clientNom = clientNom; return this; }
    public RdqSearchCriteria clientId(Long clientId) { this.clientId = clientId; return this; }
    public RdqSearchCriteria collaborateurNom(String collaborateurNom) { this.collaborateurNom = collaborateurNom; return this; }
    public RdqSearchCriteria collaborateurId(Long collaborateurId) { this.collaborateurId = collaborateurId; return this; }
    public RdqSearchCriteria managerId(Long managerId) { this.managerId = managerId; return this; }
    public RdqSearchCriteria projetNom(String projetNom) { this.projetNom = projetNom; return this; }
    public RdqSearchCriteria projetId(Long projetId) { this.projetId = projetId; return this; }
    public RdqSearchCriteria statuts(List<StatutRDQ> statuts) { this.statuts = statuts; return this; }
    public RdqSearchCriteria modes(List<String> modes) { this.modes = modes; return this; }
    public RdqSearchCriteria dateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; return this; }
    public RdqSearchCriteria dateFin(LocalDateTime dateFin) { this.dateFin = dateFin; return this; }
    public RdqSearchCriteria searchTerm(String searchTerm) { this.searchTerm = searchTerm; return this; }
    public RdqSearchCriteria page(Integer page) { this.page = page; return this; }
    public RdqSearchCriteria size(Integer size) { this.size = size; return this; }
    public RdqSearchCriteria sortBy(String sortBy) { this.sortBy = sortBy; return this; }
    public RdqSearchCriteria sortDirection(String sortDirection) { this.sortDirection = sortDirection; return this; }
    public RdqSearchCriteria includeHistory(Boolean includeHistory) { this.includeHistory = includeHistory; return this; }
    public RdqSearchCriteria myRdqsOnly(Boolean myRdqsOnly) { this.myRdqsOnly = myRdqsOnly; return this; }
    public RdqSearchCriteria myAssignmentsOnly(Boolean myAssignmentsOnly) { this.myAssignmentsOnly = myAssignmentsOnly; return this; }
    public RdqSearchCriteria includeDocuments(Boolean includeDocuments) { this.includeDocuments = includeDocuments; return this; }
    public RdqSearchCriteria includeBilans(Boolean includeBilans) { this.includeBilans = includeBilans; return this; }
    
    public RdqSearchCriteria build() { return this; }
}