package com.example.booking_api.service;

import com.example.booking_api.dto.request.LoginRequest;
import com.example.booking_api.dto.request.RegisterRequest;
import com.example.booking_api.dto.response.AuthResponse;
import com.example.booking_api.entity.Role;
import com.example.booking_api.entity.User;
import com.example.booking_api.exception.ResourceAlreadyExistsException;
import com.example.booking_api.repository.UserRepository;
import com.example.booking_api.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest(
                "testuser", "test@example.com", "password123", null
        );

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");

        User savedUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .roles(Set.of(Role.PATIENT))
                .enabled(true)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtils.generateToken(any())).thenReturn("jwt_token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwt_token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertTrue(response.getRoles().contains(Role.PATIENT));

        verify(userRepository).save(any(User.class));
        verify(jwtUtils).generateToken(any());
    }

    @Test
    void testRegisterUsernameExists() {
        RegisterRequest request = new RegisterRequest(
                "testuser", "test@example.com", "password123", null
        );

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> authService.register(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterEmailExists() {
        RegisterRequest request = new RegisterRequest(
                "testuser", "test@example.com", "password123", null
        );

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> authService.register(request));

        verify(userRepository, never()).save(any());
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("testuser", "password123");

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .roles(Set.of(Role.PATIENT))
                .enabled(true)
                .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateToken(user)).thenReturn("jwt_token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt_token", response.getToken());
        assertEquals("testuser", response.getUsername());

        verify(authenticationManager).authenticate(any());
        verify(jwtUtils).generateToken(user);
    }
}

