package com.vibecoding.rdq.controller;

import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des utilisateurs (managers, collaborateurs, admins)
 * Remplace les données mock du frontend
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Récupérer tous les utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * Récupérer les managers uniquement
     */
    @GetMapping("/managers")
    public ResponseEntity<List<User>> getManagers() {
        List<User> managers = userService.findByRole("MANAGER");
        return ResponseEntity.ok(managers);
    }

    /**
     * Récupérer les collaborateurs uniquement
     */
    @GetMapping("/collaborateurs")
    public ResponseEntity<List<User>> getCollaborateurs() {
        List<User> collaborateurs = userService.findByRole("COLLABORATEUR");
        return ResponseEntity.ok(collaborateurs);
    }

    /**
     * Récupérer les admins uniquement
     */
    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAdmins() {
        List<User> admins = userService.findByRole("ADMIN");
        return ResponseEntity.ok(admins);
    }

    /**
     * Récupérer un utilisateur par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Créer un nouvel utilisateur
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.save(user);
        return ResponseEntity.ok(savedUser);
    }

    /**
     * Mettre à jour un utilisateur
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        if (!userService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        user.setIdUser(id);
        User updatedUser = userService.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Supprimer un utilisateur
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}