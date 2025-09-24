package com.vibecoding.rdq.controller;

import com.vibecoding.rdq.entity.Manager;
import com.vibecoding.rdq.service.ManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/managers")
@Tag(name = "Manager", description = "Gestion des managers")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @GetMapping
    @Operation(summary = "Récupérer tous les managers")
    public ResponseEntity<List<Manager>> getAllManagers() {
        List<Manager> managers = managerService.findAll();
        return ResponseEntity.ok(managers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un manager par ID")
    public ResponseEntity<Manager> getManagerById(@PathVariable Long id) {
        Optional<Manager> manager = managerService.findById(id);
        return manager.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau manager")
    public ResponseEntity<Manager> createManager(@RequestBody Manager manager) {
        if (managerService.existsByEmail(manager.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        Manager savedManager = managerService.save(manager);
        return ResponseEntity.ok(savedManager);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un manager")
    public ResponseEntity<Manager> updateManager(@PathVariable Long id, @RequestBody Manager manager) {
        if (!managerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        manager.setIdManager(id);
        Manager updatedManager = managerService.save(manager);
        return ResponseEntity.ok(updatedManager);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un manager")
    public ResponseEntity<Void> deleteManager(@PathVariable Long id) {
        if (!managerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        managerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}