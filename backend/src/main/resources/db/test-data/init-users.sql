-- Script d'initialisation des utilisateurs de test pour TM-33
-- À exécuter après la création des tables

-- Insertion d'utilisateurs de test avec mots de passe cryptés (BCrypt)
-- Mot de passe par défaut: "password123" pour tous les utilisateurs de test

INSERT INTO users (username, email, password, role, enabled, account_non_expired, account_non_locked, credentials_non_expired, created_at, updated_at)
VALUES 
    -- Administrateur système
    ('admin', 'admin@rdq.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGAYBEKrwSUyerOLEDOlkqk4C', 'ADMIN', true, true, true, true, NOW(), NOW()),
    
    -- Manager de test
    ('manager1', 'manager1@rdq.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGAYBEKrwSUyerOLEDOlkqk4C', 'MANAGER', true, true, true, true, NOW(), NOW()),
    ('manager2', 'manager2@rdq.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGAYBEKrwSUyerOLEDOlkqk4C', 'MANAGER', true, true, true, true, NOW(), NOW()),
    
    -- Collaborateurs de test
    ('collab1', 'collab1@rdq.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGAYBEKrwSUyerOLEDOlkqk4C', 'COLLABORATEUR', true, true, true, true, NOW(), NOW()),
    ('collab2', 'collab2@rdq.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGAYBEKrwSUyerOLEDOlkqk4C', 'COLLABORATEUR', true, true, true, true, NOW(), NOW()),
    ('collab3', 'collab3@rdq.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGAYBEKrwSUyerOLEDOlkqk4C', 'COLLABORATEUR', true, true, true, true, NOW(), NOW());

-- Création de profils Manager correspondants
INSERT INTO managers (nom, prenom, email, telephone, id_user)
VALUES 
    ('Dupont', 'Jean', 'manager1@rdq.com', '0123456789', (SELECT id_user FROM users WHERE username = 'manager1')),
    ('Martin', 'Marie', 'manager2@rdq.com', '0123456790', (SELECT id_user FROM users WHERE username = 'manager2'));

-- Création de profils Collaborateur correspondants
INSERT INTO collaborateurs (nom, prenom, email, telephone, id_user)
VALUES 
    ('Durand', 'Pierre', 'collab1@rdq.com', '0123456791', (SELECT id_user FROM users WHERE username = 'collab1')),
    ('Lefebvre', 'Sophie', 'collab2@rdq.com', '0123456792', (SELECT id_user FROM users WHERE username = 'collab2')),
    ('Moreau', 'Luc', 'collab3@rdq.com', '0123456793', (SELECT id_user FROM users WHERE username = 'collab3'));

-- Mise à jour des relations dans l'autre sens (optionnel selon la stratégie JPA)
UPDATE users SET manager_id = (SELECT id_manager FROM managers WHERE id_user = users.id_user) WHERE username IN ('manager1', 'manager2');
UPDATE users SET collaborateur_id = (SELECT id_collaborateur FROM collaborateurs WHERE id_user = users.id_user) WHERE username IN ('collab1', 'collab2', 'collab3');

-- Affichage des utilisateurs créés pour vérification
SELECT u.id_user, u.username, u.email, u.role, u.enabled, 
       CASE 
           WHEN m.id_manager IS NOT NULL THEN CONCAT(m.prenom, ' ', m.nom, ' (Manager)')
           WHEN c.id_collaborateur IS NOT NULL THEN CONCAT(c.prenom, ' ', c.nom, ' (Collaborateur)')
           ELSE 'Profil administrateur'
       END as profil
FROM users u
LEFT JOIN managers m ON u.id_user = m.id_user
LEFT JOIN collaborateurs c ON u.id_user = c.id_user
ORDER BY u.role, u.username;