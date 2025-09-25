package com.vibecoding.rdq.controller;package com.vibecoding.rdq.controller;package com.vibecoding.rdq.controller;package com.vibecoding.rdq.controller;package com.vibecoding.rdq.controller;package com.vibecoding.rdq.controller;package com.vibecoding.rdq.controller;



import com.fasterxml.jackson.databind.ObjectMapper;  

import com.github.javafaker.Faker;

import com.vibecoding.rdq.entity.Client;import com.fasterxml.jackson.databind.ObjectMapper;

import com.vibecoding.rdq.service.ClientService;

import org.junit.jupiter.api.BeforeEach;import com.github.javafaker.Faker;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;import com.vibecoding.rdq.entity.Client;import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;import com.vibecoding.rdq.service.ClientService;

import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;import org.junit.jupiter.api.BeforeEach;import com.vibecoding.rdq.entity.Client;

import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;import org.junit.jupiter.api.DisplayName;



import java.util.Arrays;import org.junit.jupiter.api.Test;import com.vibecoding.rdq.service.ClientService;import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.hasSize;

import static org.hamcrest.Matchers.is;import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;import org.junit.jupiter.api.BeforeEach;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;import org.springframework.http.MediaType;import org.junit.jupiter.api.DisplayName;import com.github.javafaker.Faker;



@WebMvcTest(ClientController.class)import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")

@DisplayName("ClientController - Tests Unitaires")import org.springframework.test.web.servlet.MockMvc;import org.junit.jupiter.api.Test;

class ClientControllerTest {



    @Autowired

    private MockMvc mockMvc;import java.util.Arrays;import org.springframework.beans.factory.annotation.Autowired;import com.vibecoding.rdq.entity.Client;import com.fasterxml.jackson.databind.ObjectMapper;



    @Autowiredimport java.util.List;

    private ObjectMapper objectMapper;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

    @MockBean

    private ClientService clientService;import static org.hamcrest.Matchers.hasSize;



    private final Faker faker = new Faker();import static org.hamcrest.Matchers.is;import org.springframework.boot.test.mock.mockito.MockBean;import com.vibecoding.rdq.service.ClientService;

    private Client testClient;

    private List<Client> testClients;import static org.mockito.ArgumentMatchers.any;



    @BeforeEachimport static org.mockito.ArgumentMatchers.anyLong;import org.springframework.http.MediaType;

    void setUp() {

        testClient = createTestClient(1L);import static org.mockito.Mockito.when;

        testClients = Arrays.asList(

                testClient,import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;import org.springframework.test.context.ActiveProfiles;import org.junit.jupiter.api.BeforeEach;import com.github.javafaker.Faker;

                createTestClient(2L),

                createTestClient(3L)import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

        );

    }import org.springframework.test.web.servlet.MockMvc;



    @Test/**

    @DisplayName("GET /api/clients - Doit retourner la liste des clients")

    void getAllClients_ShouldReturnClientsList() throws Exception { * Tests unitaires pour ClientControllerimport org.junit.jupiter.api.DisplayName;

        when(clientService.findAll()).thenReturn(testClients);

 * Teste les endpoints REST du contrôleur des clients

        mockMvc.perform(get("/api/clients"))

                .andExpect(status().isOk()) */import java.util.Arrays;

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$", hasSize(3)))@WebMvcTest(ClientController.class)

                .andExpect(jsonPath("$[0].id", is(1)))

                .andExpect(jsonPath("$[0].nom", is(testClient.getNom())))@ActiveProfiles("test")import java.util.List;import org.junit.jupiter.api.Test;import com.vibecoding.rdq.entity.Client;import com.fasterxml.jackson.databind.ObjectMapper;import com.fasterxml.jackson.databind.ObjectMapper;

                .andExpect(jsonPath("$[0].contact", is(testClient.getContact())));

    }@DisplayName("ClientController - Tests Unitaires")



    @Testclass ClientControllerTest {import java.util.Optional;

    @DisplayName("GET /api/clients/{id} - Doit retourner un client par ID")

    void getClientById_WithValidId_ShouldReturnClient() throws Exception {

        when(clientService.findById(1L)).thenReturn(testClient);

    @Autowiredimport org.springframework.beans.factory.annotation.Autowired;

        mockMvc.perform(get("/api/clients/1"))

                .andExpect(status().isOk())    private MockMvc mockMvc;

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.id", is(1)))import static org.hamcrest.Matchers.*;

                .andExpect(jsonPath("$.nom", is(testClient.getNom())))

                .andExpect(jsonPath("$.contact", is(testClient.getContact())));    @Autowired

    }

    private ObjectMapper objectMapper;import static org.mockito.ArgumentMatchers.any;import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;import com.vibecoding.rdq.service.ClientService;

    private Client createTestClient(Long id) {

        Client client = new Client();

        if (id != null) {

            client.setId(id);    @MockBeanimport static org.mockito.Mockito.*;

        }

        client.setNom(faker.company().name());    private ClientService clientService;

        client.setContact(faker.name().fullName());

        return client;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;import org.springframework.boot.test.mock.mockito.MockBean;

    }

}    private final Faker faker = new Faker();

    private Client testClient;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

    private List<Client> testClients;

import org.springframework.http.MediaType;import org.junit.jupiter.api.BeforeEach;import com.github.javafaker.Faker;import com.github.javafaker.Faker;

    @BeforeEach

    void setUp() {/**

        testClient = createTestClient(1L);

        testClients = Arrays.asList( * Tests unitaires pour ClientController - TM-46import org.springframework.test.context.ActiveProfiles;

                testClient,

                createTestClient(2L), */

                createTestClient(3L)

        );@WebMvcTest(ClientController.class)import org.springframework.test.web.servlet.MockMvc;import org.junit.jupiter.api.DisplayName;

    }

