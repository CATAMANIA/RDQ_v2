package com.vibecoding.rdq.controller;

import com.vibecoding.rdq.dto.CreateRdqRequest;
import com.vibecoding.rdq.dto.RdqResponse;
import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.service.RDQService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        description = "Récupère tous les RDQ assignés au collaborateur authentifié"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des RDQ assignés récupérée"),
        @ApiResponse(responseCode = "401", description = "Non authentifié"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle COLLABORATEUR requis")
    })
    @PreAuthorize("hasRole('COLLABORATEUR')")
    public ResponseEntity<?> getMyAssignments(@AuthenticationPrincipal User currentUser) {
        
        Long collaborateurId = currentUser.getCollaborateur() != null ? 
            currentUser.getCollaborateur().getIdCollaborateur() : null;
        
        if (collaborateurId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Utilisateur non associé à un profil collaborateur"));
        }

        // TODO: Implémenter findRdqsByCollaborateurId dans le service
        List<RdqResponse> rdqs = List.of(); // Placeholder
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", rdqs,
            "count", rdqs.size()
        ));
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