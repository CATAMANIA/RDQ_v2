package com.vibecoding.rdq.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires pour JwtService - TM-46
 * Couverture complète des méthodes JWT avec Mockito et AssertJ
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService - Tests unitaires")
class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private final String testSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long testExpiration = 86400000L; // 24 heures
    private final long testRefreshExpiration = 604800000L; // 7 jours
    private final String testUsername = "test@example.com";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", testExpiration);
        ReflectionTestUtils.setField(jwtService, "refreshExpirationMs", testRefreshExpiration);
    }

    @Nested
    @DisplayName("Tests de génération de tokens")
    class TokenGenerationTest {

        @Test
        @DisplayName("generateToken(Authentication) - Doit générer un token valide")
        void generateToken_WithAuthentication_ShouldReturnValidToken() {
            // Given
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn(testUsername);

            // When
            String token = jwtService.generateToken(authentication);

            // Then
            assertThat(token)
                    .isNotNull()
                    .isNotEmpty()
                    .contains(".");
            
            // Vérifier que le token contient le bon username
            String extractedUsername = jwtService.getUsernameFromToken(token);
            assertThat(extractedUsername).isEqualTo(testUsername);
        }

        @Test
        @DisplayName("generateToken(String) - Doit générer un token valide pour un username")
        void generateToken_WithUsername_ShouldReturnValidToken() {
            // When
            String token = jwtService.generateToken(testUsername);

            // Then
            assertThat(token)
                    .isNotNull()
                    .isNotEmpty()
                    .contains(".");
            
            // Vérifier que le token contient le bon username
            String extractedUsername = jwtService.getUsernameFromToken(token);
            assertThat(extractedUsername).isEqualTo(testUsername);
        }

        @Test
        @DisplayName("generateRefreshToken() - Doit générer un refresh token valide")
        void generateRefreshToken_ShouldReturnValidRefreshToken() {
            // When
            String refreshToken = jwtService.generateRefreshToken(testUsername);

            // Then
            assertThat(refreshToken)
                    .isNotNull()
                    .isNotEmpty()
                    .contains(".");
            
            // Vérifier que le refresh token contient le bon username
            String extractedUsername = jwtService.getUsernameFromToken(refreshToken);
            assertThat(extractedUsername).isEqualTo(testUsername);
        }

        @Test
        @DisplayName("generateToken() - Doit générer des tokens différents à chaque appel")
        void generateToken_MultipleCalls_ShouldReturnDifferentTokens() {
            // When
            String token1 = jwtService.generateToken(testUsername);
            String token2 = jwtService.generateToken(testUsername);

            // Then
            assertThat(token1)
                    .isNotNull()
                    .isNotEqualTo(token2);
        }
    }

    @Nested
    @DisplayName("Tests d'extraction de données")
    class TokenExtractionTest {

        private String validToken;

        @BeforeEach
        void setUp() {
            validToken = jwtService.generateToken(testUsername);
        }

        @Test
        @DisplayName("extractUsername() - Doit extraire le nom d'utilisateur du token")
        void extractUsername_WithValidToken_ShouldReturnUsername() {
            // When
            String extractedUsername = jwtService.getUsernameFromToken(validToken);

            // Then
            assertThat(extractedUsername).isEqualTo(testUsername);
        }

        @Test
        @DisplayName("extractExpiration() - Doit extraire la date d'expiration du token")
        void extractExpiration_WithValidToken_ShouldReturnExpirationDate() {
            // When
            Date expirationDate = jwtService.getExpirationDateFromToken(validToken);

            // Then
            assertThat(expirationDate)
                    .isNotNull()
                    .isAfter(new Date());
        }

        @Test
        @DisplayName("extractClaim() - Doit extraire une claim spécifique du token")
        void extractClaim_WithValidToken_ShouldReturnClaim() {
            // When
            String subject = jwtService.getClaimFromToken(validToken, claims -> claims.getSubject());

            // Then
            assertThat(subject).isEqualTo(testUsername);
        }
    }

    @Nested
    @DisplayName("Tests de validation de tokens")
    class TokenValidationTest {

        private String validToken;

        @BeforeEach
        void setUp() {
            validToken = jwtService.generateToken(testUsername);
            when(userDetails.getUsername()).thenReturn(testUsername);
        }

        @Test
        @DisplayName("isTokenValid() - Doit valider un token correct")
        void isTokenValid_WithValidToken_ShouldReturnTrue() {
            // When
            boolean isValid = jwtService.validateToken(validToken, userDetails);

            // Then
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("isTokenValid() - Doit rejeter un token avec un mauvais username")
        void isTokenValid_WithWrongUsername_ShouldReturnFalse() {
            // Given
            when(userDetails.getUsername()).thenReturn("different@example.com");

            // When
            boolean isValid = jwtService.validateToken(validToken, userDetails);

            // Then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("isTokenExpired() - Doit retourner false pour un token valide")
        void isTokenExpired_WithValidToken_ShouldReturnFalse() {
            // When
            boolean isExpired = jwtService.isTokenExpired(validToken);

            // Then
            assertThat(isExpired).isFalse();
        }

        @Test
        @DisplayName("validateToken() - Doit valider un token correct sans exception")
        void validateToken_WithValidToken_ShouldNotThrowException() {
            // When & Then
            boolean result = jwtService.validateToken(validToken);
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("Tests de gestion des erreurs")
    class ErrorHandlingTest {

        @Test
        @DisplayName("extractUsername() - Doit lever une exception pour un token malformé")
        void extractUsername_WithMalformedToken_ShouldThrowException() {
            // Given
            String malformedToken = "invalid.token.format";

            // When & Then
            assertThatThrownBy(() -> jwtService.getUsernameFromToken(malformedToken))
                    .isInstanceOf(MalformedJwtException.class);
        }

        @Test
        @DisplayName("validateToken() - Doit retourner false pour un token malformé")
        void validateToken_WithMalformedToken_ShouldReturnFalse() {
            // Given
            String malformedToken = "invalid.token.format";

            // When
            boolean result = jwtService.validateToken(malformedToken);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("validateToken() - Doit retourner false pour un token avec signature invalide")
        void validateToken_WithInvalidSignature_ShouldReturnFalse() {
            // Given
            String tokenWithInvalidSignature = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNjk5OTY4MDAwLCJleHAiOjE2OTk5NzE2MDB9.invalid_signature";

            // When
            boolean result = jwtService.validateToken(tokenWithInvalidSignature);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("validateToken() - Doit retourner false pour un token null")
        void validateToken_WithNullToken_ShouldReturnFalse() {
            // When
            boolean result = jwtService.validateToken(null);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("validateToken() - Doit retourner false pour un token vide")
        void validateToken_WithEmptyToken_ShouldReturnFalse() {
            // When
            boolean result = jwtService.validateToken("");

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Tests des cas limites")
    class EdgeCasesTest {

        @Test
        @DisplayName("generateToken() - Doit gérer un username null")
        void generateToken_WithNullUsername_ShouldThrowException() {
            // When & Then
            assertThatThrownBy(() -> jwtService.generateToken((String) null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("generateToken() - Doit gérer un username vide")
        void generateToken_WithEmptyUsername_ShouldCreateToken() {
            // When
            String token = jwtService.generateToken("");

            // Then
            assertThat(token)
                    .isNotNull()
                    .isNotEmpty();
            
            String extractedUsername = jwtService.getUsernameFromToken(token);
            assertThat(extractedUsername).isEmpty();
        }

        @Test
        @DisplayName("validateToken() - Doit gérer UserDetails null")
        void isTokenValid_WithNullUserDetails_ShouldReturnFalse() {
            // Given
            String token = jwtService.generateToken(testUsername);

            // When
            boolean isValid = jwtService.validateToken(token, null);

            // Then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("generateToken() - Doit créer un token avec les bonnes dates")
        void generateToken_ShouldHaveCorrectDates() {
            // Given
            Date beforeGeneration = new Date();

            // When
            String token = jwtService.generateToken(testUsername);
            Date expirationDate = jwtService.getExpirationDateFromToken(token);

            // Then
            Date expectedExpiration = new Date(beforeGeneration.getTime() + testExpiration);
            assertThat(expirationDate)
                    .isAfter(beforeGeneration)
                    .isCloseTo(expectedExpiration, 5000); // Tolérance de 5 secondes
        }

        @Test
        @DisplayName("generateRefreshToken() - Doit avoir une durée d'expiration plus longue")
        void generateRefreshToken_ShouldHaveLongerExpiration() {
            // Given
            Date beforeGeneration = new Date();

            // When
            String accessToken = jwtService.generateToken(testUsername);
            String refreshToken = jwtService.generateRefreshToken(testUsername);
            
            Date accessExpiration = jwtService.getExpirationDateFromToken(accessToken);
            Date refreshExpiration = jwtService.getExpirationDateFromToken(refreshToken);

            // Then
            assertThat(refreshExpiration).isAfter(accessExpiration);
            
            Date expectedRefreshExpiration = new Date(beforeGeneration.getTime() + testRefreshExpiration);
            assertThat(refreshExpiration)
                    .isCloseTo(expectedRefreshExpiration, 5000); // Tolérance de 5 secondes
        }
    }
}