# Authentification et Sécurité - TM-33

## Vue d'ensemble

L'implémentation de l'authentification TM-33 "US01 - Authentification et gestion des rôles" est maintenant terminée. Le système utilise JWT (JSON Web Tokens) pour une authentification stateless avec Spring Security.

## Architecture

### Entités
- **User** : Entité principale d'authentification avec support UserDetails
- **Role** : Énumération des rôles (ADMIN, MANAGER, COLLABORATEUR)
- Relations OneToOne avec Manager et Collaborateur existants

### Services de Sécurité
- **SecurityConfig** : Configuration Spring Security avec JWT
- **JwtService** : Génération et validation des tokens JWT
- **JwtAuthenticationFilter** : Filtre d'authentification par token
- **CustomUserDetailsService** : Service de chargement utilisateur personnalisé
- **JwtAuthenticationEntryPoint** : Point d'entrée pour les erreurs d'authentification

### Services Métier
- **AuthService** : Service d'authentification et gestion utilisateur
- **UserRepository** : Repository avec requêtes personnalisées

### DTOs
- **LoginRequest** : Requête de connexion (username/email + password)
- **LoginResponse** : Réponse avec tokens JWT et profil utilisateur
- **RefreshTokenRequest** : Requête de rafraîchissement token
- **UserProfileResponse** : Profil utilisateur complet avec informations métier

## API Endpoints

### Authentification Publique

#### POST /api/auth/login
Connexion utilisateur avec génération de tokens JWT.
- **Corps** : LoginRequest (identifier, password, rememberMe)
- **Réponse** : LoginResponse (accessToken, refreshToken, user)

#### POST /api/auth/refresh
Rafraîchissement du token d'accès.
- **Corps** : RefreshTokenRequest (refreshToken)
- **Réponse** : LoginResponse avec nouveaux tokens

#### GET /api/auth/info
Informations publiques sur l'authentification (capabilities, endpoints supportés).

### Endpoints Protégés

#### GET /api/auth/profile
Profil de l'utilisateur connecté (token requis).
- **Réponse** : UserProfileResponse avec informations Manager/Collaborateur

#### GET /api/auth/profile/{userId}
Profil d'un utilisateur spécifique (admin uniquement).
- **Paramètre** : userId (Long)
- **Autorisation** : ROLE_ADMIN

#### POST /api/auth/logout
Déconnexion utilisateur (côté serveur pour logging).

#### GET /api/auth/validate
Validation du token JWT en cours.

## Configuration JWT

### Variables d'environnement
```properties
app.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
app.jwt.expiration=86400000        # 24 heures
app.jwt.refresh-expiration=604800000  # 7 jours
```

### Format des Tokens
- **Access Token** : JWT standard avec claims utilisateur
- **Refresh Token** : JWT avec claim "type": "refresh"
- **Bearer Authentication** : Authorization: Bearer <token>

## Gestion des Rôles

### Hiérarchie des Rôles
1. **ADMIN** : Accès complet système
2. **MANAGER** : Gestion RDQ et équipes
3. **COLLABORATEUR** : Accès RDQ assignés

### Contrôle d'Accès
- **Endpoints Admin** : `/api/admin/**` - ROLE_ADMIN
- **Endpoints Manager** : `/api/manager/**` - ROLE_ADMIN, ROLE_MANAGER
- **API Générale** : `/api/**` - Utilisateur authentifié

## CORS

Configuration pour développement :
- **Origins autorisés** : localhost:3000 (React), localhost:4200 (Angular)
- **Méthodes** : GET, POST, PUT, DELETE, OPTIONS, PATCH
- **Headers** : Tous autorisés
- **Credentials** : Supportés

## Sécurité

### Fonctionnalités Implémentées
- ✅ Chiffrement des mots de passe (BCrypt)
- ✅ Tokens JWT sécurisés avec expiration
- ✅ Protection CSRF désactivée (stateless)
- ✅ Validation des données d'entrée
- ✅ Gestion des comptes désactivés
- ✅ Logs de sécurité

### Données de Test

Utilisateurs de test disponibles (mot de passe: "password123") :
```sql
-- Administrateur
Username: admin, Email: admin@rdq.com, Role: ADMIN

-- Managers
Username: manager1, Email: manager1@rdq.com, Role: MANAGER
Username: manager2, Email: manager2@rdq.com, Role: MANAGER

-- Collaborateurs  
Username: collab1, Email: collab1@rdq.com, Role: COLLABORATEUR
Username: collab2, Email: collab2@rdq.com, Role: COLLABORATEUR
Username: collab3, Email: collab3@rdq.com, Role: COLLABORATEUR
```

## Tests

### Tests Unitaires
- **AuthServiceTest** : Tests du service d'authentification
- Création/gestion utilisateurs
- Validation des rôles et statuts
- Gestion des erreurs

### Documentation API
- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **OpenAPI Docs** : http://localhost:8080/v3/api-docs

## Intégration Frontend

### Workflow d'Authentification
1. **Connexion** : POST /api/auth/login
2. **Stockage token** : LocalStorage/SessionStorage
3. **Requêtes** : Header Authorization: Bearer <token>
4. **Rafraîchissement** : POST /api/auth/refresh (automatique)
5. **Déconnexion** : Suppression côté client + POST /api/auth/logout

### Gestion des Erreurs
- **401** : Token manquant/invalide → Redirection login
- **403** : Compte désactivé → Message utilisateur
- **Token expiré** : Tentative de refresh automatique

## Conformité TM-33

### Critères d'Acceptation Implementés ✅
1. **Système d'authentification JWT** : ✅ Implémenté
2. **Gestion des rôles utilisateur** : ✅ ADMIN/MANAGER/COLLABORATEUR
3. **Interface de connexion sécurisée** : ✅ Endpoints API prêts
4. **Sessions utilisateur persistantes** : ✅ Tokens avec refresh
5. **Contrôle d'accès par rôle** : ✅ Spring Security + annotations
6. **Gestion des erreurs d'authentification** : ✅ Messages explicites
7. **Sécurisation des endpoints** : ✅ Configuration complète
8. **Documentation API** : ✅ OpenAPI/Swagger

La story TM-33 est maintenant **TERMINÉE** et prête pour les tests d'intégration avec le frontend.