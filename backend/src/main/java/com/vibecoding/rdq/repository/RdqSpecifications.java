package com.vibecoding.rdq.repository;

import com.vibecoding.rdq.dto.RdqSearchCriteria;
import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.entity.RDQ.StatutRDQ;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Spécifications JPA pour les recherches dynamiques de RDQ
 * Implémentation pour TM-41 - US09 Historique et filtrage des RDQ
 */
public class RdqSpecifications {

    /**
     * Créer une spécification basée sur les critères de recherche
     */
    public static Specification<RDQ> createSpecification(RdqSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Jointures pour optimiser les requêtes
            Join<Object, Object> managerJoin = root.join("manager", JoinType.LEFT);
            Join<Object, Object> projetJoin = root.join("projet", JoinType.LEFT);
            Join<Object, Object> clientJoin = projetJoin.join("client", JoinType.LEFT);
            Join<Object, Object> collaborateursJoin = root.join("collaborateurs", JoinType.LEFT);

            // Filtrage par client
            if (criteria.getClientId() != null) {
                predicates.add(criteriaBuilder.equal(clientJoin.get("idClient"), criteria.getClientId()));
            }
            if (criteria.getClientNom() != null && !criteria.getClientNom().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(clientJoin.get("nom")),
                    "%" + criteria.getClientNom().toLowerCase() + "%"
                ));
            }

            // Filtrage par collaborateur
            if (criteria.getCollaborateurId() != null) {
                predicates.add(criteriaBuilder.equal(collaborateursJoin.get("idCollaborateur"), criteria.getCollaborateurId()));
            }
            if (criteria.getCollaborateurNom() != null && !criteria.getCollaborateurNom().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(collaborateursJoin.get("nom")),
                    "%" + criteria.getCollaborateurNom().toLowerCase() + "%"
                ));
            }

            // Filtrage par manager
            if (criteria.getManagerId() != null) {
                predicates.add(criteriaBuilder.equal(managerJoin.get("idManager"), criteria.getManagerId()));
            }

            // Filtrage par projet
            if (criteria.getProjetId() != null) {
                predicates.add(criteriaBuilder.equal(projetJoin.get("idProjet"), criteria.getProjetId()));
            }
            if (criteria.getProjetNom() != null && !criteria.getProjetNom().trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(projetJoin.get("nom")),
                    "%" + criteria.getProjetNom().toLowerCase() + "%"
                ));
            }

            // Filtrage par statut
            if (criteria.getStatuts() != null && !criteria.getStatuts().isEmpty()) {
                predicates.add(root.get("statut").in(criteria.getStatuts()));
            }

            // Filtrage par mode
            if (criteria.getModes() != null && !criteria.getModes().isEmpty()) {
                List<RDQ.ModeRDQ> modeEnums = criteria.getModes().stream()
                    .map(RDQ.ModeRDQ::valueOf)
                    .toList();
                predicates.add(root.get("mode").in(modeEnums));
            }

            // Filtrage par date
            if (criteria.getDateDebut() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateHeure"), criteria.getDateDebut()));
            }
            if (criteria.getDateFin() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateHeure"), criteria.getDateFin()));
            }

            // Recherche textuelle
            if (criteria.getSearchTerm() != null && !criteria.getSearchTerm().trim().isEmpty()) {
                String searchPattern = "%" + criteria.getSearchTerm().toLowerCase() + "%";
                
                Predicate titreMatch = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("titre")), searchPattern);
                Predicate descriptionMatch = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), searchPattern);
                Predicate indicationsMatch = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("indications")), searchPattern);
                
                predicates.add(criteriaBuilder.or(titreMatch, descriptionMatch, indicationsMatch));
            }

            // Filtrage de l'historique
            if (!criteria.getIncludeHistory()) {
                predicates.add(criteriaBuilder.not(
                    root.get("statut").in(StatutRDQ.TERMINE, StatutRDQ.ANNULE, StatutRDQ.CLOS)
                ));
            }

            // Filtres spécifiques manager
            if (criteria.getMyRdqsOnly() && criteria.getManagerId() != null) {
                predicates.add(criteriaBuilder.equal(managerJoin.get("idManager"), criteria.getManagerId()));
            }

            // Filtres spécifiques collaborateur
            if (criteria.getMyAssignmentsOnly() && criteria.getCollaborateurId() != null) {
                predicates.add(criteriaBuilder.equal(collaborateursJoin.get("idCollaborateur"), criteria.getCollaborateurId()));
            }

            // Assurer la distinct sur les résultats pour éviter les doublons des jointures
            query.distinct(true);

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Spécification pour les RDQ d'un manager avec historique optionnel
     */
    public static Specification<RDQ> forManager(Long managerId, boolean includeHistory) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            predicates.add(criteriaBuilder.equal(root.get("manager").get("idManager"), managerId));
            
            if (!includeHistory) {
                predicates.add(criteriaBuilder.not(
                    root.get("statut").in(StatutRDQ.TERMINE, StatutRDQ.ANNULE, StatutRDQ.CLOS)
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Spécification pour les RDQ assignés à un collaborateur
     */
    public static Specification<RDQ> forCollaborateur(Long collaborateurId, boolean includeHistory) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            Join<Object, Object> collaborateursJoin = root.join("collaborateurs", JoinType.INNER);
            predicates.add(criteriaBuilder.equal(collaborateursJoin.get("idCollaborateur"), collaborateurId));
            
            if (!includeHistory) {
                predicates.add(criteriaBuilder.not(
                    root.get("statut").in(StatutRDQ.TERMINE, StatutRDQ.ANNULE, StatutRDQ.CLOS)
                ));
            }
            
            query.distinct(true);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Spécification pour la recherche dans une période donnée
     */
    public static Specification<RDQ> inDateRange(LocalDateTime dateDebut, LocalDateTime dateFin) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (dateDebut != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateHeure"), dateDebut));
            }
            if (dateFin != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dateHeure"), dateFin));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}