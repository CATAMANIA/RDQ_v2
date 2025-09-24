package com.vibecoding.rdq.security;

import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service personnalisé de chargement des détails utilisateur
 * Implémente l'authentification selon les critères TM-33
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        logger.debug("Chargement de l'utilisateur: {}", identifier);

        User user = userRepository.findByUsernameOrEmail(identifier)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé: {}", identifier);
                    return new UsernameNotFoundException("Utilisateur non trouvé: " + identifier);
                });

        logger.debug("Utilisateur trouvé: {} avec le rôle: {}", user.getUsername(), user.getRole());
        return user;
    }

    /**
     * Charge un utilisateur par son ID (utile pour certains cas d'usage)
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        logger.debug("Chargement de l'utilisateur par ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé avec l'ID: {}", userId);
                    return new UsernameNotFoundException("Utilisateur non trouvé avec l'ID: " + userId);
                });

        logger.debug("Utilisateur trouvé par ID: {} - {}", user.getIdUser(), user.getUsername());
        return user;
    }
}