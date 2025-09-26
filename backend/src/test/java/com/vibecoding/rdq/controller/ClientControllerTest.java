package com.vibecoding.rdq.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.vibecoding.rdq.entity.Client;
import com.vibecoding.rdq.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour ClientController - TM-46
 * Tests des endpoints REST avec MockMvc et Spring Boot Test
 */
@WebMvcTest(controllers = ClientController.class)
@TestPropertySource(properties = {
    "spring.profiles.active=test",
    "jwt.secret=test-secret-key-for-unit-tests-only",
    "jwt.expiration=86400000"
})
@DisplayName("ClientController - Tests unitaires")
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;
    
    @MockBean
    private com.vibecoding.rdq.security.JwtService jwtService;
    
    @MockBean
    private com.vibecoding.rdq.service.UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Faker faker = new Faker();
    private Client testClient;
    private List<Client> testClients;

    @BeforeEach
    void setUp() {
        testClient = createTestClient(1L);
        testClients = Arrays.asList(
                testClient,
                createTestClient(2L),
                createTestClient(3L)
        );
    }

    private Client createTestClient(Long id) {
        return new Client(
                id,
                faker.company().name(),
                faker.name().fullName(),
                faker.phoneNumber().phoneNumber(),
                faker.internet().emailAddress()
        );
    }

    @Nested
    @DisplayName("Tests GET /api/clients")
    class GetAllClientsTest {

        @Test
        @WithMockUser
        @DisplayName("getAllClients() - Doit retourner la liste de tous les clients")
        void getAllClients_ShouldReturnAllClients() throws Exception {
            // Given
            when(clientService.findAll()).thenReturn(testClients);

            // When & Then
            mockMvc.perform(get("/api/clients"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size()").value(3))
                    .andExpect(jsonPath("$[0].idClient").value(testClient.getIdClient()))
                    .andExpect(jsonPath("$[0].nom").value(testClient.getNom()));

            verify(clientService).findAll();
        }

        @Test
        @WithMockUser
        @DisplayName("getAllClients() - Doit retourner une liste vide quand aucun client")
        void getAllClients_WithNoClients_ShouldReturnEmptyList() throws Exception {
            // Given
            when(clientService.findAll()).thenReturn(Arrays.asList());

            // When & Then
            mockMvc.perform(get("/api/clients"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.size()").value(0));

            verify(clientService).findAll();
        }
    }

    @Nested
    @DisplayName("Tests GET /api/clients/{id}")
    class GetClientByIdTest {

        @Test
        @WithMockUser
        @DisplayName("getClientById() - Doit retourner le client avec l'ID donné")
        void getClientById_WithValidId_ShouldReturnClient() throws Exception {
            // Given
            Long clientId = 1L;
            when(clientService.findById(clientId)).thenReturn(Optional.of(testClient));

            // When & Then
            mockMvc.perform(get("/api/clients/{id}", clientId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.idClient").value(testClient.getIdClient()))
                    .andExpect(jsonPath("$.nom").value(testClient.getNom()));

            verify(clientService).findById(clientId);
        }

        @Test
        @WithMockUser
        @DisplayName("getClientById() - Doit retourner 404 pour un ID inexistant")
        void getClientById_WithInvalidId_ShouldReturn404() throws Exception {
            // Given
            Long invalidId = 999L;
            when(clientService.findById(invalidId)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/clients/{id}", invalidId))
                    .andExpect(status().isNotFound());

            verify(clientService).findById(invalidId);
        }
    }
}