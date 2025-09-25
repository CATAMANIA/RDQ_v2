package com.vibecoding.rdq.controller;

import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.entity.Client;
import com.vibecoding.rdq.entity.Projet;
import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.service.UserService;
import com.vibecoding.rdq.service.ClientService;
import com.vibecoding.rdq.service.ProjetService;
import com.vibecoding.rdq.service.RDQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Contrôleur pour remplacer les données mock du frontend
 * Centralise tous les endpoints nécessaires pour TM-45
 */
@RestController
@RequestMapping("/api/mock-data")
@CrossOrigin(origins = "http://localhost:3000")
public class MockDataController {

    @Autowired
    private UserService userService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ProjetService projetService;

    @Autowired
    private RDQService rdqService;

    /**
     * Récupérer toutes les données nécessaires au frontend en une seule requête
     * Remplace l'importation de mockData.ts
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllMockData() {
        Map<String, Object> data = new HashMap<>();
        
        data.put("managers", userService.findByRole("MANAGER"));
        data.put("collaborateurs", userService.findByRole("COLLABORATEUR"));
        data.put("admins", userService.findByRole("ADMIN"));
        data.put("clients", clientService.findAll());
        data.put("projets", projetService.findAll());
        data.put("rdqs", rdqService.findAll());
        
        return ResponseEntity.ok(data);
    }

    /**
     * Endpoint spécifique pour AdminDashboard.tsx
     */
    @GetMapping("/admin-data")
    public ResponseEntity<Map<String, Object>> getAdminData() {
        Map<String, Object> data = new HashMap<>();
        
        data.put("managers", userService.findByRole("MANAGER"));
        data.put("collaborateurs", userService.findByRole("COLLABORATEUR"));
        data.put("clients", clientService.findAll());
        data.put("admins", userService.findByRole("ADMIN"));
        
        return ResponseEntity.ok(data);
    }

    /**
     * Endpoint spécifique pour CollaborateurDashboard.tsx
     */
    @GetMapping("/collaborateur-data")
    public ResponseEntity<Map<String, Object>> getCollaborateurData() {
        Map<String, Object> data = new HashMap<>();
        
        data.put("rdqs", rdqService.findAll());
        data.put("clients", clientService.findAll());
        
        return ResponseEntity.ok(data);
    }

    /**
     * Endpoint spécifique pour ManagerDashboard.tsx
     */
    @GetMapping("/manager-data")
    public ResponseEntity<Map<String, Object>> getManagerData() {
        Map<String, Object> data = new HashMap<>();
        
        data.put("rdqs", rdqService.findAll());
        data.put("collaborateurs", userService.findByRole("COLLABORATEUR"));
        data.put("clients", clientService.findAll());
        
        return ResponseEntity.ok(data);
    }

    /**
     * Endpoint spécifique pour CreateRDQModal.tsx et RDQModal.tsx
     */
    @GetMapping("/modal-data")
    public ResponseEntity<Map<String, Object>> getModalData() {
        Map<String, Object> data = new HashMap<>();
        
        data.put("collaborateurs", userService.findByRole("COLLABORATEUR"));
        data.put("clients", clientService.findAll());
        data.put("projets", projetService.findAll());
        
        return ResponseEntity.ok(data);
    }

    /**
     * Initialiser les données de test si nécessaire
     */
    @PostMapping("/initialize")
    public ResponseEntity<Map<String, String>> initializeTestData() {
        try {
            userService.initializeTestData();
            clientService.initializeTestData();
            projetService.initializeTestData();
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Données de test initialisées avec succès");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de l'initialisation: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
}