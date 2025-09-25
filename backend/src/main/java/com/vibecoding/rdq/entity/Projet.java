package com.vibecoding.rdq.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projets")
public class Projet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_projet")
    private Long idProjet;

    @NotBlank(message = "Le nom du projet est obligatoire")
    @Size(max = 200, message = "Le nom du projet ne peut pas dépasser 200 caractères")
    @Column(name = "nom", nullable = false, length = 200)
    private String nom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RDQ> rdqs = new ArrayList<>();

    public Projet(Long idProjet,
            @NotBlank(message = "Le nom du projet est obligatoire") @Size(max = 200, message = "Le nom du projet ne peut pas dépasser 200 caractères") String nom) {
        this.idProjet = idProjet;
        this.nom = nom;
    }

    // Constructors
    public Projet() {}

    public Projet(String nom, Client client) {
        this.nom = nom;
        this.client = client;
    }

    // Getters and Setters
    public Long getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(Long idProjet) {
        this.idProjet = idProjet;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<RDQ> getRdqs() {
        return rdqs;
    }

    public void setRdqs(List<RDQ> rdqs) {
        this.rdqs = rdqs;
    }

    // Helper methods
    public void addRdq(RDQ rdq) {
        rdqs.add(rdq);
        rdq.setProjet(this);
    }

    public void removeRdq(RDQ rdq) {
        rdqs.remove(rdq);
        rdq.setProjet(null);
    }
}