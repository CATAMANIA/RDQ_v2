package com.vibecoding.rdq.controller;

import com.vibecoding.rdq.dto.*;
import com.vibecoding.rdq.entity.Document;
import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.repository.DocumentRepository;
import com.vibecoding.rdq.repository.RDQRepository;
import com.vibecoding.rdq.service.SharePointService;
import com.vibecoding.rdq.exception.FileStorageException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for file management operations
 */
@RestController
@RequestMapping("/api/files")
@Tag(name = "File Management", description = "APIs for managing file attachments with SharePoint integration")
public class FileController {

    @Autowired
    private SharePointService sharePointService;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private RDQRepository rdqRepository;

    /**
     * Upload a file to SharePoint and associate it with an RDQ
     */
    @PostMapping("/upload/{rdqId}")
    @Operation(summary = "Upload a file", description = "Upload a file to SharePoint and associate it with an RDQ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file or RDQ ID"),
        @ApiResponse(responseCode = "404", description = "RDQ not found"),
        @ApiResponse(responseCode = "500", description = "File upload failed")
    })
    public ResponseEntity<FileUploadResponse> uploadFile(
            @Parameter(description = "RDQ ID to associate the file with", required = true)
            @PathVariable Long rdqId,
            @Parameter(description = "File to upload", required = true)
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        try {
            // Validate RDQ exists
            Optional<RDQ> rdqOpt = rdqRepository.findById(rdqId);
            if (rdqOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new FileUploadResponse(false, "RDQ not found", null, null, null, null, null, null));
            }

            RDQ rdq = rdqOpt.get();
            String userEmail = authentication.getName();

            // Upload to SharePoint
            SharepointFileInfo sharePointInfo = sharePointService.uploadFile(file, rdqId.toString(), userEmail);

            // Create Document entity
            Document document = new Document();
            document.setNomFichier(file.getOriginalFilename());
            document.setType(Document.getTypeFromExtension(getFileExtension(file.getOriginalFilename())));
            document.setUrl("mock://sharepoint/download/" + sharePointInfo.getItemId()); // Required field
            document.setTailleFichier(file.getSize());
            document.setMimeType(file.getContentType());
            document.setSharepointItemId(sharePointInfo.getItemId());
            document.setSharepointDriveId(sharePointInfo.getDriveId());
            document.setSharepointDownloadUrl("mock://sharepoint/download/" + sharePointInfo.getItemId());
            document.setDateUpload(LocalDateTime.now());
            document.setUploadedBy(userEmail);
            document.setRdq(rdq);

            // Save to database
            Document savedDocument = documentRepository.save(document);

            // Create response
            FileUploadResponse response = new FileUploadResponse(
                true,
                "File uploaded successfully",
                savedDocument.getId(),
                sharePointInfo.getItemId(),
                file.getOriginalFilename(),
                file.getSize(),
                document.getSharepointDownloadUrl(),
                sharePointInfo.getFormattedFileSize()
            );

            return ResponseEntity.ok(response);

        } catch (FileStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new FileUploadResponse(false, "File upload failed: " + e.getMessage(), 
                    null, null, null, null, null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new FileUploadResponse(false, "Unexpected error: " + e.getMessage(),
                    null, null, null, null, null, null));
        }
    }

    /**
     * Get list of files for an RDQ
     */
    @GetMapping("/rdq/{rdqId}")
    @Operation(summary = "Get files for an RDQ", description = "Retrieve all files associated with a specific RDQ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Files retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "RDQ not found")
    })
    public ResponseEntity<FileListResponse> getFilesForRDQ(
            @Parameter(description = "RDQ ID", required = true)
            @PathVariable Long rdqId) {
        
        // Validate RDQ exists
        Optional<RDQ> rdqOpt = rdqRepository.findById(rdqId);
        if (rdqOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new FileListResponse(List.of(), 0, "RDQ not found"));
        }

        // Get documents for RDQ
        List<Document> documents = documentRepository.findByRdqIdRdq(rdqId);
        
        // Convert to DTOs
        List<DocumentResponse> documentResponses = documents.stream()
            .map(this::convertToDocumentResponse)
            .collect(Collectors.toList());

        FileListResponse response = new FileListResponse(
            documentResponses, 
            documentResponses.size(), 
            "Files retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Download a file by document ID
     */
    @GetMapping("/download/{documentId}")
    @Operation(summary = "Download a file", description = "Download a file by document ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "500", description = "File download failed")
    })
    public ResponseEntity<byte[]> downloadFile(
            @Parameter(description = "Document ID", required = true)
            @PathVariable Long documentId) {
        
        try {
            // Find document
            Optional<Document> documentOpt = documentRepository.findById(documentId);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Document document = documentOpt.get();

            // Download from SharePoint
            InputStream inputStream = sharePointService.downloadFile(document.getSharepointItemId());
            byte[] fileContent = inputStream.readAllBytes();

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(document.getMimeType()));
            headers.setContentDispositionFormData("attachment", document.getNomFichier());
            headers.setContentLength(fileContent.length);

            return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);

        } catch (FileStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get document information by ID
     */
    @GetMapping("/{documentId}")
    @Operation(summary = "Get document information", description = "Get detailed information about a document")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document information retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found")
    })
    public ResponseEntity<DocumentResponse> getDocumentInfo(
            @Parameter(description = "Document ID", required = true)
            @PathVariable Long documentId) {
        
        Optional<Document> documentOpt = documentRepository.findById(documentId);
        if (documentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        DocumentResponse response = convertToDocumentResponse(documentOpt.get());
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a file by document ID
     */
    @DeleteMapping("/{documentId}")
    @Operation(summary = "Delete a file", description = "Delete a file by document ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "500", description = "File deletion failed")
    })
    public ResponseEntity<FileDeleteResponse> deleteFile(
            @Parameter(description = "Document ID", required = true)
            @PathVariable Long documentId,
            Authentication authentication) {
        
        try {
            // Find document
            Optional<Document> documentOpt = documentRepository.findById(documentId);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new FileDeleteResponse(false, "Document not found"));
            }

            Document document = documentOpt.get();

            // Delete from SharePoint
            sharePointService.deleteFile(document.getSharepointItemId());

            // Delete from database
            documentRepository.delete(document);

            FileDeleteResponse response = new FileDeleteResponse(
                true, 
                "File deleted successfully", 
                documentId, 
                document.getNomFichier()
            );

            return ResponseEntity.ok(response);

        } catch (FileStorageException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new FileDeleteResponse(false, "File deletion failed: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new FileDeleteResponse(false, "Unexpected error: " + e.getMessage()));
        }
    }

    // Helper methods

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
        }
        return "";
    }

    private DocumentResponse convertToDocumentResponse(Document document) {
        return new DocumentResponse(
            document.getId(),
            document.getNomFichier(),
            document.getType().name(),
            document.getTailleFichier(),
            document.getMimeType(),
            document.getSharepointItemId(),
            document.getSharepointDownloadUrl(),
            document.getDateUpload(),
            document.getUploadedBy(),
            document.getRdq().getId()
        );
    }
}