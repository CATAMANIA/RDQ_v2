package com.vibecoding.rdq.service;

import com.vibecoding.rdq.dto.CreateRdqRequest;
import com.vibecoding.rdq.dto.RdqResponse;
import com.vibecoding.rdq.entity.*;
import com.vibecoding.rdq.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RdqServiceCreateTest {

    @Mock
    private RDQRepository rdqRepository;

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private CollaborateurRepository collaborateurRepository;

    @Mock
    private ProjetRepository projetRepository;

    @InjectMocks
    private RDQService rdqService;

    private Manager testManager;
    private Projet testProjet;
    private Collaborateur testCollaborateur1;
    private Collaborateur testCollaborateur2;
    private Client testClient;
    private CreateRdqRequest validRequest;

    @BeforeEach
    void setUp() {
        // Setup test data
        testClient = new Client("Test Client", "contact@test.com");
        testClient.setIdClient(1L);

        testProjet = new Projet("Test Projet", testClient);
        testProjet.setIdProjet(1L);

        testManager = new Manager("Dupont", "Jean", "jean.dupont@test.com", "0123456789");
        testManager.setIdManager(1L);

        testCollaborateur1 = new Collaborateur("Martin", "Marie", "marie.martin@test.com", "0123456790");
        testCollaborateur1.setIdCollaborateur(1L);

        testCollaborateur2 = new Collaborateur("Durand", "Pierre", "pierre.durand@test.com", "0123456791");
        testCollaborateur2.setIdCollaborateur(2L);

        // Valid request
        validRequest = new CreateRdqRequest();
        validRequest.setTitre("Test RDQ");
        validRequest.setDateHeure(LocalDateTime.now().plusDays(1));
        validRequest.setAdresse("123 Test Street");
        validRequest.setMode(RDQ.ModeRDQ.PRESENTIEL);
        validRequest.setDescription("Test description");
        validRequest.setProjetId(1L);
        validRequest.setCollaborateurIds(Arrays.asList(1L, 2L));
        validRequest.setIndications("Test indications");
    }

    @Test
    void createRdq_WithValidData_ShouldCreateRdqSuccessfully() {
        // Given
        when(managerRepository.findById(1L)).thenReturn(Optional.of(testManager));
        when(projetRepository.findById(1L)).thenReturn(Optional.of(testProjet));
        when(collaborateurRepository.findById(1L)).thenReturn(Optional.of(testCollaborateur1));
        when(collaborateurRepository.findById(2L)).thenReturn(Optional.of(testCollaborateur2));
        
        RDQ savedRdq = new RDQ();
        savedRdq.setIdRdq(1L);
        savedRdq.setTitre("Test RDQ");
        savedRdq.setDateHeure(validRequest.getDateHeure());
        savedRdq.setAdresse("123 Test Street");
        savedRdq.setMode(RDQ.ModeRDQ.PRESENTIEL);
        savedRdq.setStatut(RDQ.StatutRDQ.PLANIFIE);
        savedRdq.setDescription("Test description");
        savedRdq.setManager(testManager);
        savedRdq.setProjet(testProjet);
        
        when(rdqRepository.save(any(RDQ.class))).thenReturn(savedRdq);

        // When
        RdqResponse result = rdqService.createRdq(validRequest, 1L);

        // Then
        assertNotNull(result);
        assertEquals("Test RDQ", result.getTitre());
        assertEquals(RDQ.ModeRDQ.PRESENTIEL, result.getMode());
        assertEquals(RDQ.StatutRDQ.PLANIFIE, result.getStatut());
        assertNotNull(result.getManager());
        assertEquals("Jean Dupont", result.getManager().getNom());
        assertNotNull(result.getProjet());
        assertEquals("Test Projet", result.getProjet().getNom());

        // Verify interactions
        verify(managerRepository).findById(1L);
        verify(projetRepository).findById(1L);
        verify(collaborateurRepository).findById(1L);
        verify(collaborateurRepository).findById(2L);
        verify(rdqRepository).save(any(RDQ.class));
    }

    @Test
    void createRdq_WithInvalidManager_ShouldThrowException() {
        // Given
        when(managerRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> rdqService.createRdq(validRequest, 1L));
        
        assertEquals("Manager non trouvé avec l'ID: 1", exception.getMessage());
        verify(managerRepository).findById(1L);
        verifyNoInteractions(rdqRepository);
    }

    @Test
    void createRdq_WithInvalidProjet_ShouldThrowException() {
        // Given
        when(managerRepository.findById(1L)).thenReturn(Optional.of(testManager));
        when(projetRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> rdqService.createRdq(validRequest, 1L));
        
        assertEquals("Projet non trouvé avec l'ID: 1", exception.getMessage());
        verify(projetRepository).findById(1L);
        verifyNoInteractions(rdqRepository);
    }

    @Test
    void createRdq_WithInvalidCollaborateur_ShouldThrowException() {
        // Given
        when(managerRepository.findById(1L)).thenReturn(Optional.of(testManager));
        when(projetRepository.findById(1L)).thenReturn(Optional.of(testProjet));
        when(collaborateurRepository.findById(1L)).thenReturn(Optional.of(testCollaborateur1));
        when(collaborateurRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> rdqService.createRdq(validRequest, 1L));
        
        assertEquals("Collaborateur non trouvé avec l'ID: 2", exception.getMessage());
        verify(collaborateurRepository).findById(2L);
        verifyNoInteractions(rdqRepository);
    }

    @Test
    void createRdq_WithPastDate_ShouldThrowException() {
        // Given
        validRequest.setDateHeure(LocalDateTime.now().minusDays(1)); // Date dans le passé
        when(managerRepository.findById(1L)).thenReturn(Optional.of(testManager));
        when(projetRepository.findById(1L)).thenReturn(Optional.of(testProjet));
        when(collaborateurRepository.findById(1L)).thenReturn(Optional.of(testCollaborateur1));
        when(collaborateurRepository.findById(2L)).thenReturn(Optional.of(testCollaborateur2));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> rdqService.createRdq(validRequest, 1L));
        
        assertEquals("La date du RDQ doit être dans le futur", exception.getMessage());
        verifyNoInteractions(rdqRepository);
    }

    @Test
    void createRdq_WithEmptyCollaborateurs_ShouldThrowException() {
        // Given
        validRequest.setCollaborateurIds(List.of()); // Liste vide
        when(managerRepository.findById(1L)).thenReturn(Optional.of(testManager));
        when(projetRepository.findById(1L)).thenReturn(Optional.of(testProjet));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> rdqService.createRdq(validRequest, 1L));
        
        assertEquals("Au moins un collaborateur doit être assigné au RDQ", exception.getMessage());
        verifyNoInteractions(rdqRepository);
    }

    @Test
    void findRdqById_WithValidId_ShouldReturnRdqResponse() {
        // Given
        RDQ rdq = new RDQ();
        rdq.setIdRdq(1L);
        rdq.setTitre("Test RDQ");
        rdq.setManager(testManager);
        rdq.setProjet(testProjet);
        
        when(rdqRepository.findByIdWithAllRelations(1L)).thenReturn(Optional.of(rdq));

        // When
        Optional<RdqResponse> result = rdqService.findRdqById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test RDQ", result.get().getTitre());
        verify(rdqRepository).findByIdWithAllRelations(1L);
    }

    @Test
    void findRdqById_WithInvalidId_ShouldReturnEmpty() {
        // Given
        when(rdqRepository.findByIdWithAllRelations(1L)).thenReturn(Optional.empty());

        // When
        Optional<RdqResponse> result = rdqService.findRdqById(1L);

        // Then
        assertFalse(result.isPresent());
        verify(rdqRepository).findByIdWithAllRelations(1L);
    }

    @Test
    void findRdqsByManagerId_ShouldReturnListOfRdqResponses() {
        // Given
        RDQ rdq1 = new RDQ();
        rdq1.setIdRdq(1L);
        rdq1.setTitre("RDQ 1");
        rdq1.setManager(testManager);
        rdq1.setProjet(testProjet);

        RDQ rdq2 = new RDQ();
        rdq2.setIdRdq(2L);
        rdq2.setTitre("RDQ 2");
        rdq2.setManager(testManager);
        rdq2.setProjet(testProjet);

        when(rdqRepository.findByManagerIdManager(1L)).thenReturn(Arrays.asList(rdq1, rdq2));

        // When
        List<RdqResponse> result = rdqService.findRdqsByManagerId(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("RDQ 1", result.get(0).getTitre());
        assertEquals("RDQ 2", result.get(1).getTitre());
        verify(rdqRepository).findByManagerIdManager(1L);
    }
}