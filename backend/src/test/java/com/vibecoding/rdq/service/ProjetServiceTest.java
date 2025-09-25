package com.vibecoding.rdq.service;

import com.github.javafaker.Faker;
import com.vibecoding.rdq.entity.Projet;
import com.vibecoding.rdq.repository.ProjetRepository;
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
 * Tests unitaires pour ProjetService - TM-46
 * Couverture complète des méthodes avec Mockito et AssertJ
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProjetService - Tests unitaires")
class ProjetServiceTest {

    @Mock
    private ProjetRepository projetRepository;

    @InjectMocks
    private ProjetService projetService;

    private final Faker faker = new Faker();
    private Projet testProjet;
    private Projet migrationProjet;
    private Projet appelOffreProjet;
    private Projet mobileProjet;

    @BeforeEach
    void setUp() {
        testProjet = createTestProjet(1L);
        migrationProjet = new Projet(10L, "Migration Cloud");
        appelOffreProjet = new Projet(20L, "Appel d'offre AO-2024-001");
        mobileProjet = new Projet(30L, "Développement mobile");
    }

    private Projet createTestProjet(Long id) {
        return new Projet(
                id,
                faker.lorem().sentence(3).replace(".", "")
        );
    }

    @Nested
    @DisplayName("Tests CRUD basiques")
    class CrudOperationsTest {

        @Test
        @DisplayName("findAll() - Doit retourner tous les projets")
        void findAll_ShouldReturnAllProjets() {
            // Given
            List<Projet> expectedProjets = Arrays.asList(migrationProjet, appelOffreProjet, mobileProjet);
            when(projetRepository.findAll()).thenReturn(expectedProjets);

            // When
            List<Projet> actualProjets = projetService.findAll();

            // Then
            assertThat(actualProjets)
                    .isNotNull()
                    .hasSize(3)
                    .containsExactlyElementsOf(expectedProjets);
            verify(projetRepository).findAll();
        }

        @Test
        @DisplayName("findById() - Doit retourner le projet avec l'ID donné")
        void findById_WithValidId_ShouldReturnProjet() {
            // Given
            Long projetId = 1L;
            when(projetRepository.findById(projetId)).thenReturn(Optional.of(testProjet));

            // When
            Optional<Projet> result = projetService.findById(projetId);

            // Then
            assertThat(result)
                    .isPresent()
                    .contains(testProjet);
            verify(projetRepository).findById(projetId);
        }

        @Test
        @DisplayName("findById() - Doit retourner Optional.empty() pour un ID inexistant")
        void findById_WithInvalidId_ShouldReturnEmpty() {
            // Given
            Long invalidId = 999L;
            when(projetRepository.findById(invalidId)).thenReturn(Optional.empty());

            // When
            Optional<Projet> result = projetService.findById(invalidId);

            // Then
            assertThat(result).isEmpty();
            verify(projetRepository).findById(invalidId);
        }

        @Test
        @DisplayName("save() - Doit sauvegarder et retourner le projet")
        void save_ShouldSaveAndReturnProjet() {
            // Given
            Projet projetToSave = createTestProjet(null);
            Projet savedProjet = createTestProjet(1L);
            when(projetRepository.save(projetToSave)).thenReturn(savedProjet);

            // When
            Projet result = projetService.save(projetToSave);

            // Then
            assertThat(result)
                    .isNotNull()
                    .isEqualTo(savedProjet);
            verify(projetRepository).save(projetToSave);
        }

        @Test
        @DisplayName("deleteById() - Doit supprimer le projet avec l'ID donné")
        void deleteById_ShouldDeleteProjet() {
            // Given
            Long projetId = 1L;

            // When
            projetService.deleteById(projetId);

            // Then
            verify(projetRepository).deleteById(projetId);
        }
    }

    @Nested
    @DisplayName("Tests de recherche par critères")
    class SearchOperationsTest {

        @Test
        @DisplayName("findByNomContaining() - Doit retourner les projets dont le nom contient le texte")
        void findByNomContaining_WithValidText_ShouldReturnMatchingProjets() {
            // Given
            String searchText = "Migration";
            List<Projet> expectedProjets = Arrays.asList(migrationProjet);
            when(projetRepository.findByNomContainingIgnoreCase(searchText)).thenReturn(expectedProjets);

            // When
            List<Projet> result = projetService.findByNomContaining(searchText);

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasSize(1)
                    .containsExactlyElementsOf(expectedProjets)
                    .allMatch(projet -> projet.getNom().toLowerCase().contains(searchText.toLowerCase()));
            verify(projetRepository).findByNomContainingIgnoreCase(searchText);
        }

        @Test
        @DisplayName("findByNomContaining() - Doit retourner une liste vide pour un texte non trouvé")
        void findByNomContaining_WithInvalidText_ShouldReturnEmptyList() {
            // Given
            String invalidText = "NotFound";
            when(projetRepository.findByNomContainingIgnoreCase(invalidText)).thenReturn(Arrays.asList());

            // When
            List<Projet> result = projetService.findByNomContaining(invalidText);

            // Then
            assertThat(result)
                    .isNotNull()
                    .isEmpty();
            verify(projetRepository).findByNomContainingIgnoreCase(invalidText);
        }

        @Test
        @DisplayName("findByNomContaining() - Doit gérer la recherche case insensitive")
        void findByNomContaining_ShouldBeCaseInsensitive() {
            // Given
            String searchText = "mobile";
            List<Projet> expectedProjets = Arrays.asList(mobileProjet);
            when(projetRepository.findByNomContainingIgnoreCase(searchText)).thenReturn(expectedProjets);

            // When
            List<Projet> result = projetService.findByNomContaining(searchText);

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasSize(1)
                    .containsExactlyElementsOf(expectedProjets);
            verify(projetRepository).findByNomContainingIgnoreCase(searchText);
        }