@ActiveProfiles("test")

    @Test

    @DisplayName("GET /api/clients - Doit retourner la liste des clients")@DisplayName("ClientController - Tests unitaires")

    void getAllClients_ShouldReturnClientsList() throws Exception {

        // Givenclass ClientControllerTest {

        when(clientService.findAll()).thenReturn(testClients);

import java.util.Arrays;import org.junit.jupiter.api.Nested;import com.vibecoding.rdq.entity.Client;import com.vibecoding.rdq.entity.Client;

        // When & Then

        mockMvc.perform(get("/api/clients"))    @Autowired

                .andExpect(status().isOk())

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))    private MockMvc mockMvc;import java.util.List;

                .andExpect(jsonPath("$", hasSize(3)))

                .andExpect(jsonPath("$[0].id", is(1)))

                .andExpect(jsonPath("$[0].nom", is(testClient.getNom())))

                .andExpect(jsonPath("$[0].contact", is(testClient.getContact())));    @MockBeanimport java.util.Optional;import org.junit.jupiter.api.Test;

    }

    private ClientService clientService;

    @Test

    @DisplayName("GET /api/clients/{id} - Doit retourner un client par ID")

    void getClientById_WithValidId_ShouldReturnClient() throws Exception {

        // Given    @Autowired

        when(clientService.findById(1L)).thenReturn(testClient);

    private ObjectMapper objectMapper;import static org.hamcrest.Matchers.*;import org.springframework.beans.factory.annotation.Autowired;import com.vibecoding.rdq.service.ClientService;import com.vibecoding.rdq.service.ClientService;

        // When & Then

        mockMvc.perform(get("/api/clients/1"))

                .andExpect(status().isOk())

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))    private Client testClient;import static org.mockito.ArgumentMatchers.any;

                .andExpect(jsonPath("$.id", is(1)))

                .andExpect(jsonPath("$.nom", is(testClient.getNom())))

                .andExpect(jsonPath("$.contact", is(testClient.getContact())));

    }    @BeforeEachimport static org.mockito.Mockito.*;import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;



    @Test    void setUp() {

    @DisplayName("POST /api/clients - Doit créer un nouveau client")

    void createClient_WithValidData_ShouldReturnCreatedClient() throws Exception {        testClient = new Client("Test Company", "John Doe");import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

        // Given

        Client newClient = createTestClient(null);        testClient.setIdClient(1L);

        Client savedClient = createTestClient(4L);

        when(clientService.save(any(Client.class))).thenReturn(savedClient);    }import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;import org.springframework.boot.test.mock.mockito.MockBean;import org.junit.jupiter.api.BeforeEach;import org.junit.jupiter.api.BeforeEach;



        // When & Then

        mockMvc.perform(post("/api/clients")

                        .contentType(MediaType.APPLICATION_JSON)    @Test

                        .content(objectMapper.writeValueAsString(newClient)))

                .andExpect(status().isCreated())    @DisplayName("GET /api/clients - Doit retourner tous les clients")

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.id", is(4)))    void getAllClients_ShouldReturnAllClients() throws Exception {/**import org.springframework.http.MediaType;

                .andExpect(jsonPath("$.nom", is(savedClient.getNom())))

                .andExpect(jsonPath("$.contact", is(savedClient.getContact())));        // Given

    }

        List<Client> clients = Arrays.asList(testClient); * Tests unitaires pour ClientController - TM-46

    @Test

    @DisplayName("PUT /api/clients/{id} - Doit mettre à jour un client")        when(clientService.findAll()).thenReturn(clients);

    void updateClient_WithValidData_ShouldReturnUpdatedClient() throws Exception {

        // Given */import org.springframework.test.context.ActiveProfiles;import org.junit.jupiter.api.DisplayName;import org.junit.jupiter.api.DisplayName;

        Client updatedClient = createTestClient(1L);

        when(clientService.save(any(Client.class))).thenReturn(updatedClient);        // When & Then



        // When & Then        mockMvc.perform(get("/api/clients")@WebMvcTest(ClientController.class)

        mockMvc.perform(put("/api/clients/1")

                        .contentType(MediaType.APPLICATION_JSON)                        .contentType(MediaType.APPLICATION_JSON))

                        .content(objectMapper.writeValueAsString(updatedClient)))

                .andExpect(status().isOk())                .andExpect(status().isOk())@ActiveProfiles("test")import org.springframework.test.web.servlet.MockMvc;

                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.id", is(1)))                .andExpect(jsonPath("$", hasSize(1)))

                .andExpect(jsonPath("$.nom", is(updatedClient.getNom())))

                .andExpect(jsonPath("$.contact", is(updatedClient.getContact())));                .andExpect(jsonPath("$[0].idClient", is(1)))@DisplayName("ClientController - Tests unitaires")

    }

                .andExpect(jsonPath("$[0].nom", is("Test Company")));

    @Test

    @DisplayName("DELETE /api/clients/{id} - Doit supprimer un client")class ClientControllerTest {import org.junit.jupiter.api.Nested;import org.junit.jupiter.api.Nested;

    void deleteClient_WithValidId_ShouldReturnNoContent() throws Exception {

        // When & Then        verify(clientService).findAll();

        mockMvc.perform(delete("/api/clients/1"))

                .andExpect(status().isNoContent());    }

    }



    /**

     * Crée un client de test avec des données aléatoires    @Test    @Autowiredimport java.util.Arrays;

     */

    private Client createTestClient(Long id) {    @DisplayName("GET /api/clients/{id} - Doit retourner le client")

        Client client = new Client();

        if (id != null) {    void getClientById_ShouldReturnClient() throws Exception {    private MockMvc mockMvc;

            client.setId(id);

        }        // Given

        client.setNom(faker.company().name());

        client.setContact(faker.name().fullName());        when(clientService.findById(1L)).thenReturn(Optional.of(testClient));import java.util.List;import org.junit.jupiter.api.Test;import org.junit.jupiter.api.Test;

        return client;

    }

}
        // When & Then    @MockBean

        mockMvc.perform(get("/api/clients/{id}", 1L))

                .andExpect(status().isOk())    private ClientService clientService;import java.util.Optional;

                .andExpect(jsonPath("$.idClient", is(1)))

                .andExpect(jsonPath("$.nom", is("Test Company")));



        verify(clientService).findById(1L);    @Autowiredimport org.springframework.beans.factory.annotation.Autowired;import org.springframework.beans.factory.annotation.Autowired;

    }

    private ObjectMapper objectMapper;

    @Test

    @DisplayName("POST /api/clients - Doit créer un client")import static org.hamcrest.Matchers.*;

    void createClient_ShouldCreateClient() throws Exception {

        // Given    private final Faker faker = new Faker();

        Client newClient = new Client("New Company", "Jane Smith");

        when(clientService.save(any(Client.class))).thenReturn(testClient);    private Client testClient;import static org.mockito.ArgumentMatchers.any;import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;



        // When & Then

        mockMvc.perform(post("/api/clients")

                        .contentType(MediaType.APPLICATION_JSON)    @BeforeEachimport static org.mockito.Mockito.*;

                        .content(objectMapper.writeValueAsString(newClient)))

                .andExpect(status().isCreated())    void setUp() {

                .andExpect(jsonPath("$.idClient", is(1)))

                .andExpect(jsonPath("$.nom", is("Test Company")));        testClient = new Client(faker.company().name(), faker.name().fullName());import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;import org.springframework.boot.test.mock.mockito.MockBean;import org.springframework.boot.test.mock.mockito.MockBean;



        verify(clientService).save(any(Client.class));        testClient.setIdClient(1L);

    }

}    }import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



    @Testimport org.springframework.http.MediaType;import org.springframework.http.MediaType;

    @DisplayName("GET /api/clients - Doit retourner tous les clients")

    void getAllClients_ShouldReturnAllClients() throws Exception {/**

        // Given

        List<Client> clients = Arrays.asList(testClient); * Tests unitaires pour ClientController - TM-46import org.springframework.test.context.ActiveProfiles;import org.springframework.test.context.ActiveProfiles;

        when(clientService.findAll()).thenReturn(clients);

 * Tests complets avec MockMvc pour tous les endpoints REST

        // When & Then

        mockMvc.perform(get("/api/clients") */import org.springframework.test.web.servlet.MockMvc;import org.springframework.test.web.servlet.MockMvc;

                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())@WebMvcTest(ClientController.class)

                .andExpect(jsonPath("$", hasSize(1)))

                .andExpect(jsonPath("$[0].idClient", is(testClient.getIdClient().intValue())))@ActiveProfiles("test")

                .andExpect(jsonPath("$[0].nom", is(testClient.getNom())));

