# RDQ_v2 Project

Application avec frontend React/Angular et backend Java 21 + Spring Framework.

## Structure du Projet

- **frontend/**: Application frontend (React → Angular LTS)
- **backend/**: Application backend (Java 21 + Spring Framework)
- **docs/**: Spécifications fonctionnelles détaillées (SFD)
- **.prompts/**: Historique des actions et décisions de développement
- **.github/**: Workflows GitHub et documentation

## Technologies

### Frontend
- React (phase initiale)
- Angular LTS (cible de migration)
- Jest pour les tests

### Backend
- Java 21
- Spring Framework
- JUnit 5 pour les tests

## Intégrations Externes
- **Gestion de projet**: JIRA project "Test MCP" sous l'EPIC "RDQ_v2"
- **Contrôle de version**: Repository GitHub dans l'organisation "Catamania"

## Démarrage Rapide

### Backend
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm start
```

## Documentation

Consultez le dossier `docs/` pour les spécifications fonctionnelles et `.github/copilot-instructions.md` pour les conventions de développement.