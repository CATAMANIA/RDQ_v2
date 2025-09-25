package com.vibecoding.rdq.dto;

import java.util.List;

/**
 * DTO pour la réponse de recherche paginée des RDQ (TM-41)
 * Encapsule les résultats de recherche avec métadonnées de pagination
 */
public class RdqSearchResponse {
    
    // Données de la page courante
    private List<RdqResponse> content;
    
    // Métadonnées de pagination
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
    
    // Statistiques de recherche
    private RdqSearchStats searchStats;
    
    // Critères de recherche appliqués
    private RdqSearchCriteria appliedCriteria;

    // Constructeurs
    public RdqSearchResponse() {}

    public RdqSearchResponse(List<RdqResponse> content, int page, int size, long totalElements, 
                           int totalPages, boolean first, boolean last, boolean hasNext, 
                           boolean hasPrevious, RdqSearchStats searchStats, RdqSearchCriteria appliedCriteria) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
        this.searchStats = searchStats;
        this.appliedCriteria = appliedCriteria;
    }

    // Méthodes utilitaires
    public boolean hasResults() {
        return content != null && !content.isEmpty();
    }
    
    public boolean hasNextPage() {
        return hasNext;
    }
    
    public boolean hasPreviousPage() {
        return hasPrevious;
    }

    // Getters et Setters
    public List<RdqResponse> getContent() { return content; }
    public void setContent(List<RdqResponse> content) { this.content = content; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public boolean isFirst() { return first; }
    public void setFirst(boolean first) { this.first = first; }

    public boolean isLast() { return last; }
    public void setLast(boolean last) { this.last = last; }

    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }

    public boolean isHasPrevious() { return hasPrevious; }
    public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }

    public RdqSearchStats getSearchStats() { return searchStats; }
    public void setSearchStats(RdqSearchStats searchStats) { this.searchStats = searchStats; }

    public RdqSearchCriteria getAppliedCriteria() { return appliedCriteria; }
    public void setAppliedCriteria(RdqSearchCriteria appliedCriteria) { this.appliedCriteria = appliedCriteria; }

    // Méthode builder statique pour compatibilité
    public static RdqSearchResponse builder() {
        return new RdqSearchResponse();
    }

    // Méthodes fluent API pour compatibilité builder
    public RdqSearchResponse content(List<RdqResponse> content) { this.content = content; return this; }
    public RdqSearchResponse page(int page) { this.page = page; return this; }
    public RdqSearchResponse size(int size) { this.size = size; return this; }
    public RdqSearchResponse totalElements(long totalElements) { this.totalElements = totalElements; return this; }
    public RdqSearchResponse totalPages(int totalPages) { this.totalPages = totalPages; return this; }
    public RdqSearchResponse first(boolean first) { this.first = first; return this; }
    public RdqSearchResponse last(boolean last) { this.last = last; return this; }
    public RdqSearchResponse hasNext(boolean hasNext) { this.hasNext = hasNext; return this; }
    public RdqSearchResponse hasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; return this; }
    public RdqSearchResponse searchStats(RdqSearchStats searchStats) { this.searchStats = searchStats; return this; }
    public RdqSearchResponse appliedCriteria(RdqSearchCriteria appliedCriteria) { this.appliedCriteria = appliedCriteria; return this; }
    public RdqSearchResponse rdqs(List<RdqResponse> rdqs) { this.content = rdqs; return this; } // Alias pour compatibilité
    
    public RdqSearchResponse build() { return this; }

    // Méthode alias pour compatibilité
    public List<RdqResponse> getRdqs() { return content; }
    
    // Méthodes pour définir la page courante et la taille
    public RdqSearchResponse currentPage(int page) {
        this.page = page;
        return this;
    }
    
    public RdqSearchResponse pageSize(int size) {
        this.size = size;
        return this;
    }
    
    public RdqSearchResponse pageSize(Integer size) {
        this.size = size != null ? size : 0;
        return this;
    }
    
    public RdqSearchResponse appliedFiltersDescription(String description) {
        // Cette méthode peut être implémentée si nécessaire pour afficher une description des filtres
        return this;
    }
    
    public RdqSearchResponse searchDuration(long duration) {
        // Méthode pour stocker la durée de recherche si nécessaire
        return this;
    }
    
    public RdqSearchResponse stats(RdqSearchStats stats) {
        this.searchStats = stats;
        return this;
    }

    /**
     * Statistiques de recherche pour améliorer l'expérience utilisateur
     */
    public static class RdqSearchStats {
        
        private long totalRdqsEnCours;
        private long totalRdqsTermines;
        private long totalRdqsAnnules;
        private long totalRdqsClos;
        private long totalRdqsPlanifies;
        
        // Répartition par mode
        private long totalPresentiel;
        private long totalDistanciel;
        private long totalHybride;
        
        // Moyennes pour insights
        private double averageNoteBilan;
        private long totalAvecBilans;

        // Constructeurs
        public RdqSearchStats() {}

        public RdqSearchStats(long totalRdqsEnCours, long totalRdqsTermines, long totalRdqsAnnules,
                            long totalRdqsClos, long totalRdqsPlanifies, long totalPresentiel,
                            long totalDistanciel, long totalHybride, double averageNoteBilan,
                            long totalAvecBilans) {
            this.totalRdqsEnCours = totalRdqsEnCours;
            this.totalRdqsTermines = totalRdqsTermines;
            this.totalRdqsAnnules = totalRdqsAnnules;
            this.totalRdqsClos = totalRdqsClos;
            this.totalRdqsPlanifies = totalRdqsPlanifies;
            this.totalPresentiel = totalPresentiel;
            this.totalDistanciel = totalDistanciel;
            this.totalHybride = totalHybride;
            this.averageNoteBilan = averageNoteBilan;
            this.totalAvecBilans = totalAvecBilans;
        }

        // Getters et Setters
        public long getTotalRdqsEnCours() { return totalRdqsEnCours; }
        public void setTotalRdqsEnCours(long totalRdqsEnCours) { this.totalRdqsEnCours = totalRdqsEnCours; }

        public long getTotalRdqsTermines() { return totalRdqsTermines; }
        public void setTotalRdqsTermines(long totalRdqsTermines) { this.totalRdqsTermines = totalRdqsTermines; }

        public long getTotalRdqsAnnules() { return totalRdqsAnnules; }
        public void setTotalRdqsAnnules(long totalRdqsAnnules) { this.totalRdqsAnnules = totalRdqsAnnules; }

        public long getTotalRdqsClos() { return totalRdqsClos; }
        public void setTotalRdqsClos(long totalRdqsClos) { this.totalRdqsClos = totalRdqsClos; }

        public long getTotalRdqsPlanifies() { return totalRdqsPlanifies; }
        public void setTotalRdqsPlanifies(long totalRdqsPlanifies) { this.totalRdqsPlanifies = totalRdqsPlanifies; }

        public long getTotalPresentiel() { return totalPresentiel; }
        public void setTotalPresentiel(long totalPresentiel) { this.totalPresentiel = totalPresentiel; }

        public long getTotalDistanciel() { return totalDistanciel; }
        public void setTotalDistanciel(long totalDistanciel) { this.totalDistanciel = totalDistanciel; }

        public long getTotalHybride() { return totalHybride; }
        public void setTotalHybride(long totalHybride) { this.totalHybride = totalHybride; }

        public double getAverageNoteBilan() { return averageNoteBilan; }
        public void setAverageNoteBilan(double averageNoteBilan) { this.averageNoteBilan = averageNoteBilan; }

        public long getTotalAvecBilans() { return totalAvecBilans; }
        public void setTotalAvecBilans(long totalAvecBilans) { this.totalAvecBilans = totalAvecBilans; }

        // Méthode builder statique pour compatibilité
        public static RdqSearchStats builder() {
            return new RdqSearchStats();
        }

        // Méthodes fluent API pour compatibilité builder
        public RdqSearchStats totalRdqsEnCours(long totalRdqsEnCours) { this.totalRdqsEnCours = totalRdqsEnCours; return this; }
        public RdqSearchStats totalRdqsTermines(long totalRdqsTermines) { this.totalRdqsTermines = totalRdqsTermines; return this; }
        public RdqSearchStats totalRdqsAnnules(long totalRdqsAnnules) { this.totalRdqsAnnules = totalRdqsAnnules; return this; }
        public RdqSearchStats totalRdqsClos(long totalRdqsClos) { this.totalRdqsClos = totalRdqsClos; return this; }
        public RdqSearchStats totalRdqsPlanifies(long totalRdqsPlanifies) { this.totalRdqsPlanifies = totalRdqsPlanifies; return this; }
        public RdqSearchStats totalPresentiel(long totalPresentiel) { this.totalPresentiel = totalPresentiel; return this; }
        
        // Méthodes alias pour compatibilité
        public RdqSearchStats rdqsPlanifies(Long count) { 
            this.totalRdqsPlanifies = count != null ? count : 0L; 
            return this; 
        }
        public RdqSearchStats rdqsPlanifies(long count) { 
            this.totalRdqsPlanifies = count; 
            return this; 
        }
        public RdqSearchStats rdqsEnCours(Long count) { 
            this.totalRdqsEnCours = count != null ? count : 0L; 
            return this; 
        }
        public RdqSearchStats rdqsEnCours(long count) { 
            this.totalRdqsEnCours = count; 
            return this; 
        }
        public RdqSearchStats rdqsTermines(Long count) { 
            this.totalRdqsTermines = count != null ? count : 0L; 
            return this; 
        }
        public RdqSearchStats rdqsTermines(long count) { 
            this.totalRdqsTermines = count; 
            return this; 
        }
        public RdqSearchStats rdqsAnnules(Long count) { 
            this.totalRdqsAnnules = count != null ? count : 0L; 
            return this; 
        }
        public RdqSearchStats rdqsAnnules(long count) { 
            this.totalRdqsAnnules = count; 
            return this; 
        }
        public RdqSearchStats rdqsClos(Long count) { 
            this.totalRdqsClos = count != null ? count : 0L; 
            return this; 
        }
        public RdqSearchStats rdqsClos(long count) { 
            this.totalRdqsClos = count; 
            return this; 
        }
        public RdqSearchStats totalDistanciel(long totalDistanciel) { this.totalDistanciel = totalDistanciel; return this; }
        public RdqSearchStats totalHybride(long totalHybride) { this.totalHybride = totalHybride; return this; }
        public RdqSearchStats averageNoteBilan(double averageNoteBilan) { this.averageNoteBilan = averageNoteBilan; return this; }
        public RdqSearchStats totalAvecBilans(long totalAvecBilans) { this.totalAvecBilans = totalAvecBilans; return this; }
        public RdqSearchStats totalRdqs(Long total) { return this; } // Méthode de compatibilité (pas stockée)
        public RdqSearchStats totalRdqs(long total) { return this; } // Méthode de compatibilité (pas stockée)
        
        public RdqSearchStats build() { return this; }
    }
}