package com.vibecoding.rdq.service;

import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des utilisateurs
 * Remplace les données mock mockManagers, mockCollaborateurs, mockAdmins
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Initialise les données de test (équivalent des mock)
     */
    public void initializeTestData() {
        if (userRepository.count() == 0) {
            // Admin équivalent à mockAdmins
            userRepository.save(new User(null, "Admin", "Système", "admin@rdq.com", "01.00.00.00.00", "ADMIN"));
            
            // Managers équivalents à mockManagers
            userRepository.save(new User(null, "Dupont", "Jean", "manager@example.com", "01.23.45.67.89", "MANAGER"));
            userRepository.save(new User(null, "Rousseau", "Marie", "marie.rousseau@example.com", "01.22.33.44.55", "MANAGER"));
            
            // Collaborateurs équivalents à mockCollaborateurs
            userRepository.save(new User(null, "Martin", "Sophie", "collaborateur@example.com", "01.98.76.54.32", "COLLABORATEUR"));
            userRepository.save(new User(null, "Bernard", "Pierre", "pierre.bernard@example.com", "01.11.22.33.44", "COLLABORATEUR"));
        }
    }
}