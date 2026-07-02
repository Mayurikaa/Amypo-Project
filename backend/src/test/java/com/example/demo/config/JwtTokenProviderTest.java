package com.example.demo.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    private static final String SECRET =
            "taskguard-super-secret-key-for-hs512-algorithm-must-be-at-least-64-characters-long-development";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET);
    }

    @Test
    void createToken_returnsThreePartJwt() {
        String token = jwtTokenProvider.createToken("user@example.com", "PROJECT_MANAGER");

        assertNotNull(token);
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT must have header.payload.signature format");
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        String token = jwtTokenProvider.createToken("user@example.com", "PROJECT_MANAGER");

        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_invalidToken_returnsFalse() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.string"));
    }

    @Test
    void validateToken_emptyToken_returnsFalse() {
        assertFalse(jwtTokenProvider.validateToken(""));
    }

    @Test
    void getAuthentication_returnsCorrectEmail() {
        String token = jwtTokenProvider.createToken("user@example.com", "PROJECT_MANAGER");

        Authentication auth = jwtTokenProvider.getAuthentication(token);

        assertNotNull(auth);
        assertEquals("user@example.com", auth.getName());
    }

    @Test
    void getAuthentication_containsRoleAuthority() {
        String token = jwtTokenProvider.createToken("user@example.com", "PROJECT_DIRECTOR");

        Authentication auth = jwtTokenProvider.getAuthentication(token);

        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PROJECT_DIRECTOR")));
    }

    @Test
    void createToken_differentRoles_produceDifferentTokens() {
        String token1 = jwtTokenProvider.createToken("user@example.com", "PROJECT_DIRECTOR");
        String token2 = jwtTokenProvider.createToken("user@example.com", "TEAM_CONTRIBUTOR");

        assertNotEquals(token1, token2);
    }

    @Test
    void createToken_differentEmails_produceDifferentTokens() {
        String token1 = jwtTokenProvider.createToken("user1@example.com", "PROJECT_MANAGER");
        String token2 = jwtTokenProvider.createToken("user2@example.com", "PROJECT_MANAGER");

        assertNotEquals(token1, token2);
    }
}
