package com.vibecoding.rdq.controller;

import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.service.RDQService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rdq")
@Tag(name = "RDQ", description = "Gestion des RDQ")
public class RDQController {

    @Autowired
    private RDQService rdqService;

    @GetMapping
    @Operation(summary = "Récupérer toutes les RDQ")
    public ResponseEntity<List<RDQ>> getAllRDQ() {
        List<RDQ> rdqs = rdqService.findAll();
        return ResponseEntity.ok(rdqs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une RDQ par ID")
    public ResponseEntity<RDQ> getRDQById(@PathVariable Long id) {
        Optional<RDQ> rdq = rdqService.findById(id);
        return rdq.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle RDQ")
    public ResponseEntity<RDQ> createRDQ(@RequestBody RDQ rdq) {
        RDQ savedRdq = rdqService.save(rdq);
        return ResponseEntity.ok(savedRdq);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une RDQ")
    public ResponseEntity<RDQ> updateRDQ(@PathVariable Long id, @RequestBody RDQ rdq) {
        if (!rdqService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        rdq.setIdRdq(id);
        RDQ updatedRdq = rdqService.save(rdq);
        return ResponseEntity.ok(updatedRdq);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une RDQ")
    public ResponseEntity<Void> deleteRDQ(@PathVariable Long id) {
        if (!rdqService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        rdqService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/manager/{managerId}")
    @Operation(summary = "Récupérer les RDQ d'un manager")
    public ResponseEntity<List<RDQ>> getRDQByManager(@PathVariable Long managerId) {
        List<RDQ> rdqs = rdqService.findByManagerId(managerId);
        return ResponseEntity.ok(rdqs);
    }

    @GetMapping("/projet/{projetId}")
    @Operation(summary = "Récupérer les RDQ d'un projet")
    public ResponseEntity<List<RDQ>> getRDQByProjet(@PathVariable Long projetId) {
        List<RDQ> rdqs = rdqService.findByProjetId(projetId);
        return ResponseEntity.ok(rdqs);
    }
}