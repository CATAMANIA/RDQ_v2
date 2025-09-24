package com.vibecoding.rdq.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_client")
    private Long idClient;

    @NotBlank(message = "Le nom du client est obligatoire")
    @Size(max = 200, message = "Le nom du client ne peut pas dépasser 200 caractères")
    @Column(name = "nom", nullable = false, length = 200)
    private String nom;

    @Size(max = 100, message = "Le contact ne peut pas dépasser 100 caractères")
    @Column(name = "contact", length = 100)
    private String contact;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Projet> projets = new ArrayList<>();

    // Constructors
    public Client() {}

    public Client(String nom, String contact) {
        this.nom = nom;
        this.contact = contact;
    }

    // Getters and Setters
    public Long getIdClient() {
        return idClient;
    }

    public void setIdClient(Long idClient) {
        this.idClient = idClient;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public List<Projet> getProjets() {
        return projets;
    }

    public void setProjets(List<Projet> projets) {
        this.projets = projets;
    }

    // Helper methods
    public void addProjet(Projet projet) {
        projets.add(projet);
        projet.setClient(this);
    }

    public void removeProjet(Projet projet) {
        projets.remove(projet);
        projet.setClient(null);
    }
}