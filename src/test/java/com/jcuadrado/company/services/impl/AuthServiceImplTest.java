package com.jcuadrado.company.services.impl;

import com.jcuadrado.company.dtos.AuthDto;
import com.jcuadrado.company.dtos.LoginRequestDto;
import com.jcuadrado.company.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Test login with valid credentials")
    void testLoginWithValidCredentials() {
        // Given
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("testuser")
                .password("password123")
                .build();

        UserDetails userDetails = User.builder()
                .username("testuser")
                .password("password123")
                .authorities(Collections.emptyList())
                .build();

        String expectedToken = "jwt.token.here";

        // Mock authentication manager
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Mock JWT service
        when(jwtService.generateTokenFromUsername(userDetails)).thenReturn(expectedToken);

        // When
        AuthDto result = authService.login(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals(expectedToken, result.getToken());

        // Verify interactions
        verify(authenticationManager).authenticate(
                argThat(auth -> auth instanceof UsernamePasswordAuthenticationToken &&
                        "testuser".equals(auth.getName()) &&
                        "password123".equals(auth.getCredentials()))
        );
        verify(jwtService).generateTokenFromUsername(userDetails);
    }

    @Test
    @DisplayName("Test login with invalid credentials throws BadCredentialsException")
    void testLoginWithInvalidCredentials() {
        // Given
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        // Mock authentication manager to throw exception
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });

        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateTokenFromUsername(any());
    }

    @Test
    @DisplayName("Test login with null username")
    void testLoginWithNullUsername() {
        // Given
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username(null)
                .password("password123")
                .build();

        // Mock authentication manager to throw exception
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Username cannot be null"));

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });

        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateTokenFromUsername(any());
    }

    @Test
    @DisplayName("Test login with empty password")
    void testLoginWithEmptyPassword() {
        // Given
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("testuser")
                .password("")
                .build();

        // Mock authentication manager to throw exception
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Password cannot be empty"));

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });

        // Verify interactions
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateTokenFromUsername(any());
    }

    @Test
    @DisplayName("Test login creates correct authentication token")
    void testLoginCreatesCorrectAuthenticationToken() {
        // Given
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("admin")
                .password("admin123")
                .build();

        UserDetails userDetails = User.builder()
                .username("admin")
                .password("admin123")
                .authorities(Collections.emptyList())
                .build();

        String expectedToken = "admin.jwt.token";

        // Mock authentication manager
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Mock JWT service
        when(jwtService.generateTokenFromUsername(userDetails)).thenReturn(expectedToken);

        // When
        AuthDto result = authService.login(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals(expectedToken, result.getToken());

        // Verify the exact authentication token created
        verify(authenticationManager).authenticate(
                argThat(token -> 
                    token instanceof UsernamePasswordAuthenticationToken &&
                    "admin".equals(token.getPrincipal()) &&
                    "admin123".equals(token.getCredentials())
                )
        );
    }
}
