package com.vibecoding.rdq.service;

import com.vibecoding.rdq.dto.CreateRdqRequest;
import com.vibecoding.rdq.dto.DocumentResponse;
import com.vibecoding.rdq.dto.RdqResponse;
import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.entity.Manager;
import com.vibecoding.rdq.entity.Collaborateur;
import com.vibecoding.rdq.entity.Projet;

import com.vibecoding.rdq.repository.RDQRepository;
import com.vibecoding.rdq.repository.ManagerRepository;
import com.vibecoding.rdq.repository.CollaborateurRepository;
import com.vibecoding.rdq.repository.ProjetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RDQService {

    @Autowired
    private RDQRepository rdqRepository;
    
    @Autowired
    private ManagerRepository managerRepository;
    
    @Autowired
    private CollaborateurRepository collaborateurRepository;
    
    @Autowired
    private ProjetRepository projetRepository;

    public List<RDQ> findAll() {
        return rdqRepository.findAll();
    }

    public Optional<RDQ> findById(Long id) {
        return rdqRepository.findById(id);
    }

    public RDQ save(RDQ rdq) {
        return rdqRepository.save(rdq);
    }

    public void deleteById(Long id) {
        rdqRepository.deleteById(id);
    }

    public List<RDQ> findByStatut(RDQ.StatutRDQ statut) {
        return rdqRepository.findByStatut(statut);
    }

    public List<RDQ> findByManagerId(Long managerId) {
        return rdqRepository.findByManagerIdManager(managerId);
    }

    public List<RDQ> findByProjetId(Long projetId) {
        return rdqRepository.findByProjetIdProjet(projetId);
    }

    public List<RDQ> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return rdqRepository.findByDateHeureBetween(startDate, endDate);
    }

    public Optional<RDQ> findByIdWithAllRelations(Long id) {
        return rdqRepository.findByIdWithAllRelations(id);
    }

    /**
     * Crée un nouveau RDQ avec validation complète
     * @param request Données de création du RDQ
     * @param managerId ID du manager créateur (obtenu depuis le contexte de sécurité)
     * @return RdqResponse avec les informations du RDQ créé
     * @throws RuntimeException si validation échoue
     */
    @Transactional
    public RdqResponse createRdq(CreateRdqRequest request, Long managerId) {
        // 1. Validation du manager
        Manager manager = managerRepository.findById(managerId)
            .orElseThrow(() -> new RuntimeException("Manager non trouvé avec l'ID: " + managerId));

        // 2. Validation du projet
        Projet projet = projetRepository.findById(request.getProjetId())
            .orElseThrow(() -> new RuntimeException("Projet non trouvé avec l'ID: " + request.getProjetId()));

        // 3. Validation des collaborateurs
        List<Collaborateur> collaborateurs = request.getCollaborateurIds().stream()
            .map(id -> collaborateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collaborateur non trouvé avec l'ID: " + id)))
            .collect(Collectors.toList());

        if (collaborateurs.isEmpty()) {
            throw new RuntimeException("Au moins un collaborateur doit être assigné au RDQ");
        }

        // 4. Validation de la date (doit être dans le futur)
        if (request.getDateHeure().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La date du RDQ doit être dans le futur");
        }

        // 5. Création de l'entité RDQ
        RDQ rdq = new RDQ();
        rdq.setTitre(request.getTitre());
        rdq.setDateHeure(request.getDateHeure());
        rdq.setAdresse(request.getAdresse());
        rdq.setMode(request.getMode());
        rdq.setDescription(request.getDescription());
        rdq.setManager(manager);
        rdq.setProjet(projet);
        rdq.setStatut(RDQ.StatutRDQ.PLANIFIE);

        // Ajout des collaborateurs
        for (Collaborateur collaborateur : collaborateurs) {
            rdq.addCollaborateur(collaborateur);
        }

        // 6. Sauvegarde en base de données
        RDQ savedRdq = rdqRepository.save(rdq);

        // 7. TODO: Envoyer notifications aux collaborateurs
        sendNotificationToCollaborateurs(savedRdq, collaborateurs);

        // 8. Conversion en DTO de réponse
        return mapToRdqResponse(savedRdq);
    }

    /**
     * Convertit une entité RDQ en DTO de réponse
     */
    private RdqResponse mapToRdqResponse(RDQ rdq) {
        RdqResponse response = new RdqResponse();
        response.setIdRdq(rdq.getIdRdq());
        response.setTitre(rdq.getTitre());
        response.setDateHeure(rdq.getDateHeure());
        response.setAdresse(rdq.getAdresse());
        response.setMode(rdq.getMode());
        response.setStatut(rdq.getStatut());
        response.setDescription(rdq.getDescription());
        response.setDateCreation(LocalDateTime.now()); // TODO: Ajouter dateCreation à l'entité

        // Manager info
        Manager manager = rdq.getManager();
        if (manager != null) {
            RdqResponse.ManagerInfo managerInfo = new RdqResponse.ManagerInfo(
                manager.getIdManager(),
                manager.getFullName(),
                manager.getEmail()
            );
            response.setManager(managerInfo);
        }

        // Projet info
        Projet projet = rdq.getProjet();
        if (projet != null) {
            RdqResponse.ProjetInfo projetInfo = new RdqResponse.ProjetInfo(
                projet.getIdProjet(),
                projet.getNom(),
                projet.getClient() != null ? projet.getClient().getNom() : null
            );
            response.setProjet(projetInfo);
        }

        // Collaborateurs info
        List<RdqResponse.CollaborateurInfo> collaborateursInfo = rdq.getCollaborateurs().stream()
            .map(c -> new RdqResponse.CollaborateurInfo(
                c.getIdCollaborateur(),
                c.getFullName(),
                c.getEmail()
            ))
            .collect(Collectors.toList());
        response.setCollaborateurs(collaborateursInfo);

        // Documents info
        List<DocumentResponse> documentsInfo = rdq.getDocuments().stream()
            .map(doc -> {
                DocumentResponse docResponse = new DocumentResponse();
                docResponse.setId(doc.getId());
                docResponse.setNomFichier(doc.getNomFichier());
                docResponse.setType(doc.getType() != null ? doc.getType().toString() : "AUTRE");
                docResponse.setTailleFichier(doc.getTailleFichier());
                docResponse.setMimeType(doc.getMimeType());
                docResponse.setDateUpload(doc.getDateUpload());
                docResponse.setUploadedBy(doc.getUploadedBy());
                docResponse.setSharepointItemId(doc.getSharepointItemId());
                docResponse.setSharepointDownloadUrl(doc.getSharepointDownloadUrl());
                docResponse.setRdqId(rdq.getIdRdq());
                return docResponse;
            })
            .collect(Collectors.toList());
        response.setDocuments(documentsInfo);

        return response;
    }

    /**
     * Envoie des notifications aux collaborateurs assignés
     * TODO: Implémenter le système de notification réel
     */
    private void sendNotificationToCollaborateurs(RDQ rdq, List<Collaborateur> collaborateurs) {
        // Placeholder pour le système de notification
        // Dans une implémentation réelle, ceci pourrait :
        // - Envoyer des emails
        // - Créer des notifications dans l'application
        // - Intégrer avec un service de messagerie externe
        
        String message = String.format(
            "Nouveau RDQ créé: %s le %s. Manager: %s",
            rdq.getTitre(),
            rdq.getDateHeure(),
            rdq.getManager().getFullName()
        );
        
        // Log pour l'instant
        System.out.println("Notification envoyée à " + collaborateurs.size() + " collaborateurs: " + message);
        
        for (Collaborateur collaborateur : collaborateurs) {
            System.out.println("  - " + collaborateur.getFullName() + " (" + collaborateur.getEmail() + ")");
        }
    }

    /**
     * Récupère un RDQ par ID avec mapping vers DTO
     */
    public Optional<RdqResponse> findRdqById(Long id) {
        return findByIdWithAllRelations(id)
            .map(this::mapToRdqResponse);
    }

    /**
     * Récupère tous les RDQ d'un manager avec mapping vers DTO
     */
    public List<RdqResponse> findRdqsByManagerId(Long managerId) {
        return findByManagerId(managerId).stream()
            .map(this::mapToRdqResponse)
            .collect(Collectors.toList());
    }

    /**
     * Récupère les RDQ assignés à un collaborateur
     */
    public List<RdqResponse> findAssignmentsByCollaborateurId(Long collaborateurId) {
        return rdqRepository.findByCollaborateurId(collaborateurId).stream()
            .map(this::mapToRdqResponse)
            .collect(Collectors.toList());
    }

    /**
     * Récupère les RDQ assignés à un collaborateur avec filtrage par statut
     */
    public List<RdqResponse> findAssignmentsByCollaborateurIdAndStatut(Long collaborateurId, RDQ.StatutRDQ statut) {
        List<RDQ> rdqs = rdqRepository.findByCollaborateurId(collaborateurId);
        
        if (statut != null) {
            rdqs = rdqs.stream()
                .filter(rdq -> rdq.getStatut() == statut)
                .collect(Collectors.toList());
        }
        
        return rdqs.stream()
            .map(this::mapToRdqResponse)
            .collect(Collectors.toList());
    }

    /**
     * Récupère les RDQ assignés à un collaborateur avec option d'inclure l'historique
     */
    public List<RdqResponse> findAssignmentsByCollaborateurId(Long collaborateurId, boolean includeHistory) {
        List<RDQ> rdqs = rdqRepository.findByCollaborateurId(collaborateurId);
        
        if (!includeHistory) {
            // Filtre pour exclure les RDQ terminés et annulés
            rdqs = rdqs.stream()
                .filter(rdq -> rdq.getStatut() != RDQ.StatutRDQ.TERMINE && 
                               rdq.getStatut() != RDQ.StatutRDQ.ANNULE)
                .collect(Collectors.toList());
        }
        
        return rdqs.stream()
            .map(this::mapToRdqResponse)
            .collect(Collectors.toList());
    }
}