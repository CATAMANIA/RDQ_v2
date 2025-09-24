package com.vibecoding.rdq.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO for file list response
 */
@Schema(description = "Response containing list of documents")
public class FileListResponse {

    @Schema(description = "List of documents")
    private List<DocumentResponse> documents;

    @Schema(description = "Total number of documents", example = "5")
    private int totalCount;

    @Schema(description = "Success message", example = "Files retrieved successfully")
    private String message;

    // Constructors
    public FileListResponse() {}

    public FileListResponse(List<DocumentResponse> documents, int totalCount, String message) {
        this.documents = documents;
        this.totalCount = totalCount;
        this.message = message;
    }

    // Getters and Setters
    public List<DocumentResponse> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentResponse> documents) {
        this.documents = documents;
        this.totalCount = documents != null ? documents.size() : 0;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}