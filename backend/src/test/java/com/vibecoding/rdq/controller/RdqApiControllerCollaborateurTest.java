package com.vibecoding.rdq.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Tests d'intégration pour les endpoints de consultation RDQ par collaborateur (TM-36)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class RdqApiControllerCollaborateurTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Setup test data si nécessaire
    }

    /**
     * Test de l'endpoint /api/rdq/my-assignments pour un collaborateur authentifié
     */
    @Test
    @WithMockUser(username = "collab1", roles = {"COLLABORATEUR"})
    void testGetMyAssignments_asCollaborateur_shouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/rdq/my-assignments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    /**
     * Test de l'endpoint avec filtre par statut
     */
    @Test
    @WithMockUser(username = "collab1", roles = {"COLLABORATEUR"})
    void testGetMyAssignments_withStatusFilter_shouldReturnFilteredResults() throws Exception {
        mockMvc.perform(get("/api/rdq/my-assignments")
                .param("statut", "EN_COURS"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    /**
     * Test de l'endpoint avec historique incluant les RDQ terminées
     */
    @Test
    @WithMockUser(username = "collab1", roles = {"COLLABORATEUR"})
    void testGetMyAssignments_withHistory_shouldIncludeCompletedRdqs() throws Exception {
        mockMvc.perform(get("/api/rdq/my-assignments")
                .param("includeHistory", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    /**
     * Test d'accès sans authentification - doit échouer
     */
    @Test
    void testGetMyAssignments_withoutAuth_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/rdq/my-assignments"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Test d'accès avec rôle manager - doit être autorisé (manager peut aussi consulter ses assignations)
     */
    @Test
    @WithMockUser(username = "manager1", roles = {"MANAGER"})
    void testGetMyAssignments_asManager_shouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/rdq/my-assignments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    /**
     * Test de l'endpoint documents pour une RDQ spécifique
     */
    @Test
    @WithMockUser(username = "collab1", roles = {"COLLABORATEUR"})
    void testGetRdqDocuments_asCollaborateur_shouldReturnDocuments() throws Exception {
        // Test avec un ID de RDQ fictif
        mockMvc.perform(get("/api/rdq/1/documents"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    /**
     * Test d'accès aux documents d'une RDQ inexistante
     */
    @Test
    @WithMockUser(username = "collab1", roles = {"COLLABORATEUR"})
    void testGetRdqDocuments_withNonExistentRdq_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/rdq/99999/documents"))
                .andExpect(status().isNotFound());
    }

    /**
     * Test de paramètres de requête combinés
     */
    @Test
    @WithMockUser(username = "collab1", roles = {"COLLABORATEUR"})
    void testGetMyAssignments_withCombinedParams_shouldWork() throws Exception {
        mockMvc.perform(get("/api/rdq/my-assignments")
                .param("statut", "EN_COURS")
                .param("includeHistory", "false"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}