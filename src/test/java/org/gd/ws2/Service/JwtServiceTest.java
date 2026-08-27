package org.gd.ws2.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private JwtService jwtService;
    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void shouldGenerateAndExtractUsername()
    {String token = jwtService.generateToken("GD");
        String username = jwtService.extractUsername(token);
        assertEquals("GD", username);

    }
    @Test
    void shouldValidateValidToken() {

        String token =
                jwtService.generateToken("GD");

        assertTrue(
                jwtService.isTokenValid(token)
        );
    }
    @Test
    void shouldRejectInvalidToken() {

        String token = "invalid-token";

        assertFalse(
                jwtService.isTokenValid(token)
        );
    }

}