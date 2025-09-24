package com.vibecoding.rdq.repository;

import com.vibecoding.rdq.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByNomContainingIgnoreCase(String nom);

    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.projets WHERE c.idClient = :id")
    Optional<Client> findByIdWithProjets(@Param("id") Long id);

    @Query("SELECT c FROM Client c WHERE SIZE(c.projets) > 0")
    List<Client> findClientsWithProjets();
}