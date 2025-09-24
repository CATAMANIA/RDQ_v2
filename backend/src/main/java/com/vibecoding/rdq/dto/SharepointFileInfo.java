package com.vibecoding.rdq.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO for SharePoint file information
 */
@Schema(description = "Information about a file stored in SharePoint")
public class SharepointFileInfo {

    @Schema(description = "SharePoint item ID", example = "01BYE5RZ56Y2GOVW7725BZO354PWSELRRZ")
    private String itemId;

    @Schema(description = "SharePoint drive ID", example = "b!-RIj2DuyvEyV1T4NlOaMHk8XkS_I8MdFlUCq1BlcjgmhRfAj3-Z8RY2VpuvV_tpd")
    private String driveId;

    @Schema(description = "Original file name", example = "CV_Jean_Dupont.pdf")
    private String originalFileName;

    @Schema(description = "Unique file name in SharePoint", example = "CV_Jean_Dupont_1695825600000.pdf")
    private String fileName;

    @Schema(description = "MIME type of the file", example = "application/pdf")
    private String mimeType;

    @Schema(description = "File size in bytes", example = "1048576")
    private Long fileSize;

    @Schema(description = "SharePoint download URL")
    private String downloadUrl;

    @Schema(description = "User who uploaded the file", example = "manager@company.com")
    private String uploadedBy;

    @Schema(description = "Upload date and time")
    private LocalDateTime dateUpload;

    // Constructors
    public SharepointFileInfo() {}

    public SharepointFileInfo(String itemId, String driveId, String originalFileName, String fileName, 
                             String mimeType, Long fileSize, String downloadUrl, String uploadedBy, 
                             LocalDateTime dateUpload) {
        this.itemId = itemId;
        this.driveId = driveId;
        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.downloadUrl = downloadUrl;
        this.uploadedBy = uploadedBy;
        this.dateUpload = dateUpload;
    }

    // Getters and Setters
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(LocalDateTime dateUpload) {
        this.dateUpload = dateUpload;
    }

    // Helper methods
    public String getExtension() {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }

    public String getFormattedFileSize() {
        if (fileSize == null) return "Unknown";
        
        double bytes = fileSize.doubleValue();
        if (bytes < 1024) return Math.round(bytes) + " B";
        if (bytes < 1024 * 1024) return Math.round(bytes / 1024) + " KB";
        if (bytes < 1024 * 1024 * 1024) return Math.round(bytes / (1024 * 1024)) + " MB";
        return Math.round(bytes / (1024 * 1024 * 1024)) + " GB";
    }
}