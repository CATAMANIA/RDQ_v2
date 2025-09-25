package com.vibecoding.rdq.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO pour les données d'intégration externe
 */
@Schema(description = "Données d'intégration externe pour un RDQ")
public class ExternalIntegrationResponse {
    
    @Schema(description = "Type d'intégration", allowableValues = {"email", "maps", "calendar"})
    private String type;
    
    @Schema(description = "URL générée pour l'intégration")
    private String url;
    
    @Schema(description = "Indique si l'intégration est disponible")
    private boolean enabled;
    
    @Schema(description = "Description/tooltip pour l'action")
    private String tooltip;

    // Constructeurs
    public ExternalIntegrationResponse() {}

    public ExternalIntegrationResponse(String type, String url, boolean enabled, String tooltip) {
        this.type = type;
        this.url = url;
        this.enabled = enabled;
        this.tooltip = tooltip;
    }

    // Getters et setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
}