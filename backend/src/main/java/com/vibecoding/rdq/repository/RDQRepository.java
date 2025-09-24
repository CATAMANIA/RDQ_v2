package com.vibecoding.rdq.repository;

import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.entity.RDQ.StatutRDQ;
import com.vibecoding.rdq.entity.RDQ.ModeRDQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RDQRepository extends JpaRepository<RDQ, Long> {

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
}