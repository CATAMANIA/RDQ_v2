package com.vibecoding.rdq.repository;

import com.vibecoding.rdq.entity.Collaborateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollaborateurRepository extends JpaRepository<Collaborateur, Long> {

    Optional<Collaborateur> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT c FROM Collaborateur c WHERE c.nom LIKE %:nom% OR c.prenom LIKE %:prenom%")
    java.util.List<Collaborateur> findByNomOrPrenomContaining(@Param("nom") String nom, @Param("prenom") String prenom);

    @Query("SELECT c FROM Collaborateur c LEFT JOIN FETCH c.rdqs WHERE c.idCollaborateur = :id")
    Optional<Collaborateur> findByIdWithRdqs(@Param("id") Long id);
}