@DisplayName("ClientController - Tests unitaires")

        verify(clientService).findAll();

    }class ClientControllerTest {import java.util.Arrays;import java.time.LocalDate;



    @Test

    @DisplayName("GET /api/clients/{id} - Doit retourner le client avec l'ID donné")

    void getClientById_WithValidId_ShouldReturnClient() throws Exception {    @Autowiredimport java.util.List;import java.util.Arrays;

        // Given

        when(clientService.findById(1L)).thenReturn(Optional.of(testClient));    private MockMvc mockMvc;



        // When & Thenimport java.util.Optional;import java.util.List;

        mockMvc.perform(get("/api/clients/{id}", 1L)

                        .contentType(MediaType.APPLICATION_JSON))    @MockBean

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.idClient", is(testClient.getIdClient().intValue())))    private ClientService clientService;import java.util.Optional;

                .andExpect(jsonPath("$.nom", is(testClient.getNom())));



        verify(clientService).findById(1L);

    }    @Autowiredimport static org.hamcrest.Matchers.*;



    @Test    private ObjectMapper objectMapper;

    @DisplayName("GET /api/clients/{id} - Doit retourner 404 pour un ID inexistant")

    void getClientById_WithInvalidId_ShouldReturn404() throws Exception {import static org.mockito.ArgumentMatchers.any;import static org.hamcrest.Matchers.*;

        // Given

        when(clientService.findById(999L)).thenReturn(Optional.empty());    private final Faker faker = new Faker();



        // When & Then    private Client testClient;import static org.mockito.Mockito.*;import static org.mockito.ArgumentMatchers.any;

        mockMvc.perform(get("/api/clients/{id}", 999L)

                        .contentType(MediaType.APPLICATION_JSON))    private Client client1;

                .andExpect(status().isNotFound());

    private Client client2;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;import static org.mockito.Mockito.*;

        verify(clientService).findById(999L);

    }



    @Test    @BeforeEachimport static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

    @DisplayName("POST /api/clients - Doit créer un nouveau client")

    void createClient_WithValidData_ShouldReturnCreatedClient() throws Exception {    void setUp() {

        // Given

        Client newClient = new Client("Nouveau Client", "Contact Test");        testClient = createTestClient(1L, "Test");import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

        when(clientService.save(any(Client.class))).thenReturn(testClient);

        client1 = createTestClient(10L, "Client1");

        // When & Then

        mockMvc.perform(post("/api/clients")        client2 = createTestClient(20L, "Client2");/**

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(newClient)))    }

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.idClient", is(testClient.getIdClient().intValue()))) * Tests unitaires pour ClientController - TM-46/**

                .andExpect(jsonPath("$.nom", is(testClient.getNom())));

    private Client createTestClient(Long id, String suffix) {

        verify(clientService).save(any(Client.class));

    }        Client client = new Client( * Tests complets avec MockMvc pour tous les endpoints REST * Tests unitaires pour ClientController - TM-46



    @Test                faker.company().name() + " " + suffix,

    @DisplayName("PUT /api/clients/{id} - Doit mettre à jour le client")

    void updateClient_WithValidData_ShouldReturnUpdatedClient() throws Exception {                faker.name().fullName() */ * Tests complets avec MockMvc pour tous les endpoints REST

        // Given

        when(clientService.findById(1L)).thenReturn(Optional.of(testClient));        );

        when(clientService.save(any(Client.class))).thenReturn(testClient);

        if (id != null) {@WebMvcTest(ClientController.class) */

        // When & Then

        mockMvc.perform(put("/api/clients/{id}", 1L)            client.setIdClient(id);

                        .contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(testClient)))        }@ActiveProfiles("test")@WebMvcTest(ClientController.class)

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.idClient", is(testClient.getIdClient().intValue())))        return client;

                .andExpected(jsonPath("$.nom", is(testClient.getNom())));

    }@DisplayName("ClientController - Tests unitaires")@ActiveProfiles("test")

        verify(clientService).findById(1L);

        verify(clientService).save(any(Client.class));

    }

    @Nestedclass ClientControllerTest {@DisplayName("ClientController - Tests unitaires")

    @Test

    @DisplayName("DELETE /api/clients/{id} - Doit supprimer le client")    @DisplayName("Tests GET /api/clients")

    void deleteClient_WithValidId_ShouldReturnNoContent() throws Exception {

        // Given    class GetAllClientsTest {class ClientControllerTest {

        when(clientService.findById(1L)).thenReturn(Optional.of(testClient));

        doNothing().when(clientService).deleteById(1L);



        // When & Then        @Test    @Autowired

        mockMvc.perform(delete("/api/clients/{id}", 1L))

                .andExpect(status().isNoContent());        @DisplayName("GET /api/clients - Doit retourner tous les clients")



        verify(clientService).findById(1L);        void getAllClients_ShouldReturnAllClients() throws Exception {    private MockMvc mockMvc;    @Autowired

        verify(clientService).deleteById(1L);

    }            // Given

}
            List<Client> clients = Arrays.asList(testClient, client1, client2);    private MockMvc mockMvc;

            when(clientService.findAll()).thenReturn(clients);

    @MockBean

            // When & Then

            mockMvc.perform(get("/api/clients")    private ClientService clientService;    @MockBean

                            .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(status().isOk())    private ClientService clientService;

                    .andExpect(jsonPath("$", hasSize(3)))

                    .andExpect(jsonPath("$[0].idClient", is(testClient.getIdClient().intValue())))    @Autowired

                    .andExpect(jsonPath("$[0].nom", is(testClient.getNom())))

                    .andExpect(jsonPath("$[0].contact", is(testClient.getContact())));    private ObjectMapper objectMapper;    @Autowired



            verify(clientService).findAll();    private ObjectMapper objectMapper;

        }

    private final Faker faker = new Faker();

        @Test

        @DisplayName("GET /api/clients - Doit retourner une liste vide quand aucun client")    private Client testClient;    private final Faker faker = new Faker();

        void getAllClients_WhenEmpty_ShouldReturnEmptyList() throws Exception {

            // Given    private Client client1;    private Client testClient;

            when(clientService.findAll()).thenReturn(Arrays.asList());

    private Client client2;    private Client activeClient;

            // When & Then

            mockMvc.perform(get("/api/clients")    private Client inactiveClient;

                            .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(status().isOk())    @BeforeEach

                    .andExpect(jsonPath("$", hasSize(0)));

    void setUp() {    @BeforeEach

            verify(clientService).findAll();

        }        testClient = createTestClient(1L, "Test");    void setUp() {

    }

        client1 = createTestClient(10L, "Client1");        testClient = createTestClient(1L, true);

    @Nested

    @DisplayName("Tests GET /api/clients/{id}")        client2 = createTestClient(20L, "Client2");        activeClient = createTestClient(10L, true);

    class GetClientByIdTest {

    }        inactiveClient = createTestClient(20L, false);

        @Test

        @DisplayName("GET /api/clients/{id} - Doit retourner le client avec l'ID donné")    }

        void getClientById_WithValidId_ShouldReturnClient() throws Exception {

            // Given    private Client createTestClient(Long id, String suffix) {

            Long clientId = 1L;

            when(clientService.findById(clientId)).thenReturn(Optional.of(testClient));        Client client = new Client(    private Client createTestClient(Long id, String suffix) {



            // When & Then                faker.company().name() + " " + suffix,        Client client = new Client(

            mockMvc.perform(get("/api/clients/{id}", clientId)

                            .contentType(MediaType.APPLICATION_JSON))                faker.name().fullName()                faker.company().name() + suffix,

                    .andExpect(status().isOk())

                    .andExpect(jsonPath("$.idClient", is(testClient.getIdClient().intValue())))        );                faker.name().fullName()

                    .andExpect(jsonPath("$.nom", is(testClient.getNom())))

                    .andExpect(jsonPath("$.contact", is(testClient.getContact())));        if (id != null) {        );



            verify(clientService).findById(clientId);            client.setIdClient(id);        if (id != null) {

        }

        }            client.setIdClient(id);

        @Test

        @DisplayName("GET /api/clients/{id} - Doit retourner 404 pour un ID inexistant")        return client;        }

        void getClientById_WithInvalidId_ShouldReturn404() throws Exception {

            // Given    }        return client;

            Long invalidId = 999L;

            when(clientService.findById(invalidId)).thenReturn(Optional.empty());    }



            // When & Then    @Nested

            mockMvc.perform(get("/api/clients/{id}", invalidId)

                            .contentType(MediaType.APPLICATION_JSON))    @DisplayName("Tests GET /api/clients")    @Nested

                    .andExpect(status().isNotFound());

    class GetAllClientsTest {    @DisplayName("Tests GET /api/clients")

            verify(clientService).findById(invalidId);

        }    class GetAllClientsTest {

    }

        @Test

    @Nested

    @DisplayName("Tests POST /api/clients")        @DisplayName("GET /api/clients - Doit retourner tous les clients")        @Test

    class CreateClientTest {

        void getAllClients_ShouldReturnAllClients() throws Exception {        @DisplayName("GET /api/clients - Doit retourner tous les clients")

        @Test

        @DisplayName("POST /api/clients - Doit créer un nouveau client")            // Given        void getAllClients_ShouldReturnAllClients() throws Exception {

        void createClient_WithValidData_ShouldReturnCreatedClient() throws Exception {

            // Given            List<Client> clients = Arrays.asList(testClient, client1, client2);            // Given

            Client newClient = createTestClient(null, "New");

            Client savedClient = createTestClient(1L, "Saved");            when(clientService.findAll()).thenReturn(clients);            List<Client> clients = Arrays.asList(testClient, activeClient, inactiveClient);

            when(clientService.save(any(Client.class))).thenReturn(savedClient);

            when(clientService.findAll()).thenReturn(clients);

            // When & Then

            mockMvc.perform(post("/api/clients")            // When & Then

                            .contentType(MediaType.APPLICATION_JSON)

                            .content(objectMapper.writeValueAsString(newClient)))            mockMvc.perform(get("/api/clients")            // When & Then

                    .andExpect(status().isCreated())

                    .andExpect(jsonPath("$.idClient", is(savedClient.getIdClient().intValue())))                            .contentType(MediaType.APPLICATION_JSON))            mockMvc.perform(get("/api/clients")

                    .andExpect(jsonPath("$.nom", is(savedClient.getNom())))

                    .andExpect(jsonPath("$.contact", is(savedClient.getContact())));                    .andExpect(status().isOk())                            .contentType(MediaType.APPLICATION_JSON))



            verify(clientService).save(any(Client.class));                    .andExpect(jsonPath("$", hasSize(3)))                    .andExpect(status().isOk())

        }

                    .andExpect(jsonPath("$[0].idClient", is(testClient.getIdClient().intValue())))                    .andExpect(jsonPath("$", hasSize(3)))

        @Test

        @DisplayName("POST /api/clients - Doit retourner une erreur avec des données invalides")                    .andExpect(jsonPath("$[0].nom", is(testClient.getNom())))                    .andExpect(jsonPath("$[0].idClient", is(testClient.getIdClient().intValue())))

        void createClient_WithInvalidData_ShouldReturnBadRequest() throws Exception {

            // Given - Client avec nom null (violation de @NotBlank)                    .andExpect(jsonPath("$[0].contact", is(testClient.getContact())))                    .andExpect(jsonPath("$[0].nom", is(testClient.getNom())))

            Client invalidClient = new Client(null, "contact");

                    .andExpect(jsonPath("$[1].idClient", is(client1.getIdClient().intValue())))                    .andExpect(jsonPath("$[0].active", is(testClient.isActive())))

            // When & Then

            mockMvc.perform(post("/api/clients")                    .andExpect(jsonPath("$[1].nom", is(client1.getNom())))                    .andExpect(jsonPath("$[1].idClient", is(activeClient.getIdClient().intValue())))

                            .contentType(MediaType.APPLICATION_JSON)

                            .content(objectMapper.writeValueAsString(invalidClient)))                    .andExpect(jsonPath("$[2].idClient", is(client2.getIdClient().intValue())))                    .andExpect(jsonPath("$[1].active", is(activeClient.isActive())))

                    .andExpect(status().isBadRequest());

                    .andExpect(jsonPath("$[2].nom", is(client2.getNom())));                    .andExpect(jsonPath("$[2].idClient", is(inactiveClient.getIdClient().intValue())))

            verify(clientService, never()).save(any(Client.class));

        }                    .andExpect(jsonPath("$[2].active", is(inactiveClient.isActive())));

    }

            verify(clientService).findAll();

    @Nested

    @DisplayName("Tests PUT /api/clients/{id}")        }            verify(clientService).findAll();

    class UpdateClientTest {

        }

        @Test

        @DisplayName("PUT /api/clients/{id} - Doit mettre à jour le client")        @Test

        void updateClient_WithValidData_ShouldReturnUpdatedClient() throws Exception {

            // Given        @DisplayName("GET /api/clients - Doit retourner une liste vide quand aucun client")        @Test

            Long clientId = 1L;

            Client updatedClient = createTestClient(clientId, "Updated");        void getAllClients_WhenEmpty_ShouldReturnEmptyList() throws Exception {        @DisplayName("GET /api/clients - Doit retourner une liste vide quand aucun client")

            when(clientService.findById(clientId)).thenReturn(Optional.of(testClient));

            when(clientService.save(any(Client.class))).thenReturn(updatedClient);            // Given        void getAllClients_WhenEmpty_ShouldReturnEmptyList() throws Exception {



            // When & Then            when(clientService.findAll()).thenReturn(Arrays.asList());            // Given

            mockMvc.perform(put("/api/clients/{id}", clientId)

                            .contentType(MediaType.APPLICATION_JSON)            when(clientService.findAll()).thenReturn(Arrays.asList());

                            .content(objectMapper.writeValueAsString(updatedClient)))

                    .andExpect(status().isOk())            // When & Then

                    .andExpect(jsonPath("$.idClient", is(updatedClient.getIdClient().intValue())))

                    .andExpect(jsonPath("$.nom", is(updatedClient.getNom())))            mockMvc.perform(get("/api/clients")            // When & Then

                    .andExpect(jsonPath("$.contact", is(updatedClient.getContact())));

                            .contentType(MediaType.APPLICATION_JSON))            mockMvc.perform(get("/api/clients")

            verify(clientService).findById(clientId);

            verify(clientService).save(any(Client.class));                    .andExpect(status().isOk())                            .contentType(MediaType.APPLICATION_JSON))

        }

                    .andExpect(jsonPath("$", hasSize(0)));                    .andExpect(status().isOk())

        @Test

        @DisplayName("PUT /api/clients/{id} - Doit retourner 404 pour un ID inexistant")                    .andExpect(jsonPath("$", hasSize(0)));

        void updateClient_WithInvalidId_ShouldReturn404() throws Exception {

            // Given            verify(clientService).findAll();

            Long invalidId = 999L;

            Client updateData = createTestClient(invalidId, "Update");        }            verify(clientService).findAll();

            when(clientService.findById(invalidId)).thenReturn(Optional.empty());

    }        }

            // When & Then

            mockMvc.perform(put("/api/clients/{id}", invalidId)    }

                            .contentType(MediaType.APPLICATION_JSON)

                            .content(objectMapper.writeValueAsString(updateData)))    @Nested

                    .andExpect(status().isNotFound());

    @DisplayName("Tests GET /api/clients/{id}")    @Nested

            verify(clientService).findById(invalidId);

            verify(clientService, never()).save(any(Client.class));    class GetClientByIdTest {    @DisplayName("Tests GET /api/clients/active")

        }

    }    class GetActiveClientsTest {



    @Nested        @Test

    @DisplayName("Tests DELETE /api/clients/{id}")  

    class DeleteClientTest {        @DisplayName("GET /api/clients/{id} - Doit retourner le client avec l'ID donné")        @Test



        @Test        void getClientById_WithValidId_ShouldReturnClient() throws Exception {        @DisplayName("GET /api/clients/active - Doit retourner seulement les clients actifs")

        @DisplayName("DELETE /api/clients/{id} - Doit supprimer le client")

        void deleteClient_WithValidId_ShouldReturnNoContent() throws Exception {            // Given        void getActiveClients_ShouldReturnOnlyActiveClients() throws Exception {

            // Given

            Long clientId = 1L;            Long clientId = 1L;            // Given

            when(clientService.findById(clientId)).thenReturn(Optional.of(testClient));

            doNothing().when(clientService).deleteById(clientId);            when(clientService.findById(clientId)).thenReturn(Optional.of(testClient));            List<Client> activeClients = Arrays.asList(testClient, activeClient);



            // When & Then            when(clientService.findByActive(true)).thenReturn(activeClients);

            mockMvc.perform(delete("/api/clients/{id}", clientId))

                    .andExpect(status().isNoContent());            // When & Then



            verify(clientService).findById(clientId);            mockMvc.perform(get("/api/clients/{id}", clientId)            // When & Then

            verify(clientService).deleteById(clientId);

        }                            .contentType(MediaType.APPLICATION_JSON))            mockMvc.perform(get("/api/clients/active")



        @Test                    .andExpect(status().isOk())                            .contentType(MediaType.APPLICATION_JSON))

        @DisplayName("DELETE /api/clients/{id} - Doit retourner 404 pour un ID inexistant")

        void deleteClient_WithInvalidId_ShouldReturn404() throws Exception {                    .andExpect(jsonPath("$.idClient", is(testClient.getIdClient().intValue())))                    .andExpect(status().isOk())

            // Given

            Long invalidId = 999L;                    .andExpect(jsonPath("$.nom", is(testClient.getNom())))                    .andExpect(jsonPath("$", hasSize(2)))

            when(clientService.findById(invalidId)).thenReturn(Optional.empty());

                    .andExpect(jsonPath("$.contact", is(testClient.getContact())));                    .andExpect(jsonPath("$[0].active", is(true)))

            // When & Then

            mockMvc.perform(delete("/api/clients/{id}", invalidId))                    .andExpect(jsonPath("$[1].active", is(true)));

                    .andExpect(status().isNotFound());

            verify(clientService).findById(clientId);

            verify(clientService).findById(invalidId);

            verify(clientService, never()).deleteById(invalidId);        }            verify(clientService).findByActive(true);

        }

    }        }



    @Nested        @Test

    @DisplayName("Tests de gestion des erreurs")

    class ErrorHandlingTest {        @DisplayName("GET /api/clients/{id} - Doit retourner 404 pour un ID inexistant")        @Test



        @Test        void getClientById_WithInvalidId_ShouldReturn404() throws Exception {        @DisplayName("GET /api/clients/active - Doit retourner une liste vide quand aucun client actif")

        @DisplayName("Doit retourner 500 en cas d'erreur interne du service")

        void shouldReturn500OnServiceError() throws Exception {            // Given        void getActiveClients_WhenEmpty_ShouldReturnEmptyList() throws Exception {

            // Given

            when(clientService.findAll()).thenThrow(new RuntimeException("Erreur base de données"));            Long invalidId = 999L;            // Given



            // When & Then            when(clientService.findById(invalidId)).thenReturn(Optional.empty());            when(clientService.findByActive(true)).thenReturn(Arrays.asList());

            mockMvc.perform(get("/api/clients")

                            .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(status().isInternalServerError());

            // When & Then            // When & Then

            verify(clientService).findAll();

        }            mockMvc.perform(get("/api/clients/{id}", invalidId)            mockMvc.perform(get("/api/clients/active")



        @Test                            .contentType(MediaType.APPLICATION_JSON))                            .contentType(MediaType.APPLICATION_JSON))

        @DisplayName("Doit gérer les requêtes avec Content-Type invalide")

        void shouldHandleInvalidContentType() throws Exception {                    .andExpect(status().isNotFound());                    .andExpect(status().isOk())

            // When & Then

            mockMvc.perform(post("/api/clients")                    .andExpect(jsonPath("$", hasSize(0)));

                            .contentType(MediaType.TEXT_PLAIN)

                            .content("invalid content"))            verify(clientService).findById(invalidId);

                    .andExpect(status().isUnsupportedMediaType());

        }            verify(clientService).findByActive(true);

            verify(clientService, never()).save(any(Client.class));

        }    }        }

    }

}    }

    @Nested

    @DisplayName("Tests POST /api/clients")    @Nested

    class CreateClientTest {    @DisplayName("Tests GET /api/clients/{id}")

    class GetClientByIdTest {

        @Test

        @DisplayName("POST /api/clients - Doit créer un nouveau client")        @Test

        void createClient_WithValidData_ShouldReturnCreatedClient() throws Exception {        @DisplayName("GET /api/clients/{id} - Doit retourner le client avec l'ID donné")

            // Given        void getClientById_WithValidId_ShouldReturnClient() throws Exception {

            Client newClient = createTestClient(null, "New");            // Given

            Client savedClient = createTestClient(1L, "Saved");            Long clientId = 1L;

            when(clientService.save(any(Client.class))).thenReturn(savedClient);            when(clientService.findById(clientId)).thenReturn(Optional.of(testClient));



            // When & Then            // When & Then

            mockMvc.perform(post("/api/clients")            mockMvc.perform(get("/api/clients/{id}", clientId)

                            .contentType(MediaType.APPLICATION_JSON)                            .contentType(MediaType.APPLICATION_JSON))

                            .content(objectMapper.writeValueAsString(newClient)))                    .andExpect(status().isOk())

                    .andExpect(status().isCreated())                    .andExpect(jsonPath("$.idClient", is(testClient.getIdClient().intValue())))

                    .andExpect(jsonPath("$.idClient", is(savedClient.getIdClient().intValue())))                    .andExpect(jsonPath("$.nom", is(testClient.getNom())))

                    .andExpect(jsonPath("$.nom", is(savedClient.getNom())))                    .andExpect(jsonPath("$.email", is(testClient.getEmail())))

                    .andExpect(jsonPath("$.contact", is(savedClient.getContact())));                    .andExpect(jsonPath("$.telephone", is(testClient.getTelephone())))

                    .andExpect(jsonPath("$.active", is(testClient.isActive())));

            verify(clientService).save(any(Client.class));

        }            verify(clientService).findById(clientId);

        }

        @Test

        @DisplayName("POST /api/clients - Doit retourner une erreur avec des données invalides")        @Test

        void createClient_WithInvalidData_ShouldReturnBadRequest() throws Exception {        @DisplayName("GET /api/clients/{id} - Doit retourner 404 pour un ID inexistant")

            // Given - Client avec nom null (violation de @NotBlank)        void getClientById_WithInvalidId_ShouldReturn404() throws Exception {

            Client invalidClient = new Client(null, "contact");            // Given

            Long invalidId = 999L;

            // When & Then            when(clientService.findById(invalidId)).thenReturn(Optional.empty());

            mockMvc.perform(post("/api/clients")

                            .contentType(MediaType.APPLICATION_JSON)            // When & Then

                            .content(objectMapper.writeValueAsString(invalidClient)))            mockMvc.perform(get("/api/clients/{id}", invalidId)

                    .andExpect(status().isBadRequest());                            .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(status().isNotFound());

            verify(clientService, never()).save(any(Client.class));

        }            verify(clientService).findById(invalidId);

    }        }

    }

    @Nested

    @DisplayName("Tests PUT /api/clients/{id}")    @Nested

    class UpdateClientTest {    @DisplayName("Tests POST /api/clients")

    class CreateClientTest {

        @Test

        @DisplayName("PUT /api/clients/{id} - Doit mettre à jour le client")        @Test

        void updateClient_WithValidData_ShouldReturnUpdatedClient() throws Exception {        @DisplayName("POST /api/clients - Doit créer un nouveau client")

            // Given        void createClient_WithValidData_ShouldReturnCreatedClient() throws Exception {

            Long clientId = 1L;            // Given

            Client updatedClient = createTestClient(clientId, "Updated");            Client newClient = createTestClient(null, true);

            when(clientService.findById(clientId)).thenReturn(Optional.of(testClient));            Client savedClient = createTestClient(1L, true);

            when(clientService.save(any(Client.class))).thenReturn(updatedClient);            when(clientService.save(any(Client.class))).thenReturn(savedClient);



            // When & Then            // When & Then

            mockMvc.perform(put("/api/clients/{id}", clientId)            mockMvc.perform(post("/api/clients")

                            .contentType(MediaType.APPLICATION_JSON)                            .contentType(MediaType.APPLICATION_JSON)

                            .content(objectMapper.writeValueAsString(updatedClient)))                            .content(objectMapper.writeValueAsString(newClient)))

                    .andExpect(status().isOk())                    .andExpect(status().isCreated())

                    .andExpect(jsonPath("$.idClient", is(updatedClient.getIdClient().intValue())))                    .andExpect(jsonPath("$.idClient", is(savedClient.getIdClient().intValue())))

                    .andExpect(jsonPath("$.nom", is(updatedClient.getNom())))                    .andExpect(jsonPath("$.nom", is(savedClient.getNom())))

                    .andExpect(jsonPath("$.contact", is(updatedClient.getContact())));                    .andExpect(jsonPath("$.email", is(savedClient.getEmail())))

                    .andExpect(jsonPath("$.active", is(savedClient.isActive())));

            verify(clientService).findById(clientId);

            verify(clientService).save(any(Client.class));            verify(clientService).save(any(Client.class));

        }        }



        @Test        @Test

        @DisplayName("PUT /api/clients/{id} - Doit retourner 404 pour un ID inexistant")        @DisplayName("POST /api/clients - Doit retourner une erreur avec des données invalides")

        void updateClient_WithInvalidId_ShouldReturn404() throws Exception {        void createClient_WithInvalidData_ShouldReturnBadRequest() throws Exception {

            // Given            // Given - Client avec données manquantes

            Long invalidId = 999L;            Client invalidClient = new Client();

            Client updateData = createTestClient(invalidId, "Update");

            when(clientService.findById(invalidId)).thenReturn(Optional.empty());            // When & Then

            mockMvc.perform(post("/api/clients")

            // When & Then                            .contentType(MediaType.APPLICATION_JSON)

            mockMvc.perform(put("/api/clients/{id}", invalidId)                            .content(objectMapper.writeValueAsString(invalidClient)))

                            .contentType(MediaType.APPLICATION_JSON)                    .andExpect(status().isBadRequest());

                            .content(objectMapper.writeValueAsString(updateData)))

                    .andExpect(status().isNotFound());            verify(clientService, never()).save(any(Client.class));

        }

            verify(clientService).findById(invalidId);    }

            verify(clientService, never()).save(any(Client.class));

        }    @Nested

    }    @DisplayName("Tests PUT /api/clients/{id}")

    class UpdateClientTest {

    @Nested

    @DisplayName("Tests DELETE /api/clients/{id}")        @Test

    class DeleteClientTest {        @DisplayName("PUT /api/clients/{id} - Doit mettre à jour le client")

        void updateClient_WithValidData_ShouldReturnUpdatedClient() throws Exception {

        @Test            // Given

        @DisplayName("DELETE /api/clients/{id} - Doit supprimer le client")            Long clientId = 1L;

        void deleteClient_WithValidId_ShouldReturnNoContent() throws Exception {            Client updatedClient = createTestClient(clientId, false);

            // Given            when(clientService.findById(clientId)).thenReturn(Optional.of(testClient));

            Long clientId = 1L;            when(clientService.save(any(Client.class))).thenReturn(updatedClient);

            when(clientService.findById(clientId)).thenReturn(Optional.of(testClient));

            doNothing().when(clientService).deleteById(clientId);            // When & Then

            mockMvc.perform(put("/api/clients/{id}", clientId)

            // When & Then                            .contentType(MediaType.APPLICATION_JSON)

            mockMvc.perform(delete("/api/clients/{id}", clientId))                            .content(objectMapper.writeValueAsString(updatedClient)))

                    .andExpect(status().isNoContent());                    .andExpect(status().isOk())

                    .andExpect(jsonPath("$.idClient", is(updatedClient.getIdClient().intValue())))

            verify(clientService).findById(clientId);                    .andExpect(jsonPath("$.nom", is(updatedClient.getNom())))

            verify(clientService).deleteById(clientId);                    .andExpect(jsonPath("$.active", is(updatedClient.isActive())));

        }

            verify(clientService).findById(clientId);

        @Test            verify(clientService).save(any(Client.class));

        @DisplayName("DELETE /api/clients/{id} - Doit retourner 404 pour un ID inexistant")        }

        void deleteClient_WithInvalidId_ShouldReturn404() throws Exception {

            // Given        @Test

            Long invalidId = 999L;        @DisplayName("PUT /api/clients/{id} - Doit retourner 404 pour un ID inexistant")

            when(clientService.findById(invalidId)).thenReturn(Optional.empty());        void updateClient_WithInvalidId_ShouldReturn404() throws Exception {

            // Given

            // When & Then            Long invalidId = 999L;

            mockMvc.perform(delete("/api/clients/{id}", invalidId))            Client updateData = createTestClient(invalidId, true);

                    .andExpect(status().isNotFound());            when(clientService.findById(invalidId)).thenReturn(Optional.empty());



            verify(clientService).findById(invalidId);            // When & Then

            verify(clientService, never()).deleteById(invalidId);            mockMvc.perform(put("/api/clients/{id}", invalidId)

        }                            .contentType(MediaType.APPLICATION_JSON)

    }                            .content(objectMapper.writeValueAsString(updateData)))

                    .andExpect(status().isNotFound());

    @Nested

    @DisplayName("Tests de recherche de clients")            verify(clientService).findById(invalidId);

    class SearchClientsTest {            verify(clientService, never()).save(any(Client.class));

        }

        @Test    }

        @DisplayName("GET /api/clients/search - Doit rechercher les clients par nom")

        void searchClients_ByName_ShouldReturnMatchingClients() throws Exception {    @Nested

            // Given    @DisplayName("Tests DELETE /api/clients/{id}")

            String searchTerm = "Test";    class DeleteClientTest {

            List<Client> matchingClients = Arrays.asList(testClient);

            when(clientService.findByNomContaining(searchTerm)).thenReturn(matchingClients);        @Test

        @DisplayName("DELETE /api/clients/{id} - Doit supprimer le client")

            // When & Then        void deleteClient_WithValidId_ShouldReturnNoContent() throws Exception {

            mockMvc.perform(get("/api/clients/search")            // Given

                            .param("nom", searchTerm)            Long clientId = 1L;

                            .contentType(MediaType.APPLICATION_JSON))            when(clientService.findById(clientId)).thenReturn(Optional.of(testClient));

                    .andExpect(status().isOk())            doNothing().when(clientService).deleteById(clientId);

                    .andExpect(jsonPath("$", hasSize(1)))

                    .andExpect(jsonPath("$[0].idClient", is(testClient.getIdClient().intValue())))            // When & Then

                    .andExpect(jsonPath("$[0].nom", containsString("Test")));            mockMvc.perform(delete("/api/clients/{id}", clientId))

                    .andExpect(status().isNoContent());

            verify(clientService).findByNomContaining(searchTerm);

        }            verify(clientService).findById(clientId);

            verify(clientService).deleteById(clientId);

        @Test        }

        @DisplayName("GET /api/clients/search - Doit retourner une liste vide si aucun résultat")

        void searchClients_NoMatch_ShouldReturnEmptyList() throws Exception {        @Test

            // Given        @DisplayName("DELETE /api/clients/{id} - Doit retourner 404 pour un ID inexistant")

            String searchTerm = "nonexistent";        void deleteClient_WithInvalidId_ShouldReturn404() throws Exception {

            when(clientService.findByNomContaining(searchTerm)).thenReturn(Arrays.asList());            // Given

            Long invalidId = 999L;

            // When & Then            when(clientService.findById(invalidId)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/clients/search")

                            .param("nom", searchTerm)            // When & Then

                            .contentType(MediaType.APPLICATION_JSON))            mockMvc.perform(delete("/api/clients/{id}", invalidId))

                    .andExpect(status().isOk())                    .andExpect(status().isNotFound());

                    .andExpect(jsonPath("$", hasSize(0)));

            verify(clientService).findById(invalidId);

            verify(clientService).findByNomContaining(searchTerm);            verify(clientService, never()).deleteById(invalidId);

        }        }

    }    }



    @Nested    @Nested

    @DisplayName("Tests de gestion des erreurs")    @DisplayName("Tests de recherche de clients")

    class ErrorHandlingTest {    class SearchClientsTest {



        @Test        @Test

        @DisplayName("Doit retourner 500 en cas d'erreur interne du service")        @DisplayName("GET /api/clients/search - Doit rechercher les clients par nom")

        void shouldReturn500OnServiceError() throws Exception {        void searchClients_ByName_ShouldReturnMatchingClients() throws Exception {

            // Given            // Given

            when(clientService.findAll()).thenThrow(new RuntimeException("Erreur base de données"));            String searchTerm = "test";

            List<Client> matchingClients = Arrays.asList(testClient);

            // When & Then            when(clientService.findByNomContaining(searchTerm)).thenReturn(matchingClients);

            mockMvc.perform(get("/api/clients")

                            .contentType(MediaType.APPLICATION_JSON))            // When & Then

                    .andExpect(status().isInternalServerError());            mockMvc.perform(get("/api/clients/search")

                            .param("nom", searchTerm)

            verify(clientService).findAll();                            .contentType(MediaType.APPLICATION_JSON))

        }                    .andExpect(status().isOk())

                    .andExpect(jsonPath("$", hasSize(1)))

        @Test                    .andExpect(jsonPath("$[0].idClient", is(testClient.getIdClient().intValue())))

        @DisplayName("Doit gérer les requêtes avec Content-Type invalide")                    .andExpect(jsonPath("$[0].nom", containsString(testClient.getNom())));

        void shouldHandleInvalidContentType() throws Exception {

            // When & Then            verify(clientService).findByNomContaining(searchTerm);

            mockMvc.perform(post("/api/clients")        }

                            .contentType(MediaType.TEXT_PLAIN)

                            .content("invalid content"))        @Test

                    .andExpect(status().isUnsupportedMediaType());        @DisplayName("GET /api/clients/search - Doit retourner une liste vide si aucun résultat")

        void searchClients_NoMatch_ShouldReturnEmptyList() throws Exception {

            verify(clientService, never()).save(any(Client.class));            // Given

        }            String searchTerm = "nonexistent";

            when(clientService.findByNomContaining(searchTerm)).thenReturn(Arrays.asList());

        @Test

        @DisplayName("Doit gérer les requêtes avec nom trop long")            // When & Then

        void shouldHandleInvalidNameLength() throws Exception {            mockMvc.perform(get("/api/clients/search")

            // Given - Client avec nom trop long (plus de 200 caractères)                            .param("nom", searchTerm)

            String longName = "A".repeat(201);                            .contentType(MediaType.APPLICATION_JSON))

            Client invalidClient = new Client(longName, "contact");                    .andExpect(status().isOk())

                    .andExpect(jsonPath("$", hasSize(0)));

            // When & Then

            mockMvc.perform(post("/api/clients")            verify(clientService).findByNomContaining(searchTerm);

                            .contentType(MediaType.APPLICATION_JSON)        }

                            .content(objectMapper.writeValueAsString(invalidClient)))    }

                    .andExpect(status().isBadRequest());

    @Nested

            verify(clientService, never()).save(any(Client.class));    @DisplayName("Tests de gestion des erreurs")

        }    class ErrorHandlingTest {



        @Test        @Test

        @DisplayName("Doit gérer les paramètres de recherche manquants")        @DisplayName("Doit retourner 500 en cas d'erreur interne du service")

        void shouldHandleMissingSearchParameters() throws Exception {        void shouldReturn500OnServiceError() throws Exception {

            // When & Then            // Given

            mockMvc.perform(get("/api/clients/search")            when(clientService.findAll()).thenThrow(new RuntimeException("Erreur base de données"));

                            .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(status().isBadRequest());            // When & Then

            mockMvc.perform(get("/api/clients")

            verify(clientService, never()).findByNomContaining(any());                            .contentType(MediaType.APPLICATION_JSON))

        }                    .andExpect(status().isInternalServerError());

    }

}            verify(clientService).findAll();
        }

        @Test
        @DisplayName("Doit gérer les requêtes avec Content-Type invalide")
        void shouldHandleInvalidContentType() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/clients")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("invalid content"))
                    .andExpect(status().isUnsupportedMediaType());

            verify(clientService, never()).save(any(Client.class));
        }

        @Test
        @DisplayName("Doit gérer les paramètres de recherche invalides")
        void shouldHandleInvalidSearchParameters() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/clients/search")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());

            verify(clientService, never()).findByNomContaining(any());
        }
    }
}