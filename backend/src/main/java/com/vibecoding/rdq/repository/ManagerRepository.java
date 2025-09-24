package com.vibecoding.rdq.repository;

import com.vibecoding.rdq.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {

    Optional<Manager> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT m FROM Manager m WHERE m.nom LIKE %:nom% OR m.prenom LIKE %:prenom%")
    java.util.List<Manager> findByNomOrPrenomContaining(@Param("nom") String nom, @Param("prenom") String prenom);

    @Query("SELECT m FROM Manager m LEFT JOIN FETCH m.rdqs WHERE m.idManager = :id")
    Optional<Manager> findByIdWithRdqs(@Param("id") Long id);
}