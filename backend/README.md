# RDQ Backend - Initialisation Stack Technique

## Overview
Ce backend Java 21 + Spring Boot 3.4 LTS gère le système RDQ (Réunions de Démarrage) avec une architecture REST complète.

## Stack Technique
- **Java**: 21 (LTS) ✅ *Mis à jour (TM-50)*
- **Framework**: Spring Boot 3.4.0 (LTS) ✅ *Mis à jour (TM-50)*
- **Base de données**: PostgreSQL 15+ (H2 pour tests)
- **ORM**: Hibernate/JPA (via Spring Boot 3.4)
- **Build**: Maven 3.9+
- **Migration DB**: Flyway 10.20+
- **Documentation API**: Swagger/OpenAPI 3 (SpringDoc 2.7.0)
- **Tests**: JUnit 5 + Mockito + TestContainers 1.20+

## Architecture

### Modèle de données
- **RDQ**: Entité principale (titre, date/heure, adresse, mode, statut)
- **Manager**: Responsable des RDQ
- **Collaborateur**: Participants aux RDQ
- **Client**: Société cliente
- **Projet**: Projet associé à une RDQ
- **Document**: Fichiers liés aux RDQ
- **Bilan**: Évaluations post-RDQ (note 1-5)

### Structure des packages
```
com.vibecoding.rdq/
├── entity/          # Entités JPA
├── repository/      # Repositories Spring Data
├── service/         # Services métier
├── controller/      # Controllers REST
└── config/          # Configuration
```

## Installation & Démarrage

### Prérequis
- Java 21
- PostgreSQL 15+
- Maven 3.9+

### Configuration base de données
1. Créer une base PostgreSQL `rdq_db`
2. Utilisateur : `rdq_user` / `rdq_password`
3. Les migrations Flyway s'exécutent automatiquement

### Commandes
```bash
# Build
./mvnw clean install

# Démarrage
./mvnw spring-boot:run

# Tests
./mvnw test
```

## API Documentation
- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v1/api-docs

## Endpoints principaux

### RDQ
- `GET /api/v1/rdq` - Liste toutes les RDQ
- `POST /api/v1/rdq` - Créer une RDQ
- `GET /api/v1/rdq/{id}` - Détails d'une RDQ
- `PUT /api/v1/rdq/{id}` - Modifier une RDQ
- `DELETE /api/v1/rdq/{id}` - Supprimer une RDQ

### Managers
- `GET /api/v1/managers` - Liste tous les managers
- `POST /api/v1/managers` - Créer un manager
- `GET /api/v1/managers/{id}` - Détails d'un manager

## Environnements
- **dev**: PostgreSQL local
- **test**: H2 en mémoire
- **prod**: PostgreSQL production

## Statut implémentation
✅ **Fondations complètes** :
- Structure Maven avec dépendances
- 7 entités JPA avec relations
- Repositories Spring Data JPA
- Services métier de base
- Controllers REST principaux
- Configuration Swagger/OpenAPI
- Migrations Flyway + données test
- Tests unitaires de base

## Prochaines étapes
- Authentification JWT (Spring Security)
- DTOs et MapStruct
- Validation avancée
- Tests d'intégration
- Configuration Docker