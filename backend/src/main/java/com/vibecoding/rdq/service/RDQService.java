package com.vibecoding.rdq.service;

import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.repository.RDQRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RDQService {

    @Autowired
    private RDQRepository rdqRepository;

    public List<RDQ> findAll() {
        return rdqRepository.findAll();
    }

    public Optional<RDQ> findById(Long id) {
        return rdqRepository.findById(id);
    }

    public RDQ save(RDQ rdq) {
        return rdqRepository.save(rdq);
    }

    public void deleteById(Long id) {
        rdqRepository.deleteById(id);
    }

    public List<RDQ> findByStatut(RDQ.StatutRDQ statut) {
        return rdqRepository.findByStatut(statut);
    }

    public List<RDQ> findByManagerId(Long managerId) {
        return rdqRepository.findByManagerIdManager(managerId);
    }

    public List<RDQ> findByProjetId(Long projetId) {
        return rdqRepository.findByProjetIdProjet(projetId);
    }

    public List<RDQ> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return rdqRepository.findByDateHeureBetween(startDate, endDate);
    }

    public Optional<RDQ> findByIdWithAllRelations(Long id) {
        return rdqRepository.findByIdWithAllRelations(id);
    }
}