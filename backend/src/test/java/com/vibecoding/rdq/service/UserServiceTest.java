package com.vibecoding.rdq.service;

import com.github.javafaker.Faker;
import com.vibecoding.rdq.entity.User;
import com.vibecoding.rdq.enums.Role;
import com.vibecoding.rdq.repository.UserRepository;
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
 * Tests unitaires pour UserService - TM-46
 * Couverture complète des méthodes avec Mockito et AssertJ
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Tests unitaires")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final Faker faker = new Faker();
    private User testUser;
    private User adminUser;
    private User managerUser;
    private User collaborateurUser;

    @BeforeEach
    void setUp() {
        testUser = createTestUser(1L, Role.MANAGER);
        adminUser = createTestUser(10L, Role.ADMIN);
        managerUser = createTestUser(20L, Role.MANAGER);
        collaborateurUser = createTestUser(30L, Role.COLLABORATEUR);
    }

    private User createTestUser(Long id, Role role) {
        User user = new User(
                faker.internet().emailAddress(),
                faker.internet().emailAddress(),
                "encodedPassword123456789012345678901234567890123456789012",
                role
        );
        if (id != null) {
            user.setIdUser(id);
        }
        return user;
    }

    @Nested
    @DisplayName("Tests CRUD basiques")
    class CrudOperationsTest {

        @Test
        @DisplayName("findAll() - Doit retourner tous les utilisateurs")
        void findAll_ShouldReturnAllUsers() {
            // Given
            List<User> expectedUsers = Arrays.asList(adminUser, managerUser, collaborateurUser);
            when(userRepository.findAll()).thenReturn(expectedUsers);

            // When
            List<User> actualUsers = userService.findAll();

            // Then
            assertThat(actualUsers)
                    .isNotNull()
                    .hasSize(3)
                    .containsExactlyElementsOf(expectedUsers);
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("findById() - Doit retourner l'utilisateur avec l'ID donné")
        void findById_WithValidId_ShouldReturnUser() {
            // Given
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userService.findById(userId);

            // Then
            assertThat(result)
                    .isPresent()
                    .contains(testUser);
            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("findById() - Doit retourner Optional.empty() pour un ID inexistant")
        void findById_WithInvalidId_ShouldReturnEmpty() {
            // Given
            Long invalidId = 999L;
            when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

            // When
            Optional<User> result = userService.findById(invalidId);

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).findById(invalidId);
        }

        @Test
        @DisplayName("save() - Doit sauvegarder et retourner l'utilisateur")
        void save_ShouldSaveAndReturnUser() {
            // Given
            User userToSave = createTestUser(null, Role.MANAGER);
            User savedUser = createTestUser(1L, Role.MANAGER);
            when(userRepository.save(userToSave)).thenReturn(savedUser);

            // When
            User result = userService.save(userToSave);

            // Then
            assertThat(result)
                    .isNotNull()
                    .isEqualTo(savedUser);
            verify(userRepository).save(userToSave);
        }

        @Test
        @DisplayName("deleteById() - Doit supprimer l'utilisateur avec l'ID donné")
        void deleteById_ShouldDeleteUser() {
            // Given
            Long userId = 1L;

            // When
            userService.deleteById(userId);

            // Then
            verify(userRepository).deleteById(userId);
        }
    }

    @Nested
    @DisplayName("Tests de recherche par critères")
    class SearchOperationsTest {

        @Test
        @DisplayName("findByRole() - Doit retourner les utilisateurs avec le rôle MANAGER")
        void findByRole_WithManagerRole_ShouldReturnManagers() {
            // Given
            String role = "MANAGER";
            List<User> expectedManagers = Arrays.asList(testUser, managerUser);
            when(userRepository.findByRole(Role.MANAGER)).thenReturn(expectedManagers);

            // When
            List<User> result = userService.findByRole(role);

            // Then
            assertThat(result)
                    .isNotNull()
                    .hasSize(2)
                    .containsExactlyElementsOf(expectedManagers)
                    .allMatch(user -> user.getRole().equals(Role.MANAGER));
            verify(userRepository).findByRole(Role.MANAGER);
        }

        @Test
        @DisplayName("findByRole() - Doit retourner une liste vide pour un rôle inexistant")
        void findByRole_WithInvalidRole_ShouldReturnEmptyList() {
            // Given
            String invalidRole = "INVALID_ROLE";
            when(userRepository.findByRole(Role.COLLABORATEUR)).thenReturn(Arrays.asList());

            // When
            List<User> result = userService.findByRole(invalidRole);

            // Then
            assertThat(result)
                    .isNotNull()
                    .isEmpty();
            verify(userRepository).findByRole(Role.COLLABORATEUR);
        }

        @Test
        @DisplayName("findByEmail() - Doit retourner l'utilisateur avec l'email donné")
        void findByEmail_WithValidEmail_ShouldReturnUser() {
            // Given
            String email = testUser.getEmail();
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

            // When
            User result = userService.findByEmail(email);

            // Then
            assertThat(result)
                    .isNotNull()
                    .isEqualTo(testUser);
            verify(userRepository).findByEmail(email);
        }

        @Test
        @DisplayName("findByEmail() - Doit retourner null pour un email inexistant")
        void findByEmail_WithInvalidEmail_ShouldReturnNull() {
            // Given
            String invalidEmail = "invalid@example.com";
            when(userRepository.findByEmail(invalidEmail)).thenReturn(null);

            // When
            User result = userService.findByEmail(invalidEmail);

            // Then
            assertThat(result).isNull();
            verify(userRepository).findByEmail(invalidEmail);
        }
    }

    @Nested
    @DisplayName("Tests d'initialisation des données")
    class InitializationTest {

        @Test
        @DisplayName("initializeTestData() - Doit initialiser les données quand la base est vide")
        void initializeTestData_WhenDatabaseEmpty_ShouldCreateTestUsers() {
            // Given
            when(userRepository.count()).thenReturn(0L);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            userService.initializeTestData();

            // Then
            verify(userRepository).count();
            verify(userRepository, times(5)).save(any(User.class));
            
            // Vérifier que les utilisateurs sont créés avec les bons rôles
            verify(userRepository).save(argThat(user -> 
                user.getRole().equals("ADMIN") && user.getEmail().equals("admin@rdq.com")));
            verify(userRepository, times(2)).save(argThat(user -> 
                user.getRole().equals("MANAGER")));
            verify(userRepository, times(2)).save(argThat(user -> 
                user.getRole().equals("COLLABORATEUR")));
        }

        @Test
        @DisplayName("initializeTestData() - Ne doit pas initialiser les données quand la base n'est pas vide")
        void initializeTestData_WhenDatabaseNotEmpty_ShouldNotCreateTestUsers() {
            // Given
            when(userRepository.count()).thenReturn(5L);

            // When
            userService.initializeTestData();

            // Then
            verify(userRepository).count();
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Tests de validation des paramètres")
    class ValidationTest {

        @Test
        @DisplayName("findByRole() - Doit gérer les rôles null")
        void findByRole_WithNullRole_ShouldCallRepository() {
            // Given
            when(userRepository.findByRole(null)).thenReturn(Arrays.asList());

            // When
            List<User> result = userService.findByRole(null);

            // Then
            assertThat(result).isNotNull().isEmpty();
            verify(userRepository).findByRole(null);
        }

        @Test
        @DisplayName("findByEmail() - Doit gérer les emails null")
        void findByEmail_WithNullEmail_ShouldCallRepository() {
            // Given
            when(userRepository.findByEmail(null)).thenReturn(null);

            // When
            User result = userService.findByEmail(null);

            // Then
            assertThat(result).isNull();
            verify(userRepository).findByEmail(null);
        }

        @Test
        @DisplayName("save() - Doit gérer les utilisateurs null")
        void save_WithNullUser_ShouldCallRepository() {
            // Given
            when(userRepository.save(null)).thenReturn(null);

            // When
            User result = userService.save(null);

            // Then
            assertThat(result).isNull();
            verify(userRepository).save(null);
        }
    }
}