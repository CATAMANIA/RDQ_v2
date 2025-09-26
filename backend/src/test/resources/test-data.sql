-- Script SQL spécifique pour les tests
-- Utilisé avec @Sql dans les classes de test
-- Version simplifiée pour les tests unitaires

-- Données de test pour les utilisateurs
INSERT INTO users (id, nom, prenom, email, mot_de_passe, role) VALUES 
(1, 'TestUser', 'Test', 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8WrBgQqh2gbgkJ5bju', 'ADMIN'),
(2, 'Manager', 'Test', 'manager@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8WrBgQqh2gbgkJ5bju', 'MANAGER'),
(3, 'Collaborateur', 'Test', 'collab@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8WrBgQqh2gbgkJ5bju', 'COLLABORATEUR');

-- Données de test pour les clients
INSERT INTO clients (id, nom, contact_principal, telephone, email) VALUES 
(1, 'Test Client', 'Contact Test', '01.23.45.67.89', 'test@client.com'),
(2, 'Client 2', 'Contact 2', '01.98.76.54.32', 'contact2@client.com');

-- Données de test pour les projets
INSERT INTO projets (id, nom) VALUES 
(1, 'Test Project'),
(2, 'Another Project');