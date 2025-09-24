package com.vibecoding.rdq.repository;

import com.vibecoding.rdq.entity.Bilan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BilanRepository extends JpaRepository<Bilan, Long> {

    List<Bilan> findByRdqIdRdq(Long rdqId);

    List<Bilan> findByAuteur(String auteur);

    List<Bilan> findByNote(Integer note);

    @Query("SELECT b FROM Bilan b WHERE b.rdq.idRdq = :rdqId ORDER BY b.dateCreation DESC")
    List<Bilan> findByRdqIdOrderByDateCreationDesc(@Param("rdqId") Long rdqId);

    @Query("SELECT AVG(b.note) FROM Bilan b WHERE b.rdq.idRdq = :rdqId")
    Double getAverageNoteByRdqId(@Param("rdqId") Long rdqId);

    @Query("SELECT b FROM Bilan b WHERE b.note >= 4")
    List<Bilan> findPositiveBilans();

    @Query("SELECT b FROM Bilan b WHERE b.note <= 2")
    List<Bilan> findNegativeBilans();

    @Query("SELECT COUNT(b) FROM Bilan b WHERE b.rdq.idRdq = :rdqId")
    Long countByRdqId(@Param("rdqId") Long rdqId);
}