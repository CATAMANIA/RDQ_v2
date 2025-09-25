package com.vibecoding.rdq.controller;

import com.vibecoding.rdq.entity.Projet;
import com.vibecoding.rdq.service.ProjetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des projets
 * Remplace les données mock mockProjets
 */
@RestController
@RequestMapping("/api/projets")
@CrossOrigin(origins = "http://localhost:3000")
public class ProjetController {

    @Autowired
    private ProjetService projetService;

    /**
     * Récupérer tous les projets
     */
    @GetMapping
    public ResponseEntity<List<Projet>> getAllProjets() {
        List<Projet> projets = projetService.findAll();
        return ResponseEntity.ok(projets);
    }

    /**
     * Récupérer un projet par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Projet> getProjetById(@PathVariable Long id) {
        Optional<Projet> projet = projetService.findById(id);
        return projet.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Rechercher des projets par nom
     */
    @GetMapping("/search")
    public ResponseEntity<List<Projet>> searchProjetsByName(@RequestParam String nom) {
        List<Projet> projets = projetService.findByNomContaining(nom);
        return ResponseEntity.ok(projets);
    }

    /**
     * Créer un nouveau projet
     */
    @PostMapping
    public ResponseEntity<Projet> createProjet(@RequestBody Projet projet) {
        Projet savedProjet = projetService.save(projet);
        return ResponseEntity.ok(savedProjet);
    }

    /**
     * Mettre à jour un projet
     */
    @PutMapping("/{id}")
    public ResponseEntity<Projet> updateProjet(@PathVariable Long id, @RequestBody Projet projet) {
        if (!projetService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        projet.setIdProjet(id);
        Projet updatedProjet = projetService.save(projet);
        return ResponseEntity.ok(updatedProjet);
    }

    /**
     * Supprimer un projet
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjet(@PathVariable Long id) {
        if (!projetService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        projetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}