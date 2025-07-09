package com.jcuadrado.company.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcuadrado.company.dtos.LoginRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test login with valid credentials returns JWT token")
    void testLoginWithValidCredentials() throws Exception {
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("admin")
                .password("admin")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.token", not(emptyString())));
    }

    @Test
    @DisplayName("Test login with invalid credentials returns unauthorized")
    void testLoginWithInvalidCredentials() throws Exception {
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("admin")
                .password("wrongpassword")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Test login with missing username returns bad request")
    void testLoginWithMissingUsername() throws Exception {
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .password("admin")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test login with missing password returns bad request")
    void testLoginWithMissingPassword() throws Exception {
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("admin")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test login with empty username returns bad request")
    void testLoginWithEmptyUsername() throws Exception {
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("")
                .password("admin")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test login with empty password returns bad request")
    void testLoginWithEmptyPassword() throws Exception {
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("admin")
                .password("")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test login with invalid JSON returns bad request")
    void testLoginWithInvalidJson() throws Exception {
        String invalidJson = "{ invalid json }";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test login without content type returns unsupported media type")
    void testLoginWithoutContentType() throws Exception {
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("admin")
                .password("admin")
                .build();

        mockMvc.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Test login endpoint only accepts POST method")
    void testLoginEndpointOnlyAcceptsPost() throws Exception {
        LoginRequestDto loginRequest = LoginRequestDto.builder()
                .username("admin")
                .password("admin")
                .build();

        // Test GET method is not allowed
        mockMvc.perform(get("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isMethodNotAllowed());

        // Test PUT method is not allowed
        mockMvc.perform(put("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isMethodNotAllowed());

        // Test DELETE method is not allowed
        mockMvc.perform(delete("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isMethodNotAllowed());
    }
}
