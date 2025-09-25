package com.vibecoding.rdq.service;

import com.vibecoding.rdq.dto.ExternalIntegrationResponse;
import com.vibecoding.rdq.entity.RDQ;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de gestion des intégrations externes
 * Génère les URLs pour les intégrations avec les applications externes
 */
@Service
public class ExternalIntegrationService {

    /**
     * Génère toutes les intégrations externes disponibles pour un RDQ
     */
    public List<ExternalIntegrationResponse> generateExternalIntegrations(RDQ rdq) {
        List<ExternalIntegrationResponse> integrations = new ArrayList<>();
        
        // Intégration email
        integrations.add(generateEmailIntegration(rdq));
        
        // Intégration Google Maps
        integrations.add(generateMapsIntegration(rdq));
        
        // Intégration Google Calendar
        integrations.add(generateCalendarIntegration(rdq));
        
        return integrations;
    }

    /**
     * Génère l'intégration email
     */
    private ExternalIntegrationResponse generateEmailIntegration(RDQ rdq) {
        String managerEmail = rdq.getManager() != null ? rdq.getManager().getEmail() : null;
        
        if (managerEmail == null || managerEmail.trim().isEmpty()) {
            return new ExternalIntegrationResponse("email", null, false, "Aucun email disponible");
        }

        try {
            String subject = URLEncoder.encode(
                String.format("RDQ #%d - %s", rdq.getIdRdq(), rdq.getTitre()),
                StandardCharsets.UTF_8
            );
            
            String body = URLEncoder.encode(buildEmailBody(rdq), StandardCharsets.UTF_8);
            String url = String.format("mailto:%s?subject=%s&body=%s", managerEmail, subject, body);
            
            return new ExternalIntegrationResponse(
                "email", 
                url, 
                true, 
                String.format("Envoyer un email à %s", managerEmail)
            );
        } catch (Exception e) {
            return new ExternalIntegrationResponse("email", null, false, "Erreur lors de la génération de l'email");
        }
    }

    /**
     * Génère l'intégration Google Maps
     */
    private ExternalIntegrationResponse generateMapsIntegration(RDQ rdq) {
        if (rdq.getAdresse() == null || rdq.getAdresse().trim().isEmpty() || 
            "DISTANCIEL".equals(rdq.getMode())) {
            String tooltip = "DISTANCIEL".equals(rdq.getMode()) ? 
                "Rendez-vous distanciel - pas d'adresse" : 
                "Aucune adresse disponible";
            return new ExternalIntegrationResponse("maps", null, false, tooltip);
        }

        try {
            String address = URLEncoder.encode(rdq.getAdresse(), StandardCharsets.UTF_8);
            String url = String.format("https://www.google.com/maps/search/?api=1&query=%s", address);
            
            return new ExternalIntegrationResponse(
                "maps", 
                url, 
                true, 
                "Ouvrir l'adresse dans Google Maps"
            );
        } catch (Exception e) {
            return new ExternalIntegrationResponse("maps", null, false, "Erreur lors de la génération du lien Maps");
        }
    }

    /**
     * Génère l'intégration Google Calendar
     */
    private ExternalIntegrationResponse generateCalendarIntegration(RDQ rdq) {
        if (rdq.getDateHeure() == null) {
            return new ExternalIntegrationResponse("calendar", null, false, "Aucune date/heure disponible");
        }

        try {
            LocalDateTime startDate = rdq.getDateHeure();
            LocalDateTime endDate = startDate.plusHours(2); // +2h par défaut

            String title = URLEncoder.encode(
                String.format("RDQ - %s", rdq.getTitre()),
                StandardCharsets.UTF_8
            );
            
            String details = URLEncoder.encode(buildCalendarDetails(rdq), StandardCharsets.UTF_8);
            
            String location = URLEncoder.encode(
                rdq.getAdresse() != null && !"DISTANCIEL".equals(rdq.getMode()) 
                    ? rdq.getAdresse() 
                    : "Rendez-vous distanciel",
                StandardCharsets.UTF_8
            );

            String dates = formatDateForGoogle(startDate) + "/" + formatDateForGoogle(endDate);
            
            String url = String.format(
                "https://calendar.google.com/calendar/render?action=TEMPLATE&text=%s&dates=%s&details=%s&location=%s",
                title, dates, details, location
            );
            
            return new ExternalIntegrationResponse(
                "calendar", 
                url, 
                true, 
                "Ajouter au calendrier Google"
            );
        } catch (Exception e) {
            return new ExternalIntegrationResponse("calendar", null, false, "Erreur lors de la génération de l'événement calendrier");
        }
    }

    /**
     * Construit le corps de l'email
     */
    private String buildEmailBody(RDQ rdq) {
        StringBuilder body = new StringBuilder();
        body.append("Bonjour,\n\n");
        body.append("Je vous contacte concernant le RDQ suivant :\n\n");
        body.append(String.format("• Titre : %s\n", rdq.getTitre()));
        
        if (rdq.getDateHeure() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy 'à' HH:mm");
            body.append(String.format("• Date : %s\n", rdq.getDateHeure().format(formatter)));
        }
        
        body.append(String.format("• Mode : %s\n", rdq.getMode()));
        
        if (rdq.getAdresse() != null && !rdq.getAdresse().trim().isEmpty()) {
            body.append(String.format("• Adresse : %s\n", rdq.getAdresse()));
        }
        
        body.append("\nCordialement.");
        
        return body.toString();
    }

    /**
     * Construit les détails de l'événement calendrier
     */
    private String buildCalendarDetails(RDQ rdq) {
        StringBuilder details = new StringBuilder();
        details.append(String.format("RDQ #%d\n\n", rdq.getIdRdq()));
        
        if (rdq.getManager() != null) {
            details.append(String.format("Manager : %s\n", rdq.getManager().getNom()));
        }
        
        details.append(String.format("Mode : %s\n", rdq.getMode()));
        
        if (rdq.getDescription() != null && !rdq.getDescription().trim().isEmpty()) {
            details.append(String.format("\nDescription :\n%s", rdq.getDescription()));
        }
        
        if (rdq.getDescription() != null && !rdq.getDescription().trim().isEmpty()) {
            details.append(String.format("\n\nDescription complète :\n%s", rdq.getDescription()));
        }
        
        return details.toString();
    }

    /**
     * Formate une date pour Google Calendar (format ISO sans séparateurs)
     */
    private String formatDateForGoogle(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
    }
}