package com.vibecoding.rdq.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rdqs")
public class RDQ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rdq")
    private Long idRdq;

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    @Column(name = "titre", nullable = false)
    private String titre;

    @NotNull(message = "La date et heure sont obligatoires")
    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;

    @Size(max = 500, message = "L'adresse ne peut pas dépasser 500 caractères")
    @Column(name = "adresse", length = 500)
    private String adresse;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    private ModeRDQ mode = ModeRDQ.PRESENTIEL;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutRDQ statut = StatutRDQ.PLANIFIE;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_manager", nullable = false)
    private Manager manager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projet", nullable = false)
    private Projet projet;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rdq_collaborateurs",
        joinColumns = @JoinColumn(name = "id_rdq"),
        inverseJoinColumns = @JoinColumn(name = "id_collaborateur")
    )
    private List<Collaborateur> collaborateurs = new ArrayList<>();

    @OneToMany(mappedBy = "rdq", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Document> documents = new ArrayList<>();

    @OneToMany(mappedBy = "rdq", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bilan> bilans = new ArrayList<>();

    // Enums
    public enum ModeRDQ {
        PRESENTIEL, DISTANCIEL, HYBRIDE
    }

    public enum StatutRDQ {
        PLANIFIE, EN_COURS, TERMINE, ANNULE
    }

    // Constructors
    public RDQ() {}

    public RDQ(String titre, LocalDateTime dateHeure, String adresse, ModeRDQ mode, Manager manager, Projet projet) {
        this.titre = titre;
        this.dateHeure = dateHeure;
        this.adresse = adresse;
        this.mode = mode;
        this.manager = manager;
        this.projet = projet;
    }

    // Getters and Setters
    public Long getIdRdq() {
        return idRdq;
    }

    public void setIdRdq(Long idRdq) {
        this.idRdq = idRdq;
    }

    public Long getId() {
        return idRdq;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public ModeRDQ getMode() {
        return mode;
    }

    public void setMode(ModeRDQ mode) {
        this.mode = mode;
    }

    public StatutRDQ getStatut() {
        return statut;
    }

    public void setStatut(StatutRDQ statut) {
        this.statut = statut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public Projet getProjet() {
        return projet;
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }

    public List<Collaborateur> getCollaborateurs() {
        return collaborateurs;
    }

    public void setCollaborateurs(List<Collaborateur> collaborateurs) {
        this.collaborateurs = collaborateurs;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Bilan> getBilans() {
        return bilans;
    }

    public void setBilans(List<Bilan> bilans) {
        this.bilans = bilans;
    }

    // Helper methods
    public void addCollaborateur(Collaborateur collaborateur) {
        collaborateurs.add(collaborateur);
        collaborateur.getRdqs().add(this);
    }

    public void removeCollaborateur(Collaborateur collaborateur) {
        collaborateurs.remove(collaborateur);
        collaborateur.getRdqs().remove(this);
    }

    public void addDocument(Document document) {
        documents.add(document);
        document.setRdq(this);
    }

    public void removeDocument(Document document) {
        documents.remove(document);
        document.setRdq(null);
    }

    public void addBilan(Bilan bilan) {
        bilans.add(bilan);
        bilan.setRdq(this);
    }

    public void removeBilan(Bilan bilan) {
        bilans.remove(bilan);
        bilan.setRdq(null);
    }
}