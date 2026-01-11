package com.example.booking_api.security;

import com.example.booking_api.entity.Role;
import com.example.booking_api.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret",
                "test-secret-key-for-jwt-token-generation-minimum-32-characters");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400000);

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .roles(Set.of(Role.PATIENT))
                .enabled(true)
                .build();
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtils.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testGetUsernameFromToken() {
        String token = jwtUtils.generateToken(testUser);
        String username = jwtUtils.getUsernameFromToken(token);

        assertEquals("testuser", username);
    }

    @Test
    void testValidateToken() {
        String token = jwtUtils.generateToken(testUser);

        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void testValidateInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertFalse(jwtUtils.validateToken(invalidToken));
    }
}
