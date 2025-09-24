package com.vibecoding.rdq.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    // Helper methods
    public String getExtension() {
        if (nomFichier != null && nomFichier.contains(".")) {
            return nomFichier.substring(nomFichier.lastIndexOf(".") + 1);
        }
        return "";
    }
}