package com.vibecoding.rdq.controller;

import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.entity.Manager;
import com.vibecoding.rdq.entity.Projet;
import com.vibecoding.rdq.service.RDQService;
import com.vibecoding.rdq.service.ExternalIntegrationService;
import com.vibecoding.rdq.dto.ExternalIntegrationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RdqApiController.class)
class RdqApiControllerExternalIntegrationsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RDQService rdqService;

    @MockBean
    private ExternalIntegrationService externalIntegrationService;

    @Autowired
    private ObjectMapper objectMapper;

    private RDQ mockRdq;
    private List<ExternalIntegrationResponse> mockIntegrations;

    @BeforeEach
    void setUp() {
        // Création d'un manager avec email
        Manager manager = new Manager();
        manager.setIdManager(1L);
        manager.setNom("Manager Test");
        manager.setEmail("manager@test.com");

        // Création d'un projet
        Projet projet = new Projet();
        projet.setIdProjet(1L);
        projet.setNom("Projet Test");

        // Création d'un RDQ complet
        mockRdq = new RDQ();
        mockRdq.setIdRdq(123L);
        mockRdq.setTitre("Test RDQ");
        mockRdq.setDateHeure(LocalDateTime.of(2025, 9, 30, 14, 0));
        mockRdq.setAdresse("123 Rue de Test, Paris");
        mockRdq.setMode(RDQ.ModeRDQ.PRESENTIEL);
        mockRdq.setStatut(RDQ.StatutRDQ.PLANIFIE);
        mockRdq.setDescription("Description du test");
        mockRdq.setManager(manager);
        mockRdq.setProjet(projet);

        // Création des intégrations mockées
        mockIntegrations = Arrays.asList(
                new ExternalIntegrationResponse("email", "mailto:manager@test.com?subject=Test", true, "Envoyer un email"),
                new ExternalIntegrationResponse("maps", "https://www.google.com/maps/search/?api=1&query=test", true, "Ouvrir dans Maps"),
                new ExternalIntegrationResponse("calendar", "https://calendar.google.com/calendar/render?action=TEMPLATE", true, "Ajouter au calendrier")
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    void getExternalIntegrations_WithValidRdq_ShouldReturnIntegrations() throws Exception {
        // Given
        when(rdqService.findById(123L)).thenReturn(Optional.of(mockRdq));
        when(externalIntegrationService.generateExternalIntegrations(mockRdq)).thenReturn(mockIntegrations);

        // When & Then
        mockMvc.perform(get("/api/rdq/123/external-integrations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].type").value("email"))
                .andExpect(jsonPath("$[0].url").value("mailto:manager@test.com?subject=Test"))
                .andExpect(jsonPath("$[0].enabled").value(true))
                .andExpect(jsonPath("$[0].tooltip").value("Envoyer un email"))
                .andExpect(jsonPath("$[1].type").value("maps"))
                .andExpect(jsonPath("$[1].enabled").value(true))
                .andExpect(jsonPath("$[2].type").value("calendar"))
                .andExpect(jsonPath("$[2].enabled").value(true));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getExternalIntegrations_WithNonExistentRdq_ShouldReturn404() throws Exception {
        // Given
        when(rdqService.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/rdq/999/external-integrations"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getExternalIntegrations_WithoutAuthentication_ShouldReturn401() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/rdq/123/external-integrations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getExternalIntegrations_WithDisabledIntegrations_ShouldReturnCorrectly() throws Exception {
        // Given - RDQ sans manager
        mockRdq.setManager(null);
        List<ExternalIntegrationResponse> disabledIntegrations = Arrays.asList(
                new ExternalIntegrationResponse("email", null, false, "Aucun manager assigné"),
                new ExternalIntegrationResponse("maps", "https://www.google.com/maps/search/?api=1&query=test", true, "Ouvrir dans Maps"),
                new ExternalIntegrationResponse("calendar", "https://calendar.google.com/calendar/render?action=TEMPLATE", true, "Ajouter au calendrier")
        );

        when(rdqService.findById(123L)).thenReturn(Optional.of(mockRdq));
        when(externalIntegrationService.generateExternalIntegrations(mockRdq)).thenReturn(disabledIntegrations);

        // When & Then
        mockMvc.perform(get("/api/rdq/123/external-integrations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].type").value("email"))
                .andExpect(jsonPath("$[0].url").isEmpty())
                .andExpect(jsonPath("$[0].enabled").value(false))
                .andExpect(jsonPath("$[0].tooltip").value("Aucun manager assigné"))
                .andExpect(jsonPath("$[1].enabled").value(true))
                .andExpect(jsonPath("$[2].enabled").value(true));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getExternalIntegrations_WithInvalidId_ShouldReturn400() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/rdq/invalid/external-integrations"))
                .andExpect(status().isBadRequest());
    }
}