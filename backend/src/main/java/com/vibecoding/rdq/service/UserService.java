package com.vibecoding.rdq.service;

import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.enums.Role;
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
        return userRepository.findByRole(Role.valueOf(role));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Initialise les données de test (équivalent des mock)
     */
    public void initializeTestData() {
        if (userRepository.count() == 0) {
            // Admin équivalent à mockAdmins
            userRepository.save(new User("Admin", "Système", "admin@rdq.com", Role.ADMIN));
            
            // Managers équivalents à mockManagers
            userRepository.save(new User("Dupont", "Jean", "manager@example.com", Role.MANAGER));
            userRepository.save(new User("Rousseau", "Marie", "marie.rousseau@example.com",  Role.MANAGER));
            
            // Collaborateurs équivalents à mockCollaborateurs
            userRepository.save(new User("Martin", "Sophie", "collaborateur@example.com",  Role.COLLABORATEUR));
            userRepository.save(new User( "Bernard", "Pierre", "pierre.bernard@example.com", Role.COLLABORATEUR));
        }
    }
}