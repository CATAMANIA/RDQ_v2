package com.vibecoding.rdq.service;

import com.vibecoding.rdq.entity.Projet;
import com.vibecoding.rdq.repository.ProjetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des projets
 * Remplace les données mock mockProjets
 */
@Service
public class ProjetService {

    @Autowired
    private ProjetRepository projetRepository;

    public List<Projet> findAll() {
        return projetRepository.findAll();
    }

    public Optional<Projet> findById(Long id) {
        return projetRepository.findById(id);
    }

    public List<Projet> findByNomContaining(String nom) {
        return projetRepository.findByNomContainingIgnoreCase(nom);
    }

    public Projet save(Projet projet) {
        return projetRepository.save(projet);
    }

    public void deleteById(Long id) {
        projetRepository.deleteById(id);
    }


}