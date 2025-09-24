package com.vibecoding.rdq.repository;

import com.vibecoding.rdq.entity.Projet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {

    List<Projet> findByNomContainingIgnoreCase(String nom);

    List<Projet> findByClientIdClient(Long clientId);

    @Query("SELECT p FROM Projet p LEFT JOIN FETCH p.rdqs WHERE p.idProjet = :id")
    Optional<Projet> findByIdWithRdqs(@Param("id") Long id);

    @Query("SELECT p FROM Projet p JOIN FETCH p.client WHERE p.idProjet = :id")
    Optional<Projet> findByIdWithClient(@Param("id") Long id);

    @Query("SELECT p FROM Projet p WHERE SIZE(p.rdqs) > 0")
    List<Projet> findProjetsWithRdqs();
}