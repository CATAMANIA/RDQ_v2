package com.vibecoding.rdq.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_document")
    private Long idDocument;

    @NotBlank(message = "Le nom du fichier est obligatoire")
    @Size(max = 255, message = "Le nom du fichier ne peut pas dépasser 255 caractères")
    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeDocument type;

    @NotBlank(message = "L'URL est obligatoire")
    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "taille_fichier")
    private Long tailleFichier;

    // SharePoint specific fields
    @Column(name = "sharepoint_item_id", length = 255)
    private String sharepointItemId;

    @Column(name = "sharepoint_drive_id", length = 255)
    private String sharepointDriveId;

    @Column(name = "sharepoint_download_url", length = 1000)
    private String sharepointDownloadUrl;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "date_upload")
    private LocalDateTime dateUpload;

    @Column(name = "uploaded_by", length = 255)
    private String uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rdq", nullable = false)
    private RDQ rdq;

    // Enum
    public enum TypeDocument {
        PDF, WORD, EXCEL, POWERPOINT, IMAGE, AUTRE
    }

    // Constructors
    public Document() {}

    public Document(String nomFichier, TypeDocument type, String url, RDQ rdq) {
        this.nomFichier = nomFichier;
        this.type = type;
        this.url = url;
        this.rdq = rdq;
    }

    // Getters and Setters
    public Long getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(Long idDocument) {
        this.idDocument = idDocument;
    }

    public Long getId() {
        return idDocument;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public TypeDocument getType() {
        return type;
    }

    public void setType(TypeDocument type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getTailleFichier() {
        return tailleFichier;
    }

    public void setTailleFichier(Long tailleFichier) {
        this.tailleFichier = tailleFichier;
    }

    public RDQ getRdq() {
        return rdq;
    }

    public void setRdq(RDQ rdq) {
        this.rdq = rdq;
    }

    public String getSharepointItemId() {
        return sharepointItemId;
    }

    public void setSharepointItemId(String sharepointItemId) {
        this.sharepointItemId = sharepointItemId;
    }

    public String getSharepointDriveId() {
        return sharepointDriveId;
    }

    public void setSharepointDriveId(String sharepointDriveId) {
        this.sharepointDriveId = sharepointDriveId;
    }

    public String getSharepointDownloadUrl() {
        return sharepointDownloadUrl;
    }

    public void setSharepointDownloadUrl(String sharepointDownloadUrl) {
        this.sharepointDownloadUrl = sharepointDownloadUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
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

    // Helper methods
    public String getExtension() {
        if (nomFichier != null && nomFichier.contains(".")) {
            return nomFichier.substring(nomFichier.lastIndexOf(".") + 1);
        }
        return "";
    }

    public boolean isSharePointDocument() {
        return sharepointItemId != null && !sharepointItemId.trim().isEmpty();
    }

    public static TypeDocument getTypeFromExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return TypeDocument.AUTRE;
        }
        
        String ext = extension.toLowerCase();
        switch (ext) {
            case "pdf":
                return TypeDocument.PDF;
            case "doc":
            case "docx":
                return TypeDocument.WORD;
            case "xls":
            case "xlsx":
                return TypeDocument.EXCEL;
            case "ppt":
            case "pptx":
                return TypeDocument.POWERPOINT;
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
                return TypeDocument.IMAGE;
            default:
                return TypeDocument.AUTRE;
        }
    }
}