package com.vibecoding.rdq.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO for document information response
 */
@Schema(description = "Document information with SharePoint details")
public class DocumentResponse {

    @Schema(description = "Document ID", example = "123")
    private Long id;

    @Schema(description = "Original file name", example = "CV_Jean_Dupont.pdf")
    private String nomFichier;

    @Schema(description = "Document type", example = "PDF")
    private String type;

    @Schema(description = "File size in bytes", example = "1048576")
    private Long tailleFichier;

    @Schema(description = "MIME type", example = "application/pdf")
    private String mimeType;

    @Schema(description = "SharePoint item ID")
    private String sharepointItemId;

    @Schema(description = "SharePoint download URL")
    private String sharepointDownloadUrl;

    @Schema(description = "Upload date and time")
    private LocalDateTime dateUpload;

    @Schema(description = "User who uploaded the file", example = "manager@company.com")
    private String uploadedBy;

    @Schema(description = "RDQ ID this document belongs to", example = "456")
    private Long rdqId;

    // Constructors
    public DocumentResponse() {}

    public DocumentResponse(Long id, String nomFichier, String type, Long tailleFichier, String mimeType,
                           String sharepointItemId, String sharepointDownloadUrl, LocalDateTime dateUpload,
                           String uploadedBy, Long rdqId) {
        this.id = id;
        this.nomFichier = nomFichier;
        this.type = type;
        this.tailleFichier = tailleFichier;
        this.mimeType = mimeType;
        this.sharepointItemId = sharepointItemId;
        this.sharepointDownloadUrl = sharepointDownloadUrl;
        this.dateUpload = dateUpload;
        this.uploadedBy = uploadedBy;
        this.rdqId = rdqId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTailleFichier() {
        return tailleFichier;
    }

    public void setTailleFichier(Long tailleFichier) {
        this.tailleFichier = tailleFichier;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSharepointItemId() {
        return sharepointItemId;
    }

    public void setSharepointItemId(String sharepointItemId) {
        this.sharepointItemId = sharepointItemId;
    }

    public String getSharepointDownloadUrl() {
        return sharepointDownloadUrl;
    }

    public void setSharepointDownloadUrl(String sharepointDownloadUrl) {
        this.sharepointDownloadUrl = sharepointDownloadUrl;
    }

    public LocalDateTime getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(LocalDateTime dateUpload) {
        this.dateUpload = dateUpload;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Long getRdqId() {
        return rdqId;
    }

    public void setRdqId(Long rdqId) {
        this.rdqId = rdqId;
    }

    // Helper methods
    public String getExtension() {
        if (nomFichier != null && nomFichier.contains(".")) {
            return nomFichier.substring(nomFichier.lastIndexOf(".") + 1);
        }
        return "";
    }

    public String getFormattedFileSize() {
        if (tailleFichier == null) return "Unknown";
        
        double bytes = tailleFichier.doubleValue();
        if (bytes < 1024) return Math.round(bytes) + " B";
        if (bytes < 1024 * 1024) return Math.round(bytes / 1024) + " KB";
        if (bytes < 1024 * 1024 * 1024) return Math.round(bytes / (1024 * 1024)) + " MB";
        return Math.round(bytes / (1024 * 1024 * 1024)) + " GB";
    }
}