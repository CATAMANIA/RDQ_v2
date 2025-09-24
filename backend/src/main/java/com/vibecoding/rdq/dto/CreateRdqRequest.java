package com.vibecoding.rdq.dto;

import com.vibecoding.rdq.entity.RDQ.ModeRDQ;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour la création d'un nouveau RDQ
 */
@Schema(description = "Données pour créer un nouveau RDQ")
public class CreateRdqRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    @Schema(description = "Titre du RDQ", example = "RDQ Société ABC - Développement App Mobile")
    private String titre;

    @NotNull(message = "La date et heure sont obligatoires")
    @Future(message = "La date doit être dans le futur")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Date et heure du RDQ", example = "2025-10-15 14:30:00")
    private LocalDateTime dateHeure;

    @Size(max = 500, message = "L'adresse ne peut pas dépasser 500 caractères")
    @Schema(description = "Adresse du RDQ (optionnel pour mode distanciel)", example = "123 Rue de la Paix, 75001 Paris")
    private String adresse;

    @NotNull(message = "Le mode est obligatoire")
    @Schema(description = "Mode du RDQ", example = "PRESENTIEL")
    private ModeRDQ mode;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    @Schema(description = "Description détaillée du RDQ", example = "Rencontre pour évaluer les besoins en développement mobile")
    private String description;

    @NotNull(message = "L'ID du projet est obligatoire")
    @Schema(description = "ID du projet associé", example = "1")
    private Long projetId;

    @NotEmpty(message = "Au moins un collaborateur doit être assigné")
    @Schema(description = "Liste des IDs des collaborateurs assignés", example = "[1, 2, 3]")
    private List<Long> collaborateurIds;

    @Schema(description = "Indications supplémentaires pour le RDQ", example = "Prévoir matériel de présentation")
    private String indications;

    // Constructors
    public CreateRdqRequest() {}

    public CreateRdqRequest(String titre, LocalDateTime dateHeure, String adresse, ModeRDQ mode, 
                           String description, Long projetId, List<Long> collaborateurIds, String indications) {
        this.titre = titre;
        this.dateHeure = dateHeure;
        this.adresse = adresse;
        this.mode = mode;
        this.description = description;
        this.projetId = projetId;
        this.collaborateurIds = collaborateurIds;
        this.indications = indications;
    }

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

    public ModeRDQ getMode() {
        return mode;
    }

    public void setMode(ModeRDQ mode) {
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

    public String getIndications() {
        return indications;
    }

    public void setIndications(String indications) {
        this.indications = indications;
    }

    @Override
    public String toString() {
        return "CreateRdqRequest{" +
                "titre='" + titre + '\'' +
                ", dateHeure=" + dateHeure +
                ", adresse='" + adresse + '\'' +
                ", mode=" + mode +
                ", description='" + description + '\'' +
                ", projetId=" + projetId +
                ", collaborateurIds=" + collaborateurIds +
                ", indications='" + indications + '\'' +
                '}';
    }
}