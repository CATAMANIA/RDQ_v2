# GitHub Copilot Instructions for RDQ_v2 Project

## Project Overview
This is the RDQ_v2 project located in the VibeCoding workspace. The project features a Java backend with a frontend that will migrate from React to Angular.

**Technology Stack:**
- **Frontend**: React (initial) → Angular LTS (target) - stored in `frontend/` folder
- **Backend**: Java 21 with Spring Framework

**Key External Integrations:**
- **Project Management**: JIRA project "Test MCP" with all tickets under "RDQ_v2" EPIC
- **Version Control**: GitHub repository in "Catamania" organization, main branch: "main"

## Architecture & Components

### Project Structure
```
RDQ_v2/
├── frontend/          # React app (migrating to Angular LTS)
├── backend/           # Java 21 + Spring Framework
├── docs/              # Functional specifications (SFD)
├── .prompts/          # History of actions performed
└── .github/           # GitHub workflows and documentation
```

### Key Components
- **Frontend (`frontend/`)**: React SPA transitioning to Angular LTS
- **Backend**: Spring Boot application using Java 21
- **API Layer**: RESTful services connecting frontend and backend
- **Data Layer**: *TBD - to be documented based on chosen persistence*
- **Documentation (`docs/`)**: Functional specifications (SFD)
- **Action History (`.prompts/`)**: Record of development actions and decisions

## Development Workflow

### Getting Started
```bash
# Backend setup (Java 21 + Spring)
cd backend
./mvnw clean install

# Frontend setup (React/Angular)
cd frontend
npm install
```

### JIRA Ticket Development Workflow
For each JIRA ticket implementation, follow this standardized workflow:

#### 1. Branch Creation
- Create a new feature branch for each JIRA ticket: `feature/TM-XX`
- Where `XX` corresponds to the JIRA ticket number (e.g., `feature/TM-33`, `feature/TM-49`)
- Branch from `main` branch

#### 2. Development Process
- Implement the feature according to JIRA acceptance criteria
- Commit code incrementally with meaningful commit messages
- Push feature branch to GitHub repository

#### 3. Pull Request Creation
- Create Pull Request from feature branch to `main`
- Include JIRA ticket reference in PR title and description
- Link PR to corresponding JIRA ticket for traceability

#### 4. JIRA Status Management
- **Acceptance Criteria Validation**: Review all acceptance criteria defined in the JIRA ticket
- **Status Update Rules**:
  - ✅ **"Terminé"** (Done): All acceptance criteria are fully implemented and validated
  - 🔄 **"En cours"** (In Progress): Partial implementation or acceptance criteria not yet met
- Update JIRA ticket status based on completion assessment

### Key Commands

#### Git Workflow for JIRA Tickets
```bash
# Create and switch to feature branch
git checkout -b feature/TM-XX

# Development cycle
git add .
git commit -m "feat(TM-XX): implement [feature description]"
git push origin feature/TM-XX

# Create Pull Request (via GitHub UI or CLI)
gh pr create --title "TM-XX: [Ticket Title]" --body "Implements JIRA ticket TM-XX"
```

#### Backend (Java 21 + Spring)
```bash
cd backend
./mvnw spring-boot:run          # Start development server
./mvnw test                     # Run tests
./mvnw clean package            # Build JAR
```

#### Frontend (React → Angular Migration)
```bash
cd frontend
# React phase
npm start                       # Development server
npm test                        # Run tests
npm run build                   # Production build

# Angular phase (post-migration)
ng serve                        # Development server
ng test                         # Run tests
ng build                        # Production build
```

## Code Conventions

### File Organization
- **Backend**: Follow Spring Boot conventions (controller, service, repository packages)
- **Frontend**: Component-based architecture (React → Angular component structure)
- **API**: RESTful endpoints with consistent naming patterns
- **Documentation**: Functional specifications stored in `docs/` folder
- **Development History**: Action logs and prompts stored in `.prompts/` folder

### Coding Standards
- **Java**: Follow Spring Boot best practices, use Java 21 features appropriately
- **Frontend**: 
  - React: Functional components with hooks
  - Angular: Component/service architecture with TypeScript
- **API Communication**: Consistent JSON response formats

### Testing Patterns
- **Backend**: JUnit 5 + Spring Boot Test for integration tests
- **Frontend**: 
  - React: Jest + React Testing Library
  - Angular: Jest + Angular Testing Library (custom setup)

### Migration Considerations
- Document React → Angular migration patterns as they're implemented
- Maintain API compatibility during frontend transition

## Integration Points
*Document external APIs, databases, and service dependencies*

## Debugging & Troubleshooting
*Common issues and debugging approaches specific to this project*

---

**Note**: This file should be updated as the project evolves to reflect actual patterns, conventions, and architectural decisions implemented in the codebase.