package com.vibecoding.rdq.service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.vibecoding.rdq.config.SharePointConfig;
import com.vibecoding.rdq.dto.SharepointFileInfo;
import com.vibecoding.rdq.exception.FileStorageException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for managing file operations with SharePoint via Microsoft Graph API
 * Note: This is a mock implementation for development. Production implementation will use actual SharePoint API.
 */
@Service
public class SharePointService {

    private static final Logger logger = LoggerFactory.getLogger(SharePointService.class);

    private final SharePointConfig sharepointConfig;
    private final RestTemplate restTemplate;
    
    // Mock storage for development
    private final Map<String, SharepointFileInfo> mockStorage = new HashMap<>();
    private final Map<String, byte[]> mockFileContent = new HashMap<>();

    @Autowired
    public SharePointService(SharePointConfig sharepointConfig) {
        this.sharepointConfig = sharepointConfig;
        this.restTemplate = new RestTemplate();
        logger.info("SharePointService initialized with mock implementation");
    }

    /**
     * Upload file to SharePoint (Mock implementation)
     */
    public SharepointFileInfo uploadFile(MultipartFile file, String rdqId, String uploadedBy) {
        validateFile(file);
        
        try {
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            String itemId = "mock_item_" + System.currentTimeMillis();
            
            // Store file content in mock storage
            mockFileContent.put(itemId, file.getBytes());
            
            // Create SharepointFileInfo
            SharepointFileInfo fileInfo = new SharepointFileInfo();
            fileInfo.setItemId(itemId);
            fileInfo.setDriveId(sharepointConfig.getDriveId());
            fileInfo.setFileName(fileName);
            fileInfo.setOriginalFileName(file.getOriginalFilename());
            fileInfo.setMimeType(file.getContentType());
            fileInfo.setFileSize(file.getSize());
            fileInfo.setDownloadUrl("https://mock-sharepoint.com/download/" + itemId);
            fileInfo.setUploadedBy(uploadedBy);
            fileInfo.setDateUpload(LocalDateTime.now());
            
            // Store in mock storage
            mockStorage.put(itemId, fileInfo);
            
            logger.info("File uploaded successfully to mock SharePoint: {}", fileName);
            return fileInfo;
            
        } catch (Exception e) {
            logger.error("Error uploading file to mock SharePoint: {}", file.getOriginalFilename(), e);
            throw new FileStorageException("Failed to upload file to SharePoint", e);
        }
    }

    /**
     * Download file from SharePoint (Mock implementation)
     */
    public InputStream downloadFile(String itemId) {
        try {
            byte[] content = mockFileContent.get(itemId);
            if (content == null) {
                throw new FileStorageException("File not found in mock SharePoint: " + itemId);
            }

            logger.info("File downloaded successfully from mock SharePoint: {}", itemId);
            return new ByteArrayInputStream(content);

        } catch (Exception e) {
            logger.error("Error downloading file from mock SharePoint: {}", itemId, e);
            throw new FileStorageException("Failed to download file from SharePoint", e);
        }
    }

    /**
     * Delete file from SharePoint (Mock implementation)
     */
    public void deleteFile(String itemId) {
        try {
            if (!mockStorage.containsKey(itemId)) {
                throw new FileStorageException("File not found in mock SharePoint: " + itemId);
            }

            mockStorage.remove(itemId);
            mockFileContent.remove(itemId);

            logger.info("File deleted successfully from mock SharePoint: {}", itemId);

        } catch (Exception e) {
            logger.error("Error deleting file from mock SharePoint: {}", itemId, e);
            throw new FileStorageException("Failed to delete file from SharePoint", e);
        }
    }

    /**
     * Get file information from SharePoint (Mock implementation)
     */
    public SharepointFileInfo getFileInfo(String itemId) {
        try {
            SharepointFileInfo fileInfo = mockStorage.get(itemId);
            if (fileInfo == null) {
                throw new FileStorageException("File not found in mock SharePoint: " + itemId);
            }

            return fileInfo;

        } catch (Exception e) {
            logger.error("Error getting file info from mock SharePoint: {}", itemId, e);
            throw new FileStorageException("Failed to get file info from SharePoint", e);
        }
    }

    // Private helper methods

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("File is empty or null");
        }

        if (file.getSize() > sharepointConfig.getMaxFileSize()) {
            throw new FileStorageException("File size exceeds maximum allowed size: " + sharepointConfig.getMaxFileSize());
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!sharepointConfig.isExtensionAllowed(extension)) {
            throw new FileStorageException("File extension not allowed: " + extension);
        }

        if (!sharepointConfig.isMimeTypeAllowed(file.getContentType())) {
            throw new FileStorageException("File type not allowed: " + file.getContentType());
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = FilenameUtils.getExtension(originalFileName);
        String baseName = FilenameUtils.getBaseName(originalFileName);
        String timestamp = String.valueOf(System.currentTimeMillis());
        
        return baseName + "_" + timestamp + "." + extension;
    }
}