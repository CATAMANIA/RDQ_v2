package com.vibecoding.rdq.repository;

import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.entity.RDQ.StatutRDQ;
import com.vibecoding.rdq.entity.RDQ.ModeRDQ;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RDQRepository extends JpaRepository<RDQ, Long>, JpaSpecificationExecutor<RDQ> {

    List<RDQ> findByStatut(StatutRDQ statut);

    List<RDQ> findByMode(ModeRDQ mode);

    List<RDQ> findByManagerIdManager(Long managerId);

    List<RDQ> findByProjetIdProjet(Long projetId);

    @Query("SELECT r FROM RDQ r WHERE r.dateHeure BETWEEN :startDate AND :endDate")
    List<RDQ> findByDateHeureBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM RDQ r LEFT JOIN FETCH r.collaborateurs WHERE r.idRdq = :id")
    Optional<RDQ> findByIdWithCollaborateurs(@Param("id") Long id);

    @Query("SELECT r FROM RDQ r LEFT JOIN FETCH r.documents WHERE r.idRdq = :id")
    Optional<RDQ> findByIdWithDocuments(@Param("id") Long id);

    @Query("SELECT r FROM RDQ r LEFT JOIN FETCH r.bilans WHERE r.idRdq = :id")
    Optional<RDQ> findByIdWithBilans(@Param("id") Long id);

    @Query("SELECT r FROM RDQ r LEFT JOIN FETCH r.collaborateurs LEFT JOIN FETCH r.documents LEFT JOIN FETCH r.bilans WHERE r.idRdq = :id")
    Optional<RDQ> findByIdWithAllRelations(@Param("id") Long id);

    @Query("SELECT r FROM RDQ r JOIN r.collaborateurs c WHERE c.idCollaborateur = :collaborateurId")
    List<RDQ> findByCollaborateurId(@Param("collaborateurId") Long collaborateurId);

    @Query("SELECT r FROM RDQ r WHERE r.titre LIKE %:titre%")
    List<RDQ> findByTitreContaining(@Param("titre") String titre);

    // ========== Nouvelles méthodes pour TM-41 - Recherche et filtrage avancé ==========

    /**
     * Recherche paginée avec critères multiples
     * Recherche textuelle dans titre, description et indications
     */
    @Query("""
        SELECT DISTINCT r FROM RDQ r 
        LEFT JOIN FETCH r.manager m 
        LEFT JOIN FETCH r.projet p 
        LEFT JOIN FETCH p.client c 
        LEFT JOIN FETCH r.collaborateurs collab 
        WHERE (:searchTerm IS NULL OR :searchTerm = '' OR 
               LOWER(r.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR 
               LOWER(r.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR 
               LOWER(r.indications) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        AND (:managerId IS NULL OR r.manager.idManager = :managerId)
        AND (:clientId IS NULL OR p.client.idClient = :clientId)
        AND (:projetId IS NULL OR p.idProjet = :projetId)
        AND (:dateDebut IS NULL OR r.dateHeure >= :dateDebut)
        AND (:dateFin IS NULL OR r.dateHeure <= :dateFin)
        """)
    Page<RDQ> findWithCriteria(
        @Param("searchTerm") String searchTerm,
        @Param("managerId") Long managerId,
        @Param("clientId") Long clientId,
        @Param("projetId") Long projetId,
        @Param("dateDebut") LocalDateTime dateDebut,
        @Param("dateFin") LocalDateTime dateFin,
        Pageable pageable
    );

    /**
     * Recherche par nom de client (recherche textuelle)
     */
    @Query("""
        SELECT DISTINCT r FROM RDQ r 
        LEFT JOIN FETCH r.projet p 
        LEFT JOIN FETCH p.client c 
        WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :clientNom, '%'))
        """)
    Page<RDQ> findByClientNomContaining(@Param("clientNom") String clientNom, Pageable pageable);

    /**
     * Recherche par nom de collaborateur assigné
     */
    @Query("""
        SELECT DISTINCT r FROM RDQ r 
        LEFT JOIN FETCH r.collaborateurs c 
        WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :collaborateurNom, '%'))
        """)
    Page<RDQ> findByCollaborateurNomContaining(@Param("collaborateurNom") String collaborateurNom, Pageable pageable);

    /**
     * Recherche par nom de projet
     */
    @Query("""
        SELECT DISTINCT r FROM RDQ r 
        LEFT JOIN FETCH r.projet p 
        WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :projetNom, '%'))
        """)
    Page<RDQ> findByProjetNomContaining(@Param("projetNom") String projetNom, Pageable pageable);

    /**
     * RDQ assignés à un collaborateur avec filtrage par statut
     */
    @Query("""
        SELECT DISTINCT r FROM RDQ r 
        LEFT JOIN FETCH r.collaborateurs c 
        LEFT JOIN FETCH r.manager m 
        LEFT JOIN FETCH r.projet p 
        WHERE c.idCollaborateur = :collaborateurId 
        AND (:includeHistory = true OR r.statut NOT IN ('TERMINE', 'ANNULE', 'CLOS'))
        ORDER BY r.dateHeure DESC
        """)
    Page<RDQ> findAssignmentsByCollaborateurId(
        @Param("collaborateurId") Long collaborateurId,
        @Param("includeHistory") Boolean includeHistory,
        Pageable pageable
    );

    /**
     * RDQ créés par un manager avec filtrage par statut
     */
    @Query("""
        SELECT DISTINCT r FROM RDQ r 
        LEFT JOIN FETCH r.manager m 
        LEFT JOIN FETCH r.projet p 
        LEFT JOIN FETCH r.collaborateurs c 
        WHERE r.manager.idManager = :managerId 
        AND (:includeHistory = true OR r.statut NOT IN ('TERMINE', 'ANNULE', 'CLOS'))
        ORDER BY r.dateHeure DESC
        """)
    Page<RDQ> findByManagerIdWithHistory(
        @Param("managerId") Long managerId,
        @Param("includeHistory") Boolean includeHistory,
        Pageable pageable
    );

    /**
     * Statistiques pour le dashboard
     */
    @Query("""
        SELECT 
            COUNT(r) as total,
            SUM(CASE WHEN r.statut = 'PLANIFIE' THEN 1 ELSE 0 END) as planifies,
            SUM(CASE WHEN r.statut = 'EN_COURS' THEN 1 ELSE 0 END) as enCours,
            SUM(CASE WHEN r.statut = 'TERMINE' THEN 1 ELSE 0 END) as termines,
            SUM(CASE WHEN r.statut = 'ANNULE' THEN 1 ELSE 0 END) as annules,
            SUM(CASE WHEN r.statut = 'CLOS' THEN 1 ELSE 0 END) as clos
        FROM RDQ r 
        WHERE (:managerId IS NULL OR r.manager.idManager = :managerId)
        """)
    Object[] getStatsByManager(@Param("managerId") Long managerId);
}