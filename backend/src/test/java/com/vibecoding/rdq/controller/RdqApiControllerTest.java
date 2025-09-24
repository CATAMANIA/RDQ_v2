package com.vibecoding.rdq.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vibecoding.rdq.dto.CreateRdqRequest;
import com.vibecoding.rdq.dto.RdqResponse;
import com.vibecoding.rdq.entity.Manager;
import com.vibecoding.rdq.entity.RDQ;
import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.enums.Role;
import com.vibecoding.rdq.service.RDQService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RdqApiController.class)
class RdqApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RDQService rdqService;

    private ObjectMapper objectMapper;
    private User managerUser;
    private User collaborateurUser;
    private CreateRdqRequest validRequest;
    private RdqResponse mockResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Setup test users
        Manager manager = new Manager("Dupont", "Jean", "jean.dupont@test.com", "0123456789");
        manager.setIdManager(1L);

        managerUser = new User();
        managerUser.setUsername("manager1");
        managerUser.setEmail("jean.dupont@test.com");
        managerUser.setRole(Role.MANAGER);
        managerUser.setManager(manager);

        collaborateurUser = new User();
        collaborateurUser.setUsername("collab1");
        collaborateurUser.setEmail("collab@test.com");
        collaborateurUser.setRole(Role.COLLABORATEUR);

        // Setup valid request
        validRequest = new CreateRdqRequest();
        validRequest.setTitre("Test RDQ");
        validRequest.setDateHeure(LocalDateTime.now().plusDays(1));
        validRequest.setAdresse("123 Test Street");
        validRequest.setMode(RDQ.ModeRDQ.PRESENTIEL);
        validRequest.setDescription("Test description");
        validRequest.setProjetId(1L);
        validRequest.setCollaborateurIds(Arrays.asList(1L, 2L));

        // Setup mock response
        mockResponse = new RdqResponse();
        mockResponse.setIdRdq(1L);
        mockResponse.setTitre("Test RDQ");
        mockResponse.setMode(RDQ.ModeRDQ.PRESENTIEL);
        mockResponse.setStatut(RDQ.StatutRDQ.PLANIFIE);
    }

    @Test
    void createRdq_WithValidManagerUser_ShouldReturnCreated() throws Exception {
        // Given
        when(rdqService.createRdq(any(CreateRdqRequest.class), anyLong()))
            .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/rdq")
                .with(user(managerUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("RDQ créé avec succès"))
                .andExpect(jsonPath("$.data.idRdq").value(1))
                .andExpect(jsonPath("$.data.titre").value("Test RDQ"));
    }

    @Test
    void createRdq_WithCollaborateurUser_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/rdq")
                .with(user(collaborateurUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createRdq_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/rdq")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createRdq_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        CreateRdqRequest invalidRequest = new CreateRdqRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/api/v1/rdq")
                .with(user(managerUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Erreurs de validation"));
    }

    @Test
    void createRdq_WithServiceException_ShouldReturnBadRequest() throws Exception {
        // Given
        when(rdqService.createRdq(any(CreateRdqRequest.class), anyLong()))
            .thenThrow(new RuntimeException("Projet non trouvé"));

        // When & Then
        mockMvc.perform(post("/api/v1/rdq")
                .with(user(managerUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Projet non trouvé"));
    }

    @Test
    void getRdqById_WithValidId_ShouldReturnOk() throws Exception {
        // Given
        when(rdqService.findRdqById(1L)).thenReturn(Optional.of(mockResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/rdq/1")
                .with(user(managerUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.idRdq").value(1))
                .andExpect(jsonPath("$.data.titre").value("Test RDQ"));
    }

    @Test
    void getRdqById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(rdqService.findRdqById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/rdq/999")
                .with(user(managerUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("RDQ non trouvé avec l'ID: 999"));
    }

    @Test
    void getMyRdqs_WithManagerUser_ShouldReturnOk() throws Exception {
        // Given
        when(rdqService.findRdqsByManagerId(1L))
            .thenReturn(Arrays.asList(mockResponse));

        // When & Then
        mockMvc.perform(get("/api/v1/rdq/my-rdqs")
                .with(user(managerUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.count").value(1));
    }

    @Test
    void getMyRdqs_WithCollaborateurUser_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/rdq/my-rdqs")
                .with(user(collaborateurUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyAssignments_WithCollaborateurUser_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/rdq/my-assignments")
                .with(user(collaborateurUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getMyAssignments_WithManagerUser_ShouldReturnForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/rdq/my-assignments")
                .with(user(managerUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void healthCheck_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/rdq/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("RDQ API"))
                .andExpect(jsonPath("$.version").value("1.0"));
    }
}