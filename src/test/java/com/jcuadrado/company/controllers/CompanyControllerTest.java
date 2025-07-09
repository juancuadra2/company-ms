package com.jcuadrado.company.controllers;

import com.jcuadrado.company.dtos.*;
import com.jcuadrado.company.exceptions.GeneralException;
import com.jcuadrado.company.services.CompanyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyControllerTest {

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private CompanyController companyController;

    @Test
    @DisplayName("Test create company success")
    public void testCreateCompanySuccess() {
        CreateCompanyDto createCompanyDto = CreateCompanyDto.builder()
                .name("Test Company")
                .nit("123456789")
                .build();
        CompanyDto expectedCompany = CompanyDto.builder()
                .id(1L)
                .name("Test Company")
                .nit("123456789")
                .build();
        when(companyService.create(createCompanyDto)).thenReturn(expectedCompany);
        ResponseEntity<?> response = companyController.create(createCompanyDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedCompany, response.getBody());
    }

    @Test
    @DisplayName("Test get all companies success")
    public void testGetAllCompaniesSuccess() {
        String search = "test";
        Integer page = 0;
        Integer size = 10;
        Boolean isActive = true;

        List<CompanyDto> companies = Arrays.asList(
            CompanyDto.builder().id(1L).name("Test Company 1").nit("123456789").build(),
            CompanyDto.builder().id(2L).name("Test Company 2").nit("987654321").build()
        );

        PaginatedResponseDto<CompanyDto> expectedResponse = PaginatedResponseDto.<CompanyDto>builder()
            .data(companies)
            .totalPages(1)
            .totalElements(2L)
            .pageSize(10)
            .currentPage(0)
            .build();

        when(companyService.getAll(any(PaginationQueryDto.class))).thenReturn(expectedResponse);

        ResponseEntity<?> response = companyController.getAll(search, page, size, isActive);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        verify(companyService).getAll(argThat(queryDto -> 
            search.equals(queryDto.getSearch()) &&
            page.equals(queryDto.getPage()) &&
            size.equals(queryDto.getSize()) &&
            isActive.equals(queryDto.getIsActive())
        ));
    }

    @Test
    @DisplayName("Test get company by id success")
    public void testGetCompanyByIdSuccess() {
        Long companyId = 1L;
        CompanyDto expectedCompany = CompanyDto.builder()
            .id(companyId)
            .name("Test Company")
            .nit("123456789")
            .address("Test Address")
            .phone("+1234567890")
            .build();

        when(companyService.getById(companyId)).thenReturn(expectedCompany);

        ResponseEntity<?> response = companyController.getById(companyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCompany, response.getBody());

        verify(companyService).getById(companyId);
    }

    @Test
    @DisplayName("Test update company success")
    public void testUpdateCompanySuccess() {
        Long companyId = 1L;
        UpdateCompanyDto updateCompanyDto = UpdateCompanyDto.builder()
            .name("Updated Company")
            .nit("987654321")
            .address("Updated Address")
            .phone("+9876543210")
            .build();

        CompanyDto expectedCompany = CompanyDto.builder()
            .id(companyId)
            .name("Updated Company")
            .nit("987654321")
            .address("Updated Address")
            .phone("+9876543210")
            .build();

        when(companyService.update(companyId, updateCompanyDto)).thenReturn(expectedCompany);

        ResponseEntity<?> response = companyController.update(companyId, updateCompanyDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCompany, response.getBody());

        verify(companyService).update(companyId, updateCompanyDto);
    }

    @Test
    @DisplayName("Test delete company success")
    public void testDeleteCompanySuccess() {
        // Prepare test data
        Long companyId = 1L;

        // Mock service behavior - void method, so no when() needed
        doNothing().when(companyService).delete(companyId);

        // Call controller method
        ResponseEntity<?> response = companyController.delete(companyId);

        // Verify results
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        // Verify that the service was called with correct parameters
        verify(companyService).delete(companyId);
    }

    @Test
    @DisplayName("Test create company with exception")
    public void testCreateCompanyWithException() {
        // Prepare test data
        CreateCompanyDto createCompanyDto = CreateCompanyDto.builder()
                .name("Test Company")
                .nit("123456789")
                .build();

        // Mock service behavior to throw exception
        GeneralException exception = new GeneralException(HttpStatus.BAD_REQUEST, "Error creating company");
        when(companyService.create(createCompanyDto)).thenThrow(exception);

        // Call controller method and verify exception is thrown
        assertThrows(GeneralException.class, () -> companyController.create(createCompanyDto));

        // Verify that the service was called with correct parameters
        verify(companyService).create(createCompanyDto);
    }

    @Test
    @DisplayName("Test get all companies with exception")
    public void testGetAllCompaniesWithException() {
        // Prepare test data
        String search = "test";
        Integer page = 0;
        Integer size = 10;
        Boolean isActive = true;

        // Mock service behavior to throw exception
        GeneralException exception = new GeneralException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting companies");
        when(companyService.getAll(any(PaginationQueryDto.class))).thenThrow(exception);

        // Call controller method and verify exception is thrown
        assertThrows(GeneralException.class, () -> companyController.getAll(search, page, size, isActive));

        // Verify that the service was called with correct parameters
        verify(companyService).getAll(any(PaginationQueryDto.class));
    }

    @Test
    @DisplayName("Test get company by id with exception")
    public void testGetCompanyByIdWithException() {
        // Prepare test data
        Long companyId = 1L;

        // Mock service behavior to throw exception
        GeneralException exception = new GeneralException(HttpStatus.NOT_FOUND, "Company not found");
        when(companyService.getById(companyId)).thenThrow(exception);

        // Call controller method and verify exception is thrown
        assertThrows(GeneralException.class, () -> companyController.getById(companyId));

        // Verify that the service was called with correct parameters
        verify(companyService).getById(companyId);
    }

    @Test
    @DisplayName("Test update company with exception")
    public void testUpdateCompanyWithException() {
        // Prepare test data
        Long companyId = 1L;
        UpdateCompanyDto updateCompanyDto = UpdateCompanyDto.builder()
                .name("Updated Company")
                .nit("987654321")
                .build();

        // Mock service behavior to throw exception
        GeneralException exception = new GeneralException(HttpStatus.NOT_FOUND, "Company not found");
        when(companyService.update(companyId, updateCompanyDto)).thenThrow(exception);

        // Call controller method and verify exception is thrown
        assertThrows(GeneralException.class, () -> companyController.update(companyId, updateCompanyDto));

        // Verify that the service was called with correct parameters
        verify(companyService).update(companyId, updateCompanyDto);
    }

    @Test
    @DisplayName("Test delete company with exception")
    public void testDeleteCompanyWithException() {
        // Prepare test data
        Long companyId = 1L;

        // Mock service behavior to throw exception
        GeneralException exception = new GeneralException(HttpStatus.NOT_FOUND, "Company not found");
        doThrow(exception).when(companyService).delete(companyId);

        // Call controller method and verify exception is thrown
        assertThrows(GeneralException.class, () -> companyController.delete(companyId));

        // Verify that the service was called with correct parameters
        verify(companyService).delete(companyId);
    }
}
