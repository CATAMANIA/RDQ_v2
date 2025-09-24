package com.vibecoding.rdq.controller;

import com.vibecoding.rdq.entity.Document;
import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.repository.DocumentRepository;
import com.vibecoding.rdq.repository.RDQRepository;
import com.vibecoding.rdq.service.SharePointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;



    @MockBean
    private SharePointService sharePointService;

    @MockBean
    private DocumentRepository documentRepository;

    @MockBean
    private RDQRepository rdqRepository;

    private RDQ mockRdq;
    private Document mockDocument;

    @BeforeEach
    void setUp() {
        // Create mock RDQ
        mockRdq = new RDQ();
        mockRdq.setIdRdq(1L);
        mockRdq.setTitre("Test RDQ");
        mockRdq.setDateHeure(LocalDateTime.now());

        // Create mock Document
        mockDocument = new Document();
        mockDocument.setIdDocument(1L);
        mockDocument.setNomFichier("test-document.pdf");
        mockDocument.setType(Document.TypeDocument.PDF);
        mockDocument.setUrl("mock://sharepoint/download/mock_item_123");
        mockDocument.setTailleFichier(1024L);
        mockDocument.setMimeType("application/pdf");
        mockDocument.setSharepointItemId("mock_item_123");
        mockDocument.setSharepointDriveId("mock_drive_123");
        mockDocument.setSharepointDownloadUrl("mock://sharepoint/download/mock_item_123");
        mockDocument.setDateUpload(LocalDateTime.now());
        mockDocument.setUploadedBy("test@company.com");
        mockDocument.setRdq(mockRdq);
    }

    @Test
    @WithMockUser(username = "test@company.com")
    void testUploadFile_Success() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test-document.pdf",
            "application/pdf",
            "Test file content".getBytes()
        );

        when(rdqRepository.findById(1L)).thenReturn(Optional.of(mockRdq));
        when(documentRepository.save(any(Document.class))).thenReturn(mockDocument);

        // When & Then
        mockMvc.perform(multipart("/api/files/upload/1")
                .file(file)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("File uploaded successfully"))
                .andExpect(jsonPath("$.originalFileName").value("test-document.pdf"));
    }

    @Test
    @WithMockUser(username = "test@company.com")
    void testUploadFile_RdqNotFound() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test-document.pdf",
            "application/pdf",
            "Test file content".getBytes()
        );

        when(rdqRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(multipart("/api/files/upload/999")
                .file(file)
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("RDQ not found"));
    }

    @Test
    @WithMockUser(username = "test@company.com")
    void testGetFilesForRDQ_Success() throws Exception {
        // Given
        List<Document> documents = new ArrayList<>();
        documents.add(mockDocument);

        when(rdqRepository.findById(1L)).thenReturn(Optional.of(mockRdq));
        when(documentRepository.findByRdqIdRdq(1L)).thenReturn(documents);

        // When & Then
        mockMvc.perform(get("/api/files/rdq/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.documents").isArray())
                .andExpect(jsonPath("$.documents", hasSize(1)))
                .andExpect(jsonPath("$.totalCount").value(1))
                .andExpect(jsonPath("$.message").value("Files retrieved successfully"));
    }

    @Test
    @WithMockUser(username = "test@company.com")
    void testGetFilesForRDQ_RdqNotFound() throws Exception {
        // Given
        when(rdqRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/files/rdq/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("RDQ not found"));
    }

    @Test
    @WithMockUser(username = "test@company.com")
    void testGetDocumentInfo_Success() throws Exception {
        // Given
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));

        // When & Then
        mockMvc.perform(get("/api/files/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomFichier").value("test-document.pdf"))
                .andExpect(jsonPath("$.type").value("PDF"))
                .andExpect(jsonPath("$.mimeType").value("application/pdf"));
    }

    @Test
    @WithMockUser(username = "test@company.com")
    void testGetDocumentInfo_NotFound() throws Exception {
        // Given
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/files/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@company.com")
    void testDeleteFile_Success() throws Exception {
        // Given
        when(documentRepository.findById(1L)).thenReturn(Optional.of(mockDocument));

        // When & Then
        mockMvc.perform(delete("/api/files/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("File deleted successfully"))
                .andExpect(jsonPath("$.documentId").value(1))
                .andExpect(jsonPath("$.fileName").value("test-document.pdf"));
    }

    @Test
    @WithMockUser(username = "test@company.com")
    void testDeleteFile_NotFound() throws Exception {
        // Given
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(delete("/api/files/999")
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Document not found"));
    }
}