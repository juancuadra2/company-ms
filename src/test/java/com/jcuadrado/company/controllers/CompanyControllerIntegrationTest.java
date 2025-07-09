package com.jcuadrado.company.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcuadrado.company.dtos.CreateCompanyDto;
import com.jcuadrado.company.dtos.UpdateCompanyDto;
import com.jcuadrado.company.entities.Company;
import com.jcuadrado.company.repositories.CompanyRepository;
import com.jcuadrado.company.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CompanyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JwtService jwtService;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Clean the database before each test
        companyRepository.deleteAll();

        // Generate JWT token for tests using a test user
        jwtToken = "Bearer " + jwtService.generateTokenFromUsername(User
                .withUsername("admin")
                .password("admin")
                .authorities("ADMIN")
                .build()
        );
    }

    @Test
    @DisplayName("Integration Test - Create Company")
    void testCreateCompany() throws Exception {
        CreateCompanyDto createCompanyDto = CreateCompanyDto.builder()
                .name("Test Company")
                .nit("12345678")
                .address("Test Address")
                .phone("+1234567890")
                .build();

        mockMvc.perform(post("/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCompanyDto))
                .header("Authorization", jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Test Company")))
                .andExpect(jsonPath("$.nit", is("12345678")))
                .andExpect(jsonPath("$.address", is("Test Address")))
                .andExpect(jsonPath("$.phone", is("+1234567890")));

        List<Company> companies = companyRepository.findAll();
        assertEquals(1, companies.size());
        assertEquals("Test Company", companies.get(0).getName());
        assertEquals("12345678", companies.get(0).getNit());
    }

    @Test
    @DisplayName("Integration Test - Get All Companies")
    void testGetAllCompanies() throws Exception {
        Company company1 = Company.builder()
                .name("Test Company 1")
                .nit("12345678")
                .address("Test Address 1")
                .phone("+1234567890")
                .build();

        Company company2 = Company.builder()
                .name("Test Company 2")
                .nit("87654321")
                .address("Test Address 2")
                .phone("+0987654321")
                .build();

        companyRepository.saveAll(List.of(company1, company2));

        mockMvc.perform(get("/companies")
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name", is("Test Company 1")))
                .andExpect(jsonPath("$.data[1].name", is("Test Company 2")))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    @DisplayName("Integration Test - Get Company By Id")
    void testGetCompanyById() throws Exception {
        Company company = Company.builder()
                .name("Test Company")
                .nit("12345678")
                .address("Test Address")
                .phone("+1234567890")
                .build();

        company = companyRepository.save(company);

        mockMvc.perform(get("/companies/{id}", company.getId())
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(company.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Test Company")))
                .andExpect(jsonPath("$.nit", is("12345678")))
                .andExpect(jsonPath("$.address", is("Test Address")))
                .andExpect(jsonPath("$.phone", is("+1234567890")));
    }

    @Test
    @DisplayName("Integration Test - Update Company")
    void testUpdateCompany() throws Exception {
        Company company = Company.builder()
                .name("Test Company")
                .nit("12345678")
                .address("Test Address")
                .phone("+1234567890")
                .build();

        company = companyRepository.save(company);

        UpdateCompanyDto updateCompanyDto = UpdateCompanyDto.builder()
                .name("Updated Company")
                .nit("87654321")
                .address("Updated Address")
                .phone("+0987654321")
                .build();

        mockMvc.perform(put("/companies/{id}", company.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateCompanyDto))
                .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(company.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Updated Company")))
                .andExpect(jsonPath("$.nit", is("87654321")))
                .andExpect(jsonPath("$.address", is("Updated Address")))
                .andExpect(jsonPath("$.phone", is("+0987654321")));

        Company updatedCompany = companyRepository.findById(company.getId()).orElse(null);
        assertNotNull(updatedCompany);
        assertEquals("Updated Company", updatedCompany.getName());
        assertEquals("87654321", updatedCompany.getNit());
    }

    @Test
    @DisplayName("Integration Test - Delete Company")
    void testDeleteCompany() throws Exception {
        Company company = Company.builder()
                .name("Test Company")
                .nit("12345678")
                .address("Test Address")
                .phone("+1234567890")
                .build();

        company = companyRepository.save(company);

        mockMvc.perform(delete("/companies/{id}", company.getId())
                .header("Authorization", jwtToken))
                .andExpect(status().isNoContent());

        assertFalse(companyRepository.existsById(company.getId()));
    }

    @Test
    @DisplayName("Integration Test - Get Company By Id Not Found")
    void testGetCompanyByIdNotFound() throws Exception {
        mockMvc.perform(get("/companies/{id}", 999L)
                .header("Authorization", jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Integration Test - Create Company with Invalid Data")
    void testCreateCompanyWithInvalidData() throws Exception {
        CreateCompanyDto invalidCompanyDto = CreateCompanyDto.builder()
                .name("")
                .nit("123")
                .phone("invalid-phone")
                .build();

        mockMvc.perform(post("/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCompanyDto))
                .header("Authorization", jwtToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration Test - Should Return 401 When No JWT Token Provided")
    void testUnauthorizedWhenNoToken() throws Exception {
        mockMvc.perform(get("/companies"))
                .andExpect(status().isForbidden());
    }

}
