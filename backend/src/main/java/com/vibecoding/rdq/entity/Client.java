package com.vibecoding.rdq.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Column(name = "email", nullable = false, unique = true)
    private String contactEmail;

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
    @Column(name = "telephone", length = 20)
    private String contactTelephone;

    public Client(Long idClient,
            @NotBlank(message = "Le nom du client est obligatoire") @Size(max = 200, message = "Le nom du client ne peut pas dépasser 200 caractères") String nom,
            @Size(max = 100, message = "Le contact ne peut pas dépasser 100 caractères") String contact,@Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères") String contactTelephone,
            @NotBlank(message = "L'email est obligatoire") @Email(message = "L'email doit être valide") String contactEmail
            ) {
        this.idClient = idClient;
        this.nom = nom;
        this.contact = contact;
        this.contactEmail = contactEmail;
        this.contactTelephone = contactTelephone;
    }

    public String getContactTelephone() {
        return contactTelephone;
    }

    public void setContactTelephone(String contactTelephone) {
        this.contactTelephone = contactTelephone;
    }

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