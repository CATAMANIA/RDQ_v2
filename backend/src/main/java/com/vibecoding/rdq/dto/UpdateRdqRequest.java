package com.vibecoding.rdq.dto;

import com.vibecoding.rdq.entity.RDQ;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour la modification d'un RDQ existant
 * Tous les champs sont optionnels pour permettre les modifications partielles
 */
public class UpdateRdqRequest {

    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    private LocalDateTime dateHeure;

    @Size(max = 500, message = "L'adresse ne peut pas dépasser 500 caractères")
    private String adresse;

    private RDQ.ModeRDQ mode;

    private String description;

    private Long projetId;

    private List<Long> collaborateurIds;

    // Constructors
    public UpdateRdqRequest() {}

    // Getters and Setters
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

    public RDQ.ModeRDQ getMode() {
        return mode;
    }

    public void setMode(RDQ.ModeRDQ mode) {
        this.mode = mode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProjetId() {
        return projetId;
    }

    public void setProjetId(Long projetId) {
        this.projetId = projetId;
    }

    public List<Long> getCollaborateurIds() {
        return collaborateurIds;
    }

    public void setCollaborateurIds(List<Long> collaborateurIds) {
        this.collaborateurIds = collaborateurIds;
    }

    @Override
    public String toString() {
        return "UpdateRdqRequest{" +
                "titre='" + titre + '\'' +
                ", dateHeure=" + dateHeure +
                ", adresse='" + adresse + '\'' +
                ", mode=" + mode +
                ", description='" + description + '\'' +
                ", projetId=" + projetId +
                ", collaborateurIds=" + collaborateurIds +
                '}';
    }
}