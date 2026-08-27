package org.gd.ws2.controllers;

import static org.junit.jupiter.api.Assertions.*;
import org.gd.ws2.Entity.User;
import org.gd.ws2.Entity.dto.LoginRequest;
import org.gd.ws2.Entity.dto.RegisterRequest;
import org.gd.ws2.Service.JwtService;
import org.gd.ws2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    private AuthController authController;
    @BeforeEach
    void setUp() {

        userRepository =
                mock(UserRepository.class);

        passwordEncoder =
                mock(PasswordEncoder.class);

        jwtService =
                mock(JwtService.class);

        authController =
                new AuthController(
                        userRepository,
                        passwordEncoder,
                        jwtService
                );
    }
    @Test
    void shouldRegisterNewUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("GD");
        request.setPassword("1234");
        when(
                userRepository.findByUsername("GD")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("encodedPassword");
        String result = authController.register(request);
        assertEquals("User registered successfully", result);
        verify(passwordEncoder)
                .encode("1234");

        verify(userRepository)
                .save(any(User.class));

    }
    @Test
    void shouldLoginWithCorrectPassword() {

        LoginRequest request =
                new LoginRequest();

        request.setUsername("GD");
        request.setPassword("1234");

        User user = new User();
        user.setUsername("GD");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("GD"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "1234",
                "encodedPassword"
        )).thenReturn(true);

        when(jwtService.generateToken("GD"))
                .thenReturn("jwt-token");

        String result =
                authController.login(request);

        assertEquals(
                "jwt-token",
                result
        );

        verify(passwordEncoder)
                .matches("1234", "encodedPassword");

        verify(jwtService)
                .generateToken("GD");
    }
    @Test
    void shouldRejectWrongPassword() {

        LoginRequest request =
                new LoginRequest();

        request.setUsername("GD");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setUsername("GD");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("GD"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "encodedPassword"
        )).thenReturn(false);

        String result =
                authController.login(request);

        assertEquals(
                "Wrong password",
                result
        );

        verify(passwordEncoder)
                .matches(
                        "wrongPassword",
                        "encodedPassword"
                );

        verify(jwtService, never())
                .generateToken(anyString());
    }
    @Test
    void shouldRejectExistingUsername() {

        RegisterRequest request =
                new RegisterRequest();

        request.setUsername("GD");
        request.setPassword("1234");

        User existingUser = new User();
        existingUser.setUsername("GD");

        when(userRepository.findByUsername("GD"))
                .thenReturn(Optional.of(existingUser));

        String result =
                authController.register(request);

        assertEquals(
                "Username already exists",
                result
        );

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }


}