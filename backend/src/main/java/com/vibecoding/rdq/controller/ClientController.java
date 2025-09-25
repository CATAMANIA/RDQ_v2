package com.vibecoding.rdq.controller;

import com.vibecoding.rdq.entity.Client;
import com.vibecoding.rdq.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des clients
 * Remplace les données mock mockClients
 */
@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "http://localhost:3000")
public class ClientController {

    @Autowired
    private ClientService clientService;

    /**
     * Récupérer tous les clients
     */
    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.findAll();
        return ResponseEntity.ok(clients);
    }

    /**
     * Récupérer un client par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        Optional<Client> client = clientService.findById(id);
        return client.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Rechercher des clients par nom
     */
    @GetMapping("/search")
    public ResponseEntity<List<Client>> searchClientsByName(@RequestParam String nom) {
        List<Client> clients = clientService.findByNomContaining(nom);
        return ResponseEntity.ok(clients);
    }

    /**
     * Créer un nouveau client
     */
    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client savedClient = clientService.save(client);
        return ResponseEntity.ok(savedClient);
    }

    /**
     * Mettre à jour un client
     */
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client client) {
        if (!clientService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        client.setIdClient(id);
        Client updatedClient = clientService.save(client);
        return ResponseEntity.ok(updatedClient);
    }

    /**
     * Supprimer un client
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        if (!clientService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        clientService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}