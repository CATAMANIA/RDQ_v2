package com.vibecoding.rdq.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "bilans")
public class Bilan {

    // Enum pour le type d'auteur
    public enum TypeAuteur {
        MANAGER, COLLABORATEUR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bilan")
    private Long idBilan;

    @Min(value = 1, message = "La note doit être comprise entre 1 et 10")
    @Max(value = 10, message = "La note doit être comprise entre 1 et 10")
    @Column(name = "note", nullable = false)
    private Integer note;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_auteur", nullable = false)
    private TypeAuteur typeAuteur;

    @NotBlank(message = "L'auteur est obligatoire")
    @Size(max = 100, message = "L'auteur ne peut pas dépasser 100 caractères")
    @Column(name = "auteur", nullable = false, length = 100)
    private String auteur;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rdq", nullable = false)
    private RDQ rdq;

    // Constructors
    public Bilan() {}

    public Bilan(Integer note, String commentaire, TypeAuteur typeAuteur, String auteur, RDQ rdq) {
        this.note = note;
        this.commentaire = commentaire;
        this.typeAuteur = typeAuteur;
        this.auteur = auteur;
        this.rdq = rdq;
    }

    // Getters and Setters
    public Long getIdBilan() {
        return idBilan;
    }

    public void setIdBilan(Long idBilan) {
        this.idBilan = idBilan;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public TypeAuteur getTypeAuteur() {
        return typeAuteur;
    }

    public void setTypeAuteur(TypeAuteur typeAuteur) {
        this.typeAuteur = typeAuteur;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public RDQ getRdq() {
        return rdq;
    }

    public void setRdq(RDQ rdq) {
        this.rdq = rdq;
    }

    // Helper methods
    public boolean isPositive() {
        return note != null && note >= 4;
    }

    public boolean isNegative() {
        return note != null && note <= 2;
    }
}