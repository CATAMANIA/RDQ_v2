-- Script d'initialisation des données de test pour RDQ_v2
-- Ce script est exécuté automatiquement par Spring Boot au démarrage
-- Remplace les méthodes initializeTestData() supprimées des services

-- Insertion des utilisateurs de test
INSERT INTO users (id_user, username, email, password, role, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_at, updated_at) VALUES 
(1, 'jean.dupont', 'jean.dupont@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8WrBgQqh2gbgkJ5bju', 'ADMIN', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'sophie.martin', 'sophie.martin@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8WrBgQqh2gbgkJ5bju', 'MANAGER', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'pierre.durand', 'pierre.durand@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8WrBgQqh2gbgkJ5bju', 'COLLABORATEUR', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'marie.leblanc', 'marie.leblanc@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8WrBgQqh2gbgkJ5bju', 'COLLABORATEUR', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'carlos.garcia', 'carlos.garcia@email.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8WrBgQqh2gbgkJ5bju', 'MANAGER', true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insertion des clients de test
INSERT INTO clients (id_client, nom, contact, telephone, email) VALUES 
(1, 'ACME Corp', 'M. Durand', '01.55.66.77.88', 'durand@acme.com'),
(2, 'TechSolutions', 'Mme Leblanc', '01.44.55.66.77', 'leblanc@techsolutions.com'),
(3, 'Digital Innovations', 'M. Martin', '01.33.44.55.66', 'martin@digital-innovations.com'),
(4, 'StartupX', 'Mme Garcia', '01.22.33.44.55', 'garcia@startupx.com'),
(5, 'Global Systems', 'M. Rodriguez', '01.11.22.33.44', 'rodriguez@global-systems.com');

-- Insertion des projets de test
INSERT INTO projets (id_projet, nom, id_client) VALUES 
(1, 'Migration Cloud', 1),
(2, 'Appel d''offre AO-2024-001', 2),
(3, 'Développement mobile', 3),
(4, 'Refonte site web', 4),
(5, 'Audit de sécurité', 5),
(6, 'Formation équipe', 1),
(7, 'Support technique', 2),
(8, 'Intégration API', 3);

-- Note: Les séquences sont automatiquement gérées par JPA/Hibernate
-- Pas besoin de les réinitialiser manuellement pour H2