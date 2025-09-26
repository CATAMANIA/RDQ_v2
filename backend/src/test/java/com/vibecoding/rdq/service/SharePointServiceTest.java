package com.vibecoding.rdq.service;

import com.vibecoding.rdq.config.SharePointConfig;
import com.vibecoding.rdq.dto.SharepointFileInfo;
import com.vibecoding.rdq.exception.FileStorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SharePointServiceTest {

    @Mock
    private SharePointConfig sharePointConfig;

    @InjectMocks
    private SharePointService sharePointService;

    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        // Configure mock SharePointConfig
        when(sharePointConfig.getMaxFileSize()).thenReturn(10L * 1024 * 1024); // 10MB
        when(sharePointConfig.getAllowedExtensions()).thenReturn(new String[]{"pdf", "doc", "docx", "jpg", "png"});
        when(sharePointConfig.isExtensionAllowed("pdf")).thenReturn(true);
        when(sharePointConfig.isExtensionAllowed("txt")).thenReturn(false);
        when(sharePointConfig.isMimeTypeAllowed("application/pdf")).thenReturn(true);
        when(sharePointConfig.isMimeTypeAllowed("text/plain")).thenReturn(false);
        when(sharePointConfig.getDriveId()).thenReturn("mock_drive_123");

        // Create mock file
        mockFile = new MockMultipartFile(
            "test-file",
            "test-document.pdf",
            "application/pdf",
            "Test file content".getBytes()
        );
    }

    @Test
    void testUploadFile_Success() throws IOException {
        // Given
        String rdqId = "123";
        String userEmail = "test@company.com";

        // When
        SharepointFileInfo result = sharePointService.uploadFile(mockFile, rdqId, userEmail);

        // Then
        assertNotNull(result);
        assertTrue(result.getFileName().startsWith("test-document_"));
        assertTrue(result.getFileName().endsWith(".pdf"));
        assertEquals(mockFile.getSize(), result.getFileSize());
        assertEquals(userEmail, result.getUploadedBy());
        assertNotNull(result.getItemId());
        assertNotNull(result.getDriveId());
        assertTrue(result.getItemId().startsWith("mock_item_"));
        assertTrue(result.getDriveId().startsWith("mock_drive_"));
    }

    @Test
    void testUploadFile_EmptyFile() {
        // Given
        MultipartFile emptyFile = new MockMultipartFile("empty", "", "text/plain", new byte[0]);
        String rdqId = "123";
        String userEmail = "test@company.com";

        // When & Then
        assertThrows(FileStorageException.class, () -> {
            sharePointService.uploadFile(emptyFile, rdqId, userEmail);
        });
    }

    @Test
    void testUploadFile_FileTooLarge() {
        // Given
        when(sharePointConfig.getMaxFileSize()).thenReturn(1L); // 1 byte limit
        String rdqId = "123";
        String userEmail = "test@company.com";

        // When & Then
        assertThrows(FileStorageException.class, () -> {
            sharePointService.uploadFile(mockFile, rdqId, userEmail);
        });
    }

    @Test
    void testUploadFile_InvalidExtension() {
        // Given
        MultipartFile invalidFile = new MockMultipartFile(
            "invalid-file",
            "test.txt",
            "text/plain",
            "Invalid file content".getBytes()
        );
        String rdqId = "123";
        String userEmail = "test@company.com";

        // When & Then
        assertThrows(FileStorageException.class, () -> {
            sharePointService.uploadFile(invalidFile, rdqId, userEmail);
        });
    }

    @Test
    void testDownloadFile_Success() throws IOException {
        // Given - First upload a file
        String rdqId = "123";
        String userEmail = "test@company.com";
        SharepointFileInfo uploadResult = sharePointService.uploadFile(mockFile, rdqId, userEmail);

        // When
        InputStream result = sharePointService.downloadFile(uploadResult.getItemId());

        // Then
        assertNotNull(result);
        byte[] content = result.readAllBytes();
        assertEquals("Test file content", new String(content));
    }

    @Test
    void testDownloadFile_FileNotFound() {
        // Given
        String nonExistentItemId = "non-existent-id";

        // When & Then
        assertThrows(FileStorageException.class, () -> {
            sharePointService.downloadFile(nonExistentItemId);
        });
    }

    @Test
    void testDeleteFile_Success() throws IOException {
        // Given - First upload a file
        String rdqId = "123";
        String userEmail = "test@company.com";
        SharepointFileInfo uploadResult = sharePointService.uploadFile(mockFile, rdqId, userEmail);

        // When
        assertDoesNotThrow(() -> {
            sharePointService.deleteFile(uploadResult.getItemId());
        });

        // Then - File should no longer be downloadable
        assertThrows(FileStorageException.class, () -> {
            sharePointService.downloadFile(uploadResult.getItemId());
        });
    }

    @Test
    void testDeleteFile_FileNotFound() {
        // Given
        String nonExistentItemId = "non-existent-id";

        // When & Then
        assertThrows(FileStorageException.class, () -> {
            sharePointService.deleteFile(nonExistentItemId);
        });
    }

    @Test
    void testGetFileInfo_Success() throws IOException {
        // Given - First upload a file
        String rdqId = "123";
        String userEmail = "test@company.com";
        SharepointFileInfo uploadResult = sharePointService.uploadFile(mockFile, rdqId, userEmail);

        // When
        SharepointFileInfo result = sharePointService.getFileInfo(uploadResult.getItemId());

        // Then
        assertNotNull(result);
        assertEquals(uploadResult.getItemId(), result.getItemId());
        assertEquals(uploadResult.getFileName(), result.getFileName());
        assertEquals(uploadResult.getFileSize(), result.getFileSize());
        assertEquals(uploadResult.getUploadedBy(), result.getUploadedBy());
    }

    @Test
    void testGetFileInfo_FileNotFound() {
        // Given
        String nonExistentItemId = "non-existent-id";

        // When & Then
        assertThrows(FileStorageException.class, () -> {
            sharePointService.getFileInfo(nonExistentItemId);
        });
    }
}