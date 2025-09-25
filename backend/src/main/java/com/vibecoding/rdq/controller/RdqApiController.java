package com.vibecoding.rdq.controller;

import com.vibecoding.rdq.dto.CreateRdqRequest;
import com.vibecoding.rdq.dto.ExternalIntegrationResponse;
import com.vibecoding.rdq.dto.RdqResponse;
import com.vibecoding.rdq.dto.UpdateRdqRequest;
import com.vibecoding.rdq.dto.RdqSearchCriteria;
import com.vibecoding.rdq.dto.RdqSearchResponse;
import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.service.RDQService;
import com.vibecoding.rdq.service.ExternalIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des RDQ - API v1
 * Endpoints sécurisés avec contrôle d'accès par rôles
 */
@RestController
@RequestMapping("/api/v1/rdq")
@Tag(name = "RDQ Management", description = "API de gestion des Rendez-vous Qualifiés (RDQ)")
@SecurityRequirement(name = "bearerAuth")
public class RdqApiController {

    @Autowired
    private RDQService rdqService;

    @Autowired
    private ExternalIntegrationService externalIntegrationService;

    /**
     * Créer un nouveau RDQ (MANAGER uniquement)
     */
    @PostMapping
    @Operation(
        summary = "Créer un nouveau RDQ",
        description = "Permet à un manager de créer un nouveau RDQ avec collaborateurs assignés"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "RDQ créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle MANAGER requis"),
        @ApiResponse(responseCode = "404", description = "Projet ou collaborateur non trouvé"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> createRdq(
            @Valid @RequestBody CreateRdqRequest request,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            // Récupération de l'ID du manager depuis l'utilisateur authentifié
            Long managerId = currentUser.getManager() != null ? 
                currentUser.getManager().getIdManager() : null;
            
            if (managerId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Utilisateur non associé à un profil manager"));
            }

            RdqResponse rdqResponse = rdqService.createRdq(request, managerId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "success", true,
                    "message", "RDQ créé avec succès",
                    "data", rdqResponse
                ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false,
                    "error", e.getMessage()
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "error", "Erreur interne du serveur"
                ));
        }
    }

    /**
     * Récupérer un RDQ par ID
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer un RDQ par ID",
        description = "Récupère les détails complets d'un RDQ"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "RDQ trouvé"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "404", description = "RDQ non trouvé")
    })
    @PreAuthorize("hasRole('MANAGER') or hasRole('COLLABORATEUR')")
    public ResponseEntity<?> getRdqById(
            @Parameter(description = "ID du RDQ") @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        Optional<RdqResponse> rdqResponse = rdqService.findRdqById(id);
        
        if (rdqResponse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "success", false,
                    "error", "RDQ non trouvé avec l'ID: " + id
                ));
        }

        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", rdqResponse.get()
        ));
    }

    /**
     * Récupérer les RDQ du manager connecté
     */
    @GetMapping("/my-rdqs")
    @Operation(
        summary = "Récupérer les RDQ du manager connecté",
        description = "Récupère tous les RDQ créés par le manager authentifié"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des RDQ récupérée"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle MANAGER requis")
    })
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> getMyRdqs(@AuthenticationPrincipal User currentUser) {
        
        Long managerId = currentUser.getManager() != null ? 
            currentUser.getManager().getIdManager() : null;
        
        if (managerId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Utilisateur non associé à un profil manager"));
        }

        List<RdqResponse> rdqs = rdqService.findRdqsByManagerId(managerId);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", rdqs,
            "count", rdqs.size()
        ));
    }

    /**
     * Récupérer les RDQ assignés au collaborateur connecté
     */
    @GetMapping("/my-assignments")
    @Operation(
        summary = "Récupérer les RDQ assignés au collaborateur",
        description = "Récupère tous les RDQ assignés au collaborateur authentifié avec options de filtrage"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des RDQ assignés récupérée"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle COLLABORATEUR requis")
    })
    @PreAuthorize("hasRole('COLLABORATEUR')")
    public ResponseEntity<?> getMyAssignments(
            @Parameter(description = "Inclure l'historique (RDQ terminés/annulés)") 
            @RequestParam(required = false, defaultValue = "false") boolean includeHistory,
            @Parameter(description = "Filtrer par statut") 
            @RequestParam(required = false) String statut,
            @AuthenticationPrincipal User currentUser) {
        
        Long collaborateurId = currentUser.getCollaborateur() != null ? 
            currentUser.getCollaborateur().getIdCollaborateur() : null;
        
        if (collaborateurId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Utilisateur non associé à un profil collaborateur"));
        }

        try {
            List<RdqResponse> rdqs;
            
            if (statut != null && !statut.isEmpty()) {
                // Filtrage par statut spécifique
                RDQ.StatutRDQ statutEnum = RDQ.StatutRDQ.valueOf(statut.toUpperCase());
                rdqs = rdqService.findAssignmentsByCollaborateurIdAndStatut(collaborateurId, statutEnum);
            } else {
                // Filtrage avec ou sans historique
                rdqs = rdqService.findAssignmentsByCollaborateurId(collaborateurId, includeHistory);
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", rdqs,
                "count", rdqs.size(),
                "filters", Map.of(
                    "includeHistory", includeHistory,
                    "statut", statut != null ? statut : "all"
                )
            ));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false,
                    "error", "Statut invalide. Valeurs autorisées: PLANIFIE, EN_COURS, TERMINE, ANNULE, CLOS"
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "error", "Erreur lors de la récupération des RDQ"
                ));
        }
    }

    /**
     * Récupérer les documents d'un RDQ
     */
    @GetMapping("/{id}/documents")
    @Operation(
        summary = "Récupérer les documents d'un RDQ",
        description = "Récupère la liste des documents attachés à un RDQ"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des documents récupérée"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "404", description = "RDQ non trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    @PreAuthorize("hasRole('MANAGER') or hasRole('COLLABORATEUR')")
    public ResponseEntity<?> getRdqDocuments(
            @Parameter(description = "ID du RDQ") @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            Optional<RdqResponse> rdqResponse = rdqService.findRdqById(id);
            
            if (rdqResponse.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "success", false,
                        "error", "RDQ non trouvé avec l'ID: " + id
                    ));
            }

            // TODO: Vérifier les droits d'accès (collaborateur assigné ou manager propriétaire)
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", rdqResponse.get().getDocuments() != null ? rdqResponse.get().getDocuments() : List.of(),
                "count", rdqResponse.get().getDocuments() != null ? rdqResponse.get().getDocuments().size() : 0
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "error", "Erreur lors de la récupération des documents"
                ));
        }
    }

    /**
     * Récupérer les intégrations externes pour un RDQ
     */
    @GetMapping("/{id}/external-integrations")
    @Operation(
        summary = "Récupérer les intégrations externes",
        description = "Génère les URLs d'intégration avec les applications externes (email, maps, calendrier)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Intégrations générées avec succès"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "404", description = "RDQ non trouvé")
    })
    @PreAuthorize("hasRole('MANAGER') or hasRole('COLLABORATEUR')")
    public ResponseEntity<?> getExternalIntegrations(
            @Parameter(description = "ID du RDQ") @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            // Récupérer le RDQ pour les intégrations
            Optional<RDQ> rdqOptional = rdqService.findRdqEntityById(id);
            
            if (rdqOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "success", false,
                        "error", "RDQ non trouvé avec l'ID: " + id
                    ));
            }

            RDQ rdq = rdqOptional.get();
            List<ExternalIntegrationResponse> integrations = 
                externalIntegrationService.generateExternalIntegrations(rdq);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", integrations,
                "rdqId", id
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "error", "Erreur lors de la génération des intégrations externes"
                ));
        }
    }

    /**
     * Endpoint de test pour vérifier l'accès API
     */
    @GetMapping("/health")
    @Operation(
        summary = "Health check de l'API RDQ",
        description = "Endpoint pour vérifier que l'API RDQ fonctionne correctement"
    )
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "RDQ API",
            "version", "1.0",
            "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Modifier un RDQ (MANAGER propriétaire uniquement)
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Modifier un RDQ existant",
        description = "Permet au manager propriétaire de modifier un RDQ tant qu'il n'est pas clos"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "RDQ modifié avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Seul le manager propriétaire peut modifier"),
        @ApiResponse(responseCode = "404", description = "RDQ non trouvé"),
        @ApiResponse(responseCode = "409", description = "RDQ clos - Modification impossible"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<?> modifierRdq(
            @Parameter(description = "ID du RDQ à modifier") @PathVariable Long id,
            @Valid @RequestBody UpdateRdqRequest updateRequest,
            @AuthenticationPrincipal User currentUser) {
        
        try {
            // Récupération de l'ID du manager depuis l'utilisateur authentifié
            Long managerId = currentUser.getManager() != null ? 
                currentUser.getManager().getIdManager() : null;
            
            if (managerId == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Utilisateur non associé à un profil manager"));
            }

            RdqResponse rdqResponse = rdqService.modifierRdq(id, updateRequest, managerId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "RDQ modifié avec succès",
                "data", rdqResponse
            ));

        } catch (RuntimeException e) {
            // Gestion spécifique des différents types d'erreurs
            String errorMessage = e.getMessage();
            HttpStatus status = HttpStatus.BAD_REQUEST;

            if (errorMessage.contains("non trouvé")) {
                status = HttpStatus.NOT_FOUND;
            } else if (errorMessage.contains("clos") || errorMessage.contains("annulé")) {
                status = HttpStatus.CONFLICT;
            } else if (errorMessage.contains("propriétaire") || errorMessage.contains("droits")) {
                status = HttpStatus.FORBIDDEN;
            }

            return ResponseEntity.status(status)
                .body(Map.of(
                    "success", false,
                    "error", errorMessage
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "success", false,
                    "error", "Erreur interne du serveur"
                ));
        }
    }

    /**
     * Clôturer un RDQ (MANAGER uniquement)
     */
    @PutMapping("/{id}/cloturer")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
        summary = "Clôturer un RDQ",
        description = "Permet à un manager de clôturer un RDQ si les deux bilans sont présents"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "RDQ clôturé avec succès"),
        @ApiResponse(responseCode = "400", description = "Prérequis non remplis pour la clôture"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Manager requis"),
        @ApiResponse(responseCode = "404", description = "RDQ non trouvé")
    })
    public ResponseEntity<Map<String, Object>> cloturerRdq(
            @Parameter(description = "ID du RDQ à clôturer")
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        try {
            RDQ rdqClos = rdqService.cloturerRdq(id);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "RDQ clôturé avec succès",
                "rdq", Map.of(
                    "id", rdqClos.getIdRdq(),
                    "titre", rdqClos.getTitre(),
                    "statut", rdqClos.getStatut().toString()
                )
            ));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "success", false,
                    "error", "RDQ non trouvé"
                ));
                
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false,
                    "error", e.getMessage()
                ));
        }
    }

    /**
     * Rouvrir un RDQ clos (MANAGER uniquement)
     */
    @PutMapping("/{id}/rouvrir")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
        summary = "Rouvrir un RDQ clos",
        description = "Permet à un manager de rouvrir un RDQ précédemment clos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "RDQ rouvert avec succès"),
        @ApiResponse(responseCode = "400", description = "Le RDQ n'est pas clos"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Manager requis"),
        @ApiResponse(responseCode = "404", description = "RDQ non trouvé")
    })
    public ResponseEntity<Map<String, Object>> rouvrirRdq(
            @Parameter(description = "ID du RDQ à rouvrir")
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        try {
            RDQ rdqRouvert = rdqService.rouvrirRdq(id);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "RDQ rouvert avec succès",
                "rdq", Map.of(
                    "id", rdqRouvert.getIdRdq(),
                    "titre", rdqRouvert.getTitre(),
                    "statut", rdqRouvert.getStatut().toString()
                )
            ));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "success", false,
                    "error", "RDQ non trouvé"
                ));
                
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false,
                    "error", e.getMessage()
                ));
        }
    }

    /**
     * Vérifier si un RDQ peut être clôturé
     */
    @GetMapping("/{id}/peut-cloturer")
    @Operation(
        summary = "Vérifier si un RDQ peut être clôturé",
        description = "Vérifie les prérequis pour la clôture d'un RDQ"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vérification effectuée"),
        @ApiResponse(responseCode = "404", description = "RDQ non trouvé")
    })
    public ResponseEntity<Map<String, Object>> peutCloturerRdq(
            @Parameter(description = "ID du RDQ à vérifier")
            @PathVariable Long id) {
        
        if (!rdqService.findById(id).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "success", false,
                    "error", "RDQ non trouvé"
                ));
        }
        
        boolean peutCloture = rdqService.peutEtreCloture(id);
        boolean peutRouvrir = rdqService.peutEtreRouvert(id);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "peutCloture", peutCloture,
            "peutRouvrir", peutRouvrir
        ));
    }

    // ========== Nouveaux endpoints pour TM-41 - Recherche et filtrage avancé ==========

    /**
     * Recherche avancée de RDQ avec filtrage et pagination
     * Support de multiples critères de filtrage pour TM-41 - US09
     */
    @GetMapping("/search")
    @Operation(
        summary = "Recherche avancée de RDQ",
        description = "Recherche paginée avec filtrage par client, collaborateur, statut, dates, etc."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recherche effectuée avec succès"),
        @ApiResponse(responseCode = "400", description = "Paramètres de recherche invalides"),
        @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PreAuthorize("hasRole('MANAGER') or hasRole('COLLABORATEUR')")
    public ResponseEntity<RdqSearchResponse> searchRdqs(
            @Parameter(description = "Nom du client") @RequestParam(required = false) String clientNom,
            @Parameter(description = "ID du client") @RequestParam(required = false) Long clientId,
            @Parameter(description = "Nom du collaborateur") @RequestParam(required = false) String collaborateurNom,
            @Parameter(description = "ID du collaborateur") @RequestParam(required = false) Long collaborateurId,
            @Parameter(description = "ID du manager") @RequestParam(required = false) Long managerId,
            @Parameter(description = "Nom du projet") @RequestParam(required = false) String projetNom,
            @Parameter(description = "ID du projet") @RequestParam(required = false) Long projetId,
            @Parameter(description = "Statuts (PLANIFIE,EN_COURS,TERMINE,ANNULE,CLOS)") @RequestParam(required = false) String statuts,
            @Parameter(description = "Modes (PRESENTIEL,DISTANCIEL,HYBRIDE)") @RequestParam(required = false) String modes,
            @Parameter(description = "Date de début (yyyy-MM-dd'T'HH:mm:ss)") @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @Parameter(description = "Date de fin (yyyy-MM-dd'T'HH:mm:ss)") @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            @Parameter(description = "Terme de recherche textuelle") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Numéro de page (0-indexé)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Taille de page (1-100)") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "dateHeure") String sortBy,
            @Parameter(description = "Direction de tri (ASC/DESC)") @RequestParam(defaultValue = "DESC") String sortDirection,
            @Parameter(description = "Inclure l'historique (RDQ terminés/annulés)") @RequestParam(defaultValue = "false") Boolean includeHistory,
            @Parameter(description = "Inclure les documents") @RequestParam(defaultValue = "false") Boolean includeDocuments,
            @Parameter(description = "Inclure les bilans") @RequestParam(defaultValue = "false") Boolean includeBilans,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            // Construction des critères de recherche
            RdqSearchCriteria criteriaBuilder = RdqSearchCriteria.builder()
                .clientNom(clientNom)
                .clientId(clientId)
                .collaborateurNom(collaborateurNom)
                .collaborateurId(collaborateurId)
                .managerId(managerId)
                .projetNom(projetNom)
                .projetId(projetId)
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .searchTerm(searchTerm)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .includeHistory(includeHistory)
                .includeDocuments(includeDocuments)
                .includeBilans(includeBilans);

            // Parsing des statuts
            if (statuts != null && !statuts.trim().isEmpty()) {
                criteriaBuilder.statuts(
                    Arrays.stream(statuts.split(","))
                        .map(String::trim)
                        .map(RDQ.StatutRDQ::valueOf)
                        .toList()
                );
            }

            // Parsing des modes
            if (modes != null && !modes.trim().isEmpty()) {
                criteriaBuilder.modes(
                    Arrays.asList(modes.split(","))
                );
            }

            // Filtrage par rôle utilisateur
            if ("MANAGER".equals(currentUser.getRole().name())) {
                if (managerId == null) {
                    criteriaBuilder.managerId(currentUser.getIdUser());
                    criteriaBuilder.myRdqsOnly(true);
                }
            } else if ("COLLABORATEUR".equals(currentUser.getRole().name())) {
                if (collaborateurId == null) {
                    criteriaBuilder.collaborateurId(currentUser.getIdUser());
                    criteriaBuilder.myAssignmentsOnly(true);
                }
            }

            RdqSearchCriteria criteria = criteriaBuilder.build();

            // Exécution de la recherche
            RdqSearchResponse response = rdqService.searchRdqs(criteria);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            RdqSearchResponse errorResponse = RdqSearchResponse.builder()
                .rdqs(List.of())
                .currentPage(0)
                .totalPages(0)
                .pageSize(size)
                .totalElements(0L)
                .appliedFiltersDescription("Erreur: " + e.getMessage())
                .build();
            
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            RdqSearchResponse errorResponse = RdqSearchResponse.builder()
                .rdqs(List.of())
                .currentPage(0)
                .totalPages(0)
                .pageSize(size)
                .totalElements(0L)
                .appliedFiltersDescription("Erreur serveur: " + e.getMessage())
                .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Export des RDQ selon critères (CSV/Excel futur)
     */
    @GetMapping("/export")
    @Operation(
        summary = "Export des RDQ",
        description = "Export des RDQ selon critères de filtrage (limite 1000 résultats)"
    )
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<RdqResponse>> exportRdqs(
            @Parameter(description = "Critères identiques à /search") @RequestParam(required = false) String clientNom,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String collaborateurNom,
            @RequestParam(required = false) Long collaborateurId,
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) String projetNom,
            @RequestParam(required = false) Long projetId,
            @RequestParam(required = false) String statuts,
            @RequestParam(required = false) String modes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "dateHeure") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "true") Boolean includeHistory,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            // Construction des critères similaires à la recherche
            RdqSearchCriteria criteriaBuilder = RdqSearchCriteria.builder()
                .clientNom(clientNom)
                .clientId(clientId)
                .collaborateurNom(collaborateurNom)
                .collaborateurId(collaborateurId)
                .managerId(managerId != null ? managerId : currentUser.getIdUser())
                .projetNom(projetNom)
                .projetId(projetId)
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .searchTerm(searchTerm)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .includeHistory(includeHistory)
                .myRdqsOnly(true); // Managers ne peuvent exporter que leurs RDQ

            // Parsing des statuts et modes
            if (statuts != null && !statuts.trim().isEmpty()) {
                criteriaBuilder.statuts(
                    Arrays.stream(statuts.split(","))
                        .map(String::trim)
                        .map(RDQ.StatutRDQ::valueOf)
                        .toList()
                );
            }

            if (modes != null && !modes.trim().isEmpty()) {
                criteriaBuilder.modes(Arrays.asList(modes.split(",")));
            }

            RdqSearchCriteria criteria = criteriaBuilder.build();

            // Export via service
            List<RdqResponse> rdqs = rdqService.exportRdqs(criteria);

            return ResponseEntity.ok(rdqs);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(List.of());
        }
    }

    /**
     * Gestion globale des erreurs de validation
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new java.util.HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "success", false,
                "error", "Erreurs de validation",
                "details", errors
            ));
    }
}