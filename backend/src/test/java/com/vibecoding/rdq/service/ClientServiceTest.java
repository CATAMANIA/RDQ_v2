package com.vibecoding.rdq.service;

import com.github.javafaker.Faker;
import com.vibecoding.rdq.entity.Client;
import com.vibecoding.rdq.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour ClientService - TM-46
 * Couverture complète des méthodes avec Mockito et AssertJ
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService - Tests unitaires")
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private final Faker faker = new Faker();
    private Client testClient;
    private Client acmeClient;
    private Client techSolutionsClient;

    @BeforeEach
    void setUp() {
        testClient = createTestClient(1L);
        acmeClient = new Client(10L, "ACME Corp", "M. Durand", "01.55.66.77.88", "durand@acme.com");
        techSolutionsClient = new Client(20L, "TechSolutions", "Mme Leblanc", "01.44.55.66.77", "leblanc@techsolutions.com");
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
    @DisplayName("Tests CRUD basiques")
    class CrudOperationsTest {

        @Test
        @DisplayName("findAll() - Doit retourner tous les clients")
        void findAll_ShouldReturnAllClients() {
            // Given
            List<Client> expectedClients = Arrays.asList(acmeClient, techSolutionsClient, testClient);
            when(clientRepository.findAll()).thenReturn(expectedClients);

            // When
            List<Client> actualClients = clientService.findAll();

            // Then
            assertThat(actualClients)
                    .isNotNull()
                    .hasSize(3)
                    .containsExactlyElementsOf(expectedClients);
            verify(clientRepository).findAll();
        }

        @Test
        @DisplayName("findById() - Doit retourner le client avec l'ID donné")
        void findById_WithValidId_ShouldReturnClient() {
            // Given
            Long clientId = 1L;
            when(clientRepository.findById(clientId)).thenReturn(Optional.of(testClient));

            // When
            Optional<Client> result = clientService.findById(clientId);

            // Then
            assertThat(result)
                    .isPresent()
                    .contains(testClient);
            verify(clientRepository).findById(clientId);
        }

        @Test
        @DisplayName("findById() - Doit retourner Optional.empty() pour un ID inexistant")
        void findById_WithInvalidId_ShouldReturnEmpty() {
            // Given
            Long invalidId = 999L;
            when(clientRepository.findById(invalidId)).thenReturn(Optional.empty());

            // When
            Optional<Client> result = clientService.findById(invalidId);

            // Then
            assertThat(result).isEmpty();
            verify(clientRepository).findById(invalidId);
        }

        @Test
        @DisplayName("save() - Doit sauvegarder et retourner le client")
        void save_ShouldSaveAndReturnClient() {
            // Given
            Client clientToSave = createTestClient(null);
            Client savedClient = createTestClient(1L);
            when(clientRepository.save(clientToSave)).thenReturn(savedClient);

            // When
            Client result = clientService.save(clientToSave);

            // Then
            assertThat(result)
                    .isNotNull()
                    .isEqualTo(savedClient);
            verify(clientRepository).save(clientToSave);
        }

        @Test
        @DisplayName("deleteById() - Doit supprimer le client avec l'ID donné")
        void deleteById_ShouldDeleteClient() {
            // Given
            Long clientId = 1L;

            // When
            clientService.deleteById(clientId);

            // Then
            verify(clientRepository).deleteById(clientId);
        }
    }

    @Nested
    @DisplayName("Tests de recherche par critères")
    class SearchOperationsTest {

        @Test
        @DisplayName("findByNomContaining() - Doit retourner les clients dont le nom contient le texte")
        void findByNomContaining_WithValidText_ShouldReturnMatchingClients() {
            // Given
            String searchText = "Tech";
            List<Client> expectedClients = Arrays.asList(techSolutionsClient);
            when(clientRepository.findByNomContainingIgnoreCase(searchText)).thenReturn(expectedClients);

            // When
            List<Client> result = clientService.findByNomContaining(searchText);

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasSize(1)
                    .containsExactlyElementsOf(expectedClients)
                    .allMatch(client -> client.getNom().toLowerCase().contains(searchText.toLowerCase()));
            verify(clientRepository).findByNomContainingIgnoreCase(searchText);
        }

        @Test
        @DisplayName("findByNomContaining() - Doit retourner une liste vide pour un texte non trouvé")
        void findByNomContaining_WithInvalidText_ShouldReturnEmptyList() {
            // Given
            String invalidText = "NotFound";
            when(clientRepository.findByNomContainingIgnoreCase(invalidText)).thenReturn(Arrays.asList());

            // When
            List<Client> result = clientService.findByNomContaining(invalidText);

            // Then
            assertThat(result)
                    .isNotNull()
                    .isEmpty();
            verify(clientRepository).findByNomContainingIgnoreCase(invalidText);
        }

        @Test
        @DisplayName("findByNomContaining() - Doit gérer la recherche case insensitive")
        void findByNomContaining_ShouldBeCaseInsensitive() {
            // Given
            String searchText = "acme";
            List<Client> expectedClients = Arrays.asList(acmeClient);
            when(clientRepository.findByNomContainingIgnoreCase(searchText)).thenReturn(expectedClients);

            // When
            List<Client> result = clientService.findByNomContaining(searchText);

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasSize(1)
                    .containsExactlyElementsOf(expectedClients);
            verify(clientRepository).findByNomContainingIgnoreCase(searchText);
        }
    }



    @Nested
    @DisplayName("Tests de validation des paramètres")
    class ValidationTest {

        @Test
        @DisplayName("findByNomContaining() - Doit gérer les chaînes null")
        void findByNomContaining_WithNullString_ShouldCallRepository() {
            // Given
            when(clientRepository.findByNomContainingIgnoreCase(null)).thenReturn(Arrays.asList());

            // When
            List<Client> result = clientService.findByNomContaining(null);

            // Then
            assertThat(result).isNotNull().isEmpty();
            verify(clientRepository).findByNomContainingIgnoreCase(null);
        }

        @Test
        @DisplayName("findByNomContaining() - Doit gérer les chaînes vides")
        void findByNomContaining_WithEmptyString_ShouldReturnAllClients() {
            // Given
            String emptyString = "";
            List<Client> allClients = Arrays.asList(acmeClient, techSolutionsClient);
            when(clientRepository.findByNomContainingIgnoreCase(emptyString)).thenReturn(allClients);

            // When
            List<Client> result = clientService.findByNomContaining(emptyString);

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasSize(2)
                    .containsExactlyElementsOf(allClients);
            verify(clientRepository).findByNomContainingIgnoreCase(emptyString);
        }

        @Test
        @DisplayName("save() - Doit gérer les clients null")
        void save_WithNullClient_ShouldCallRepository() {
            // Given
            when(clientRepository.save(null)).thenReturn(null);

            // When
            Client result = clientService.save(null);

            // Then
            assertThat(result).isNull();
            verify(clientRepository).save(null);
        }
    }

    @Nested
    @DisplayName("Tests des cas limites")
    class EdgeCasesTest {

        @Test
        @DisplayName("findByNomContaining() - Doit gérer les caractères spéciaux")
        void findByNomContaining_WithSpecialCharacters_ShouldCallRepository() {
            // Given
            String specialChars = "Café & Co";
            when(clientRepository.findByNomContainingIgnoreCase(specialChars)).thenReturn(Arrays.asList());

            // When
            List<Client> result = clientService.findByNomContaining(specialChars);

            // Then
            assertThat(result).isNotNull().isEmpty();
            verify(clientRepository).findByNomContainingIgnoreCase(specialChars);
        }

        @Test
        @DisplayName("findByNomContaining() - Doit gérer les espaces")
        void findByNomContaining_WithSpaces_ShouldCallRepository() {
            // Given
            String textWithSpaces = "  ACME  ";
            when(clientRepository.findByNomContainingIgnoreCase(textWithSpaces)).thenReturn(Arrays.asList());

            // When
            List<Client> result = clientService.findByNomContaining(textWithSpaces);

            // Then
            assertThat(result).isNotNull();
            verify(clientRepository).findByNomContainingIgnoreCase(textWithSpaces);
        }
    }
}