        @Test
        @DisplayName("findByNomContaining() - Doit trouver les projets avec des caractères spéciaux")
        void findByNomContaining_WithSpecialCharacters_ShouldReturnMatchingProjets() {
            // Given
            String searchText = "AO-2024";
            List<Projet> expectedProjets = Arrays.asList(appelOffreProjet);
            when(projetRepository.findByNomContainingIgnoreCase(searchText)).thenReturn(expectedProjets);

            // When
            List<Projet> result = projetService.findByNomContaining(searchText);

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasSize(1)
                    .containsExactlyElementsOf(expectedProjets);
            verify(projetRepository).findByNomContainingIgnoreCase(searchText);
        }
    }

    @Nested
    @DisplayName("Tests d'initialisation des données")
    class InitializationTest {

        @Test
        @DisplayName("initializeTestData() - Doit initialiser les données quand la base est vide")
        void initializeTestData_WhenDatabaseEmpty_ShouldCreateTestProjets() {
            // Given
            when(projetRepository.count()).thenReturn(0L);
            when(projetRepository.save(any(Projet.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            projetService.initializeTestData();

            // Then
            verify(projetRepository).count();
            verify(projetRepository, times(4)).save(any(Projet.class));
            
            // Vérifier que les projets sont créés avec les bons noms
            verify(projetRepository).save(argThat(projet -> 
                projet.getNom().equals("Migration Cloud")));
            verify(projetRepository).save(argThat(projet -> 
                projet.getNom().equals("Appel d'offre AO-2024-001")));
            verify(projetRepository).save(argThat(projet -> 
                projet.getNom().equals("Développement mobile")));
            verify(projetRepository).save(argThat(projet -> 
                projet.getNom().equals("Refonte site web")));
        }

        @Test
        @DisplayName("initializeTestData() - Ne doit pas initialiser les données quand la base n'est pas vide")
        void initializeTestData_WhenDatabaseNotEmpty_ShouldNotCreateTestProjets() {
            // Given
            when(projetRepository.count()).thenReturn(3L);

            // When
            projetService.initializeTestData();

            // Then
            verify(projetRepository).count();
            verify(projetRepository, never()).save(any(Projet.class));
        }
    }

    @Nested
    @DisplayName("Tests de validation des paramètres")
    class ValidationTest {

        @Test
        @DisplayName("findByNomContaining() - Doit gérer les chaînes null")
        void findByNomContaining_WithNullString_ShouldCallRepository() {
            // Given
            when(projetRepository.findByNomContainingIgnoreCase(null)).thenReturn(Arrays.asList());

            // When
            List<Projet> result = projetService.findByNomContaining(null);

            // Then
            assertThat(result).isNotNull().isEmpty();
            verify(projetRepository).findByNomContainingIgnoreCase(null);
        }

        @Test
        @DisplayName("findByNomContaining() - Doit gérer les chaînes vides")
        void findByNomContaining_WithEmptyString_ShouldReturnAllProjets() {
            // Given
            String emptyString = "";
            List<Projet> allProjets = Arrays.asList(migrationProjet, appelOffreProjet, mobileProjet);
            when(projetRepository.findByNomContainingIgnoreCase(emptyString)).thenReturn(allProjets);

            // When
            List<Projet> result = projetService.findByNomContaining(emptyString);

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasSize(3)
                    .containsExactlyElementsOf(allProjets);
            verify(projetRepository).findByNomContainingIgnoreCase(emptyString);
        }

        @Test
        @DisplayName("save() - Doit gérer les projets null")
        void save_WithNullProjet_ShouldCallRepository() {
            // Given
            when(projetRepository.save(null)).thenReturn(null);

            // When
            Projet result = projetService.save(null);

            // Then
            assertThat(result).isNull();
            verify(projetRepository).save(null);
        }
    }

    @Nested
    @DisplayName("Tests des cas limites")
    class EdgeCasesTest {

        @Test
        @DisplayName("findByNomContaining() - Doit gérer les espaces")
        void findByNomContaining_WithSpaces_ShouldCallRepository() {
            // Given
            String textWithSpaces = "  Migration  ";
            when(projetRepository.findByNomContainingIgnoreCase(textWithSpaces)).thenReturn(Arrays.asList());

            // When
            List<Projet> result = projetService.findByNomContaining(textWithSpaces);

            // Then
            assertThat(result).isNotNull();
            verify(projetRepository).findByNomContainingIgnoreCase(textWithSpaces);
        }

        @Test
        @DisplayName("findByNomContaining() - Doit gérer les caractères unicode")
        void findByNomContaining_WithUnicodeCharacters_ShouldCallRepository() {
            // Given
            String unicodeText = "Développement";
            when(projetRepository.findByNomContainingIgnoreCase(unicodeText)).thenReturn(Arrays.asList());

            // When
            List<Projet> result = projetService.findByNomContaining(unicodeText);

            // Then
            assertThat(result).isNotNull();
            verify(projetRepository).findByNomContainingIgnoreCase(unicodeText);
        }

        @Test
        @DisplayName("findByNomContaining() - Doit gérer les chaînes très longues")
        void findByNomContaining_WithVeryLongString_ShouldCallRepository() {
            // Given
            String longString = "a".repeat(1000);
            when(projetRepository.findByNomContainingIgnoreCase(longString)).thenReturn(Arrays.asList());

            // When
            List<Projet> result = projetService.findByNomContaining(longString);

            // Then
            assertThat(result).isNotNull().isEmpty();
            verify(projetRepository).findByNomContainingIgnoreCase(longString);
        }
    }
}