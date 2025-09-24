package com.vibecoding.rdq.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for file upload response
 */
@Schema(description = "Response after successful file upload")
public class FileUploadResponse {

    @Schema(description = "Unique document ID", example = "123")
    private Long documentId;

    @Schema(description = "SharePoint item ID", example = "mock_item_1695825600000")
    private String itemId;

    @Schema(description = "Original file name", example = "CV_Jean_Dupont.pdf")
    private String originalFileName;

    @Schema(description = "Unique file name in storage", example = "CV_Jean_Dupont_1695825600000.pdf")
    private String fileName;

    @Schema(description = "File size in bytes", example = "1048576")
    private Long fileSize;

    @Schema(description = "File type", example = "PDF")
    private String fileType;

    @Schema(description = "Download URL")
    private String downloadUrl;

    @Schema(description = "Upload status message", example = "File uploaded successfully")
    private String message;

    // Constructors
    public FileUploadResponse() {}

    public FileUploadResponse(Long documentId, String itemId, String originalFileName, String fileName, 
                             Long fileSize, String fileType, String downloadUrl, String message) {
        this.documentId = documentId;
        this.itemId = itemId;
        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.downloadUrl = downloadUrl;
        this.message = message;
    }

    public FileUploadResponse(boolean success, String message, Long documentId, String itemId, 
                             String originalFileName, Long fileSize, String downloadUrl, String formattedSize) {
        this.documentId = documentId;
        this.itemId = itemId;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
        this.downloadUrl = downloadUrl;
        this.message = message;
    }

    // Getters and Setters
    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}