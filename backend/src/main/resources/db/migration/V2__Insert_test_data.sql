-- Données de test pour le développement
INSERT INTO managers (nom, prenom, email, telephone) VALUES
('Dupont', 'Jean', 'jean.dupont@company.com', '0123456789'),
('Martin', 'Marie', 'marie.martin@company.com', '0123456790');

INSERT INTO collaborateurs (nom, prenom, email, telephone) VALUES
('Durand', 'Pierre', 'pierre.durand@company.com', '0123456791'),
('Moreau', 'Sophie', 'sophie.moreau@company.com', '0123456792'),
('Bernard', 'Luc', 'luc.bernard@company.com', '0123456793');

INSERT INTO clients (nom, contact) VALUES
('Client ABC', 'contact@abc.com'),
('Client XYZ', 'contact@xyz.com');

INSERT INTO projets (nom, id_client) VALUES
('Projet Alpha', 1),
('Projet Beta', 1),
('Projet Gamma', 2);

INSERT INTO rdqs (titre, date_heure, adresse, mode, statut, description, id_manager, id_projet) VALUES
('RDQ Projet Alpha - Phase 1', '2024-01-15 09:00:00', '123 Rue de la Paix, Paris', 'PRESENTIEL', 'PLANIFIE', 'Première réunion de démarrage du projet Alpha', 1, 1),
('RDQ Projet Beta - Kick-off', '2024-01-20 14:00:00', NULL, 'DISTANCIEL', 'PLANIFIE', 'Lancement du projet Beta en mode distanciel', 2, 2);

INSERT INTO rdq_collaborateurs (id_rdq, id_collaborateur) VALUES
(1, 1),
(1, 2),
(2, 2),
(2, 3);