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
- **Frontend REACT(`frontend-react/`)**: React SPA transitioning to Angular LTS
- **Frontend ANGULAR(`frontend-angular/`)**: Angular LTS application
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
cd frontend-react # During React phase
cd frontend-angular  # During Angular phase
npm install
```

### JIRA Ticket Development Workflow
For each JIRA ticket implementation, follow this standardized workflow:

**⚠️ IMPORTANT**: Always use **MCP GitHub tools** for Pull Request and Issue operations instead of CLI commands.

#### 1. Branch Creation
- Create a new feature branch for each JIRA ticket: `feature/TM-XX`
- Where `XX` corresponds to the JIRA ticket number (e.g., `feature/TM-33`, `feature/TM-49`)
- Branch from `main` branch

#### 2. JIRA Ticket Assignment & Status (AUTOMATIQUE)
- **⚠️ OBLIGATOIRE**: Avant toute implémentation, exécuter automatiquement :
  1. Assigner le ticket au développeur connecté (account_id: `63fdcc6f7c30bbd6b33df040`)
  2. Passer le statut de "À faire" vers "En cours" 
  3. Ajouter commentaire de début d'implémentation
- **Objectif**: Éviter les conflits entre développeurs travaillant simultanément

#### 3. Development Process
- Implement the feature according to JIRA acceptance criteria
- Commit code incrementally with meaningful commit messages
- Push feature branch to GitHub repository

#### 4. Pull Request Creation
- **IMPORTANT**: Use MCP GitHub tools instead of CLI commands for PR operations
- Create Pull Request from feature branch to `main` using MCP tools
- Include JIRA ticket reference in PR title and description
- Link PR to corresponding JIRA ticket for traceability

#### 5. JIRA Status Management
- **🎯 DÉBUT D'IMPLÉMENTATION (OBLIGATOIRE)**:
  - Assigner automatiquement le ticket au compte connecté (`63fdcc6f7c30bbd6b33df040` - André-Pierre ABELLARD)
  - Passer immédiatement le statut à "En cours" pour éviter les conflits entre développeurs
  - Ajouter un commentaire indiquant le début des travaux
- **Acceptance Criteria Validation**: Review all acceptance criteria defined in the JIRA ticket
- **Status Update Rules**:
  - 🔄 **"En cours"** (In Progress): Travaux en cours d'implémentation (statut automatique en début)
  - ✅ **"Terminé"** (Done): All acceptance criteria are fully implemented and validated
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

# Create Pull Request using MCP GitHub tools (PREFERRED)
# Use mcp_github_create_pull_request instead of CLI
# Example: via MCP tools in VS Code
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
cd frontend-react  # During React phase
cd frontend-angular  # During Angular phase
# React phase
npm start                       # Development server
npm test                        # Run tests
npm run build                   # Production build

# Angular phase (post-migration)
ng serve                        # Development server
ng test                         # Run tests
ng build                        # Production build
```

### GitHub Integration via MCP Tools

#### **CRITICAL: Use MCP Tools for GitHub Operations**
When working with GitHub repositories, **ALWAYS use MCP GitHub tools** instead of CLI commands for the following operations:

#### Pull Request Management (via MCP)
```markdown
✅ **PREFERRED**: Use MCP GitHub tools
- mcp_github_create_pull_request
- mcp_github_update_pull_request  
- mcp_github_merge_pull_request
- mcp_github_get_pull_request
- mcp_github_list_pull_requests

❌ **AVOID**: Direct CLI commands
- gh pr create
- gh pr merge
- gh pr view
```

#### Issue Management (via MCP)
```markdown
✅ **PREFERRED**: Use MCP GitHub tools
- mcp_github_create_issue
- mcp_github_update_issue
- mcp_github_get_issue
- mcp_github_add_issue_comment

❌ **AVOID**: Direct CLI commands  
- gh issue create
- gh issue comment
```

#### Repository Operations (via MCP)
```markdown
✅ **PREFERRED**: Use MCP GitHub tools for
- Creating/updating files
- Managing branches
- Repository information
- Workflow management

⚠️ **Git CLI**: Still use for local operations
- git add, git commit, git push
- git checkout, git merge
- git status, git log
```

#### Benefits of MCP GitHub Tools
- **🔧 Integrated workflow**: Direct integration with VS Code environment
- **📋 Structured data**: Consistent API responses and error handling  
- **🎯 Context awareness**: Better integration with JIRA ticket workflow
- **📚 Documentation**: Automatic documentation and traceability
- **🔄 Reliability**: More robust than CLI for automated operations

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