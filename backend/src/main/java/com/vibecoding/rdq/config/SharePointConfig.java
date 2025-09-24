package com.vibecoding.rdq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for SharePoint integration
 */
@Configuration
@ConfigurationProperties(prefix = "sharepoint")
public class SharePointConfig {

    private String tenantId;
    private String clientId;
    private String clientSecret;
    private String siteId;
    private String driveId;
    private String baseUrl;
    private String scope = "https://graph.microsoft.com/.default";
    private String authority = "https://login.microsoftonline.com/";

    // File upload constraints
    private long maxFileSize = 52428800; // 50MB
    private String[] allowedExtensions = {"pdf", "docx", "doc", "xlsx", "xls", "pptx", "ppt", "jpg", "jpeg", "png", "gif"};
    private String[] allowedMimeTypes = {
        "application/pdf",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "application/vnd.ms-powerpoint",
        "image/jpeg",
        "image/png",
        "image/gif"
    };

    // Constructors
    public SharePointConfig() {}

    // Getters and Setters
    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String[] getAllowedExtensions() {
        return allowedExtensions;
    }

    public void setAllowedExtensions(String[] allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public String[] getAllowedMimeTypes() {
        return allowedMimeTypes;
    }

    public void setAllowedMimeTypes(String[] allowedMimeTypes) {
        this.allowedMimeTypes = allowedMimeTypes;
    }

    // Helper methods
    public String getAuthorityUrl() {
        return authority + tenantId;
    }

    public boolean isExtensionAllowed(String extension) {
        if (extension == null) return false;
        String ext = extension.toLowerCase();
        for (String allowed : allowedExtensions) {
            if (allowed.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    public boolean isMimeTypeAllowed(String mimeType) {
        if (mimeType == null) return false;
        for (String allowed : allowedMimeTypes) {
            if (allowed.equals(mimeType)) {
                return true;
            }
        }
        return false;
    }
}