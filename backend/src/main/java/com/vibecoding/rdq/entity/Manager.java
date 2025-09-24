package com.vibecoding.rdq.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "managers")
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_manager")
    private Long idManager;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
    @Column(name = "telephone", length = 20)
    private String telephone;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RDQ> rdqs = new ArrayList<>();

    // Constructors
    public Manager() {}

    public Manager(String nom, String prenom, String email, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
    }

    // Getters and Setters
    public Long getIdManager() {
        return idManager;
    }

    public void setIdManager(Long idManager) {
        this.idManager = idManager;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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
        rdq.setManager(this);
    }

    public void removeRdq(RDQ rdq) {
        rdqs.remove(rdq);
        rdq.setManager(null);
    }

    public String getFullName() {
        return prenom + " " + nom;
    }
}