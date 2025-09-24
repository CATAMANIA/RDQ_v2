-- Migration V1: Création des tables principales
CREATE TABLE managers (
    id_manager BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telephone VARCHAR(20)
);

CREATE TABLE collaborateurs (
    id_collaborateur BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telephone VARCHAR(20)
);

CREATE TABLE clients (
    id_client BIGSERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    contact VARCHAR(100)
);

CREATE TABLE projets (
    id_projet BIGSERIAL PRIMARY KEY,
    nom VARCHAR(200) NOT NULL,
    id_client BIGINT NOT NULL,
    FOREIGN KEY (id_client) REFERENCES clients(id_client)
);

CREATE TABLE rdqs (
    id_rdq BIGSERIAL PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    date_heure TIMESTAMP NOT NULL,
    adresse VARCHAR(500),
    mode VARCHAR(20) NOT NULL DEFAULT 'PRESENTIEL',
    statut VARCHAR(20) NOT NULL DEFAULT 'PLANIFIE',
    description TEXT,
    id_manager BIGINT NOT NULL,
    id_projet BIGINT NOT NULL,
    FOREIGN KEY (id_manager) REFERENCES managers(id_manager),
    FOREIGN KEY (id_projet) REFERENCES projets(id_projet)
);

CREATE TABLE rdq_collaborateurs (
    id_rdq BIGINT NOT NULL,
    id_collaborateur BIGINT NOT NULL,
    PRIMARY KEY (id_rdq, id_collaborateur),
    FOREIGN KEY (id_rdq) REFERENCES rdqs(id_rdq) ON DELETE CASCADE,
    FOREIGN KEY (id_collaborateur) REFERENCES collaborateurs(id_collaborateur) ON DELETE CASCADE
);

CREATE TABLE documents (
    id_document BIGSERIAL PRIMARY KEY,
    nom_fichier VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    url VARCHAR(500) NOT NULL,
    taille_fichier BIGINT,
    id_rdq BIGINT NOT NULL,
    FOREIGN KEY (id_rdq) REFERENCES rdqs(id_rdq) ON DELETE CASCADE
);

CREATE TABLE bilans (
    id_bilan BIGSERIAL PRIMARY KEY,
    note INTEGER NOT NULL CHECK (note >= 1 AND note <= 5),
    commentaire TEXT,
    auteur VARCHAR(100) NOT NULL,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_rdq BIGINT NOT NULL,
    FOREIGN KEY (id_rdq) REFERENCES rdqs(id_rdq) ON DELETE CASCADE
);

-- Index pour améliorer les performances
CREATE INDEX idx_rdqs_manager ON rdqs(id_manager);
CREATE INDEX idx_rdqs_projet ON rdqs(id_projet);
CREATE INDEX idx_rdqs_date_heure ON rdqs(date_heure);
CREATE INDEX idx_rdqs_statut ON rdqs(statut);
CREATE INDEX idx_documents_rdq ON documents(id_rdq);
CREATE INDEX idx_bilans_rdq ON bilans(id_rdq);
CREATE INDEX idx_projets_client ON projets(id_client);