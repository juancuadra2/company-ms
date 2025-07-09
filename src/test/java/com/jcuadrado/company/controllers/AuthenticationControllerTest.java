package com.jcuadrado.company.controllers;

import com.jcuadrado.company.dtos.AuthDto;
import com.jcuadrado.company.dtos.LoginRequestDto;
import com.jcuadrado.company.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    @DisplayName("Test login success")
    void testLoginSuccess() {
        // Given
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("testuser")
                .password("password123")
                .build();

        AuthDto expectedAuthDto = AuthDto.builder()
                .token("jwt.token.here")
                .build();

        when(authService.login(loginRequest)).thenReturn(expectedAuthDto);

        // When
        ResponseEntity<AuthDto> response = authenticationController.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAuthDto, response.getBody());
        assertEquals("jwt.token.here", response.getBody().getToken());

        // Verify service interaction
        verify(authService).login(loginRequest);
    }

    @Test
    @DisplayName("Test login with invalid credentials")
    void testLoginWithInvalidCredentials() {
        // Given
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        when(authService.login(loginRequest)).thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            authenticationController.login(loginRequest);
        });

        // Verify service interaction
        verify(authService).login(loginRequest);
    }

    @Test
    @DisplayName("Test login with valid admin credentials")
    void testLoginWithValidAdminCredentials() {
        // Given
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("admin")
                .password("admin123")
                .build();

        AuthDto expectedAuthDto = AuthDto.builder()
                .token("admin.jwt.token.here")
                .build();

        when(authService.login(loginRequest)).thenReturn(expectedAuthDto);

        // When
        ResponseEntity<AuthDto> response = authenticationController.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAuthDto, response.getBody());
        assertNotNull(response.getBody().getToken());
        assertEquals("admin.jwt.token.here", response.getBody().getToken());

        // Verify service interaction
        verify(authService).login(loginRequest);
    }

    @Test
    @DisplayName("Test login returns correct response structure")
    void testLoginReturnsCorrectResponseStructure() {
        // Given
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("user")
                .password("pass")
                .build();

        AuthDto authDto = AuthDto.builder()
                .token("sample.jwt.token")
                .build();

        when(authService.login(loginRequest)).thenReturn(authDto);

        // When
        ResponseEntity<AuthDto> response = authenticationController.login(loginRequest);

        // Then
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthDto);
        assertNotNull(response.getBody().getToken());

        // Verify service was called exactly once
        verify(authService, times(1)).login(loginRequest);
    }

    @Test
    @DisplayName("Test login with null request throws exception")
    void testLoginWithNullRequest() {
        // Given
        LoginRequestDto nullRequest = null;

        // When & Then - This would typically be caught by validation
        when(authService.login(nullRequest)).thenThrow(new IllegalArgumentException("Login request cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> {
            authenticationController.login(nullRequest);
        });

        verify(authService).login(nullRequest);
    }

    @Test
    @DisplayName("Test login service method is called with correct parameters")
    void testLoginServiceMethodIsCalledWithCorrectParameters() {
        // Given
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("specificuser")
                .password("specificpassword")
                .build();

        AuthDto authDto = AuthDto.builder()
                .token("specific.token")
                .build();

        when(authService.login(loginRequest)).thenReturn(authDto);

        // When
        authenticationController.login(loginRequest);

        // Then - Verify the exact object was passed
        verify(authService).login(argThat(request -> 
            "specificuser".equals(request.getUsername()) && 
            "specificpassword".equals(request.getPassword())
        ));
    }
}
