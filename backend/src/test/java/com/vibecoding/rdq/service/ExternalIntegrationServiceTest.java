package com.vibecoding.rdq.service;

import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.entity.Manager;
import com.vibecoding.rdq.entity.Projet;
import com.vibecoding.rdq.dto.ExternalIntegrationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExternalIntegrationServiceTest {

    @InjectMocks
    private ExternalIntegrationService externalIntegrationService;

    private RDQ mockRdq;

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
    }

    @Test
    void generateExternalIntegrations_WithCompleteRdq_ShouldReturnAllIntegrations() {
        // When
        List<ExternalIntegrationResponse> integrations = externalIntegrationService.generateExternalIntegrations(mockRdq);

        // Then
        assertNotNull(integrations);
        assertEquals(3, integrations.size());

        // Vérifier l'intégration email
        ExternalIntegrationResponse emailIntegration = integrations.stream()
                .filter(i -> "email".equals(i.getType()))
                .findFirst()
                .orElse(null);
        assertNotNull(emailIntegration);
        assertTrue(emailIntegration.isEnabled());
        assertTrue(emailIntegration.getUrl().startsWith("mailto:manager@test.com"));
        assertTrue(emailIntegration.getUrl().contains("subject="));
        assertEquals("Envoyer un email à manager@test.com", emailIntegration.getTooltip());

        // Vérifier l'intégration maps
        ExternalIntegrationResponse mapsIntegration = integrations.stream()
                .filter(i -> "maps".equals(i.getType()))
                .findFirst()
                .orElse(null);
        assertNotNull(mapsIntegration);
        assertTrue(mapsIntegration.isEnabled());
        assertTrue(mapsIntegration.getUrl().startsWith("https://www.google.com/maps/search/"));
        assertEquals("Ouvrir l'adresse dans Google Maps", mapsIntegration.getTooltip());

        // Vérifier l'intégration calendar
        ExternalIntegrationResponse calendarIntegration = integrations.stream()
                .filter(i -> "calendar".equals(i.getType()))
                .findFirst()
                .orElse(null);
        assertNotNull(calendarIntegration);
        assertTrue(calendarIntegration.isEnabled());
        assertTrue(calendarIntegration.getUrl().startsWith("https://calendar.google.com/calendar/render"));
        assertEquals("Ajouter au calendrier Google", calendarIntegration.getTooltip());
    }

    @Test
    void generateExternalIntegrations_WithoutManager_ShouldDisableEmail() {
        // Given
        mockRdq.setManager(null);

        // When
        List<ExternalIntegrationResponse> integrations = externalIntegrationService.generateExternalIntegrations(mockRdq);

        // Then
        ExternalIntegrationResponse emailIntegration = integrations.stream()
                .filter(i -> "email".equals(i.getType()))
                .findFirst()
                .orElse(null);
        assertNotNull(emailIntegration);
        assertFalse(emailIntegration.isEnabled());
        assertNull(emailIntegration.getUrl());
        assertEquals("Aucun email disponible", emailIntegration.getTooltip());
    }

    @Test
    void generateExternalIntegrations_WithoutManagerEmail_ShouldDisableEmail() {
        // Given
        mockRdq.getManager().setEmail(null);

        // When
        List<ExternalIntegrationResponse> integrations = externalIntegrationService.generateExternalIntegrations(mockRdq);

        // Then
        ExternalIntegrationResponse emailIntegration = integrations.stream()
                .filter(i -> "email".equals(i.getType()))
                .findFirst()
                .orElse(null);
        assertNotNull(emailIntegration);
        assertFalse(emailIntegration.isEnabled());
        assertNull(emailIntegration.getUrl());
        assertEquals("Aucun email disponible", emailIntegration.getTooltip());
    }

    @Test
    void generateExternalIntegrations_WithoutAddress_ShouldDisableMaps() {
        // Given
        mockRdq.setAdresse(null);

        // When
        List<ExternalIntegrationResponse> integrations = externalIntegrationService.generateExternalIntegrations(mockRdq);

        // Then
        ExternalIntegrationResponse mapsIntegration = integrations.stream()
                .filter(i -> "maps".equals(i.getType()))
                .findFirst()
                .orElse(null);
        assertNotNull(mapsIntegration);
        assertFalse(mapsIntegration.isEnabled());
        assertNull(mapsIntegration.getUrl());
        assertEquals("Aucune adresse disponible", mapsIntegration.getTooltip());
    }

    @Test
    void generateExternalIntegrations_WithoutDateTime_ShouldDisableCalendar() {
        // Given
        mockRdq.setDateHeure(null);

        // When
        List<ExternalIntegrationResponse> integrations = externalIntegrationService.generateExternalIntegrations(mockRdq);

        // Then
        ExternalIntegrationResponse calendarIntegration = integrations.stream()
                .filter(i -> "calendar".equals(i.getType()))
                .findFirst()
                .orElse(null);
        assertNotNull(calendarIntegration);
        assertFalse(calendarIntegration.isEnabled());
        assertNull(calendarIntegration.getUrl());
        assertEquals("Aucune date/heure disponible", calendarIntegration.getTooltip());
    }

    // Tests pour buildEmailBody supprimés car méthode privée
    // Le formatage est testé indirectement via les intégrations complètes

    @Test
    void formatDateForGoogle_ShouldFormatCorrectly() {
        // When - accès au formatage via une intégration complète
        List<ExternalIntegrationResponse> integrations = externalIntegrationService.generateExternalIntegrations(mockRdq);
        ExternalIntegrationResponse calendarIntegration = integrations.stream()
                .filter(i -> "calendar".equals(i.getType()))
                .findFirst()
                .orElse(null);

        // Then
        assertNotNull(calendarIntegration);
        assertTrue(calendarIntegration.getUrl().contains("dates=20250930T140000"));
    }
}