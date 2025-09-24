package com.vibecoding.rdq.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for file delete response
 */
@Schema(description = "Response after file deletion")
public class FileDeleteResponse {

    @Schema(description = "Success status", example = "true")
    private boolean success;

    @Schema(description = "Response message", example = "File deleted successfully")
    private String message;

    @Schema(description = "Deleted document ID", example = "123")
    private Long documentId;

    @Schema(description = "Deleted file name", example = "CV_Jean_Dupont.pdf")
    private String fileName;

    // Constructors
    public FileDeleteResponse() {}

    public FileDeleteResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public FileDeleteResponse(boolean success, String message, Long documentId, String fileName) {
        this.success = success;
        this.message = message;
        this.documentId = documentId;
        this.fileName = fileName;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}