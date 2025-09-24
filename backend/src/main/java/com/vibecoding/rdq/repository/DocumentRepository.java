package com.vibecoding.rdq.repository;

import com.vibecoding.rdq.entity.Document;
import com.vibecoding.rdq.entity.Document.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByRdqIdRdq(Long rdqId);

    List<Document> findByType(TypeDocument type);

    List<Document> findByNomFichierContainingIgnoreCase(String nomFichier);

    @Query("SELECT d FROM Document d WHERE d.rdq.idRdq = :rdqId AND d.type = :type")
    List<Document> findByRdqIdAndType(@Param("rdqId") Long rdqId, @Param("type") TypeDocument type);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.rdq.idRdq = :rdqId")
    Long countByRdqId(@Param("rdqId") Long rdqId);
}