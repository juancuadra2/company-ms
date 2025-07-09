package com.jcuadrado.company.services.impl;

import com.jcuadrado.company.constants.CompanyErrorMessages;
import com.jcuadrado.company.dtos.*;
import com.jcuadrado.company.entities.Company;
import com.jcuadrado.company.exceptions.GeneralException;
import com.jcuadrado.company.mappers.CompanyMapper;
import com.jcuadrado.company.repositories.CompanyRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Test
    @DisplayName("Test create company with valid data")
    void testCreateCompanyWithValidData() {
        CreateCompanyDto createCompanyDto = CreateCompanyDto.builder()
                .name("Test Company")
                .nit("123456789")
                .build();
        CompanyDto expectedCompanyDto = CompanyDto.builder()
                .id(1L)
                .name("Test Company")
                .nit("123456789")
                .build();

        when(companyMapper.toCompany(createCompanyDto)).thenReturn(new Company());
        when(companyRepository.save(new Company())).thenReturn(new Company());
        when(companyMapper.toCompanyDto(new Company())).thenReturn(expectedCompanyDto);

        CompanyDto actualCompanyDto = companyService.create(createCompanyDto);

        assertEquals(expectedCompanyDto.getName(), actualCompanyDto.getName());
        assertEquals(expectedCompanyDto.getNit(), actualCompanyDto.getNit());
        assertEquals(expectedCompanyDto.getId(), actualCompanyDto.getId());
        assertEquals(expectedCompanyDto, actualCompanyDto);

        verify(companyMapper).toCompany(createCompanyDto);
        verify(companyRepository).save(new Company());
        verify(companyMapper).toCompanyDto(new Company());
    }

    @Test
    @DisplayName("Test create company with DataIntegrityViolationException")
    void testCreateCompanyWithException() {
        CreateCompanyDto createCompanyDto = CreateCompanyDto.builder()
                .name("Test Company")
                .nit("123456789")
                .build();

        when(companyMapper.toCompany(createCompanyDto)).thenReturn(new Company());
        when(companyRepository.save(new Company())).thenThrow(new DataIntegrityViolationException("Data integrity violation"));

        GeneralException exception = assertThrows(GeneralException.class, () -> companyService.create(createCompanyDto));
        assertEquals(CompanyErrorMessages.DATA_INTEGRITY_ERROR, exception.getMessage());

        verify(companyMapper).toCompany(createCompanyDto);
        verify(companyRepository).save(new Company());
    }

    @Test
    @DisplayName("Test create company with general exception")
    void testCreateCompanyWithGeneralException() {
        CreateCompanyDto createCompanyDto = CreateCompanyDto.builder()
                .name("Test Company")
                .nit("123456789")
                .build();

        when(companyMapper.toCompany(createCompanyDto)).thenReturn(new Company());
        when(companyRepository.save(new Company())).thenThrow(new RuntimeException("Unexpected error"));

        GeneralException exception = assertThrows(GeneralException.class, () -> companyService.create(createCompanyDto));
        assertEquals("Unexpected error", exception.getMessage());

        verify(companyMapper).toCompany(createCompanyDto);
        verify(companyRepository).save(new Company());
    }

    @Test
    @DisplayName("Test get company by ID with valid ID")
    void testGetCompanyByIdWithValidId() {
        Long companyId = 1L;
        Company company = new Company();
        company.setId(companyId);
        company.setName("Test Company");
        company.setNit("123456789");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMapper.toCompanyDto(company)).thenReturn(CompanyDto.builder()
                .id(company.getId())
                .name(company.getName())
                .nit(company.getNit())
                .build());

        CompanyDto actualCompanyDto = companyService.getById(companyId);

        assertEquals(companyId, actualCompanyDto.getId());
        assertEquals("Test Company", actualCompanyDto.getName());
        assertEquals("123456789", actualCompanyDto.getNit());

        verify(companyRepository).findById(companyId);
        verify(companyMapper).toCompanyDto(company);
    }

    @Test
    @DisplayName("Test get company by ID with invalid ID")
    void testGetCompanyByIdWithInvalidId() {
        Long companyId = 999L;

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        GeneralException exception = assertThrows(GeneralException.class, () -> companyService.getById(companyId));
        assertEquals(CompanyErrorMessages.COMPANY_NOT_FOUND, exception.getMessage());

        verify(companyRepository).findById(companyId);
    }

    @Test
    @DisplayName("Test get company by ID with general exception")
    void testGetCompanyByIdWithGeneralException() {
        Long companyId = 1L;

        when(companyRepository.findById(companyId)).thenThrow(new RuntimeException("Unexpected error"));

        GeneralException exception = assertThrows(GeneralException.class, () -> companyService.getById(companyId));
        assertEquals("Unexpected error", exception.getMessage());

        verify(companyRepository).findById(companyId);
    }

    @Test
    @DisplayName("Test get all companies with valid pagination")
    void testGetAllCompaniesWithValidPagination() {
        PaginationQueryDto paginationQueryDto = PaginationQueryDto.builder()
                .page(1)
                .size(10)
                .search("Test")
                .build();

        Company company = new Company();
        company.setId(1L);
        company.setName("Test Company");
        company.setNit("123456789");

        Page<Company> page = new PageImpl<>(List.of(company), PageRequest.of(0, 10, Sort.Direction.ASC, "name"), 1);

        when(companyRepository.findByNameContainingIgnoreCaseOrNitContainingIgnoreCase(
                paginationQueryDto.getSearch(),
                paginationQueryDto.getSearch(),
                PageRequest.of(0, 10, Sort.Direction.ASC, "name")))
                .thenReturn(page);

        when(companyMapper.toCompanyDtoList(List.of(company)))
                .thenReturn(List.of(CompanyDto.builder()
                        .id(company.getId())
                        .name(company.getName())
                        .nit(company.getNit())
                        .build()));

        PaginatedResponseDto<CompanyDto> response = companyService.getAll(paginationQueryDto);

        assertEquals(1, response.getData().size());
        assertEquals("Test Company", response.getData().get(0).getName());
        assertEquals("123456789", response.getData().get(0).getNit());
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertEquals(10, response.getPageSize());
        assertEquals(1, response.getCurrentPage());

        verify(companyRepository).findByNameContainingIgnoreCaseOrNitContainingIgnoreCase(
                paginationQueryDto.getSearch(),
                paginationQueryDto.getSearch(),
                PageRequest.of(0, 10, Sort.Direction.ASC, "name"));
        verify(companyMapper).toCompanyDtoList(List.of(company));
    }

    @Test
    @DisplayName("Test get all companies with general exception")
    void testGetAllCompaniesWithGeneralException() {
        PaginationQueryDto paginationQueryDto = PaginationQueryDto.builder()
                .page(1)
                .size(10)
                .search("Test")
                .build();

        when(companyRepository.findByNameContainingIgnoreCaseOrNitContainingIgnoreCase(
                paginationQueryDto.getSearch(),
                paginationQueryDto.getSearch(),
                PageRequest.of(0, 10, Sort.Direction.ASC, "name")))
                .thenThrow(new RuntimeException("Unexpected error"));

        GeneralException exception = assertThrows(GeneralException.class, () -> companyService.getAll(paginationQueryDto));
        assertEquals("Unexpected error", exception.getMessage());

        verify(companyRepository).findByNameContainingIgnoreCaseOrNitContainingIgnoreCase(
                paginationQueryDto.getSearch(),
                paginationQueryDto.getSearch(),
                PageRequest.of(0, 10, Sort.Direction.ASC, "name"));
    }

    @Test
    @DisplayName("Test update company with valid data")
    void testUpdateCompanyWithValidData() {
        Long companyId = 1L;
        UpdateCompanyDto updateCompanyDto = UpdateCompanyDto.builder()
                .name("Updated Company")
                .nit("987654321")
                .build();

        Company existingCompany = new Company();
        existingCompany.setId(companyId);
        existingCompany.setName("Test Company");
        existingCompany.setNit("123456789");

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(existingCompany));
        when(companyMapper.toCompanyDto(existingCompany)).thenReturn(CompanyDto.builder()
                .id(existingCompany.getId())
                .name(existingCompany.getName())
                .nit(existingCompany.getNit())
                .build());

        when(companyMapper.toCompany(CompanyDto.builder()
                .id(existingCompany.getId())
                .name(updateCompanyDto.getName())
                .nit(updateCompanyDto.getNit())
                .build())).thenReturn(new Company());

        when(companyRepository.save(new Company())).thenReturn(new Company());
        when(companyMapper.toCompanyDto(new Company())).thenReturn(CompanyDto.builder()
                .id(existingCompany.getId())
                .name(updateCompanyDto.getName())
                .nit(updateCompanyDto.getNit())
                .build());

        CompanyDto updatedCompany = companyService.update(companyId, updateCompanyDto);

        assertEquals("Updated Company", updatedCompany.getName());
        assertEquals("987654321", updatedCompany.getNit());
        assertEquals(companyId, updatedCompany.getId());

        verify(companyRepository).findById(companyId);
        verify(companyMapper).toCompanyDto(existingCompany);
        verify(companyMapper).toCompany(CompanyDto.builder()
                .id(existingCompany.getId())
                .name(updateCompanyDto.getName())
                .nit(updateCompanyDto.getNit())
                .build());
        verify(companyRepository).save(new Company());
        verify(companyMapper).toCompanyDto(new Company());
    }

    @Test
    @DisplayName("Test update company with DataIntegrityViolationException")
    void testUpdateCompanyWithDataIntegrityViolationException() {
        Long companyId = 1L;
        UpdateCompanyDto updateCompanyDto = UpdateCompanyDto.builder()
                .name("Updated Company")
                .nit("987654321")
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(new Company()));
        when(companyMapper.toCompanyDto(new Company())).thenReturn(CompanyDto.builder()
                .id(companyId)
                .name("Test Company")
                .nit("123456789")
                .build());

        when(companyMapper.toCompany(CompanyDto.builder()
                .id(companyId)
                .name(updateCompanyDto.getName())
                .nit(updateCompanyDto.getNit())
                .build())).thenReturn(new Company());

        when(companyRepository.save(new Company())).thenThrow(new DataIntegrityViolationException("Data integrity violation"));

        GeneralException exception = assertThrows(GeneralException.class, () -> companyService.update(companyId, updateCompanyDto));
        assertEquals(CompanyErrorMessages.DATA_INTEGRITY_ERROR, exception.getMessage());

        verify(companyRepository).findById(companyId);
        verify(companyMapper).toCompanyDto(new Company());
        verify(companyMapper).toCompany(CompanyDto.builder()
                .id(companyId)
                .name(updateCompanyDto.getName())
                .nit(updateCompanyDto.getNit())
                .build());
        verify(companyRepository).save(new Company());
    }

    @Test
    @DisplayName("Test update company with general exception")
    void testUpdateCompanyWithGeneralException() {
        Long companyId = 1L;
        UpdateCompanyDto updateCompanyDto = UpdateCompanyDto.builder()
                .name("Updated Company")
                .nit("987654321")
                .build();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(new Company()));
        when(companyMapper.toCompanyDto(new Company())).thenReturn(CompanyDto.builder()
                .id(companyId)
                .name("Test Company")
                .nit("123456789")
                .build());

        when(companyMapper.toCompany(CompanyDto.builder()
                .id(companyId)
                .name(updateCompanyDto.getName())
                .nit(updateCompanyDto.getNit())
                .build())).thenReturn(new Company());

        when(companyRepository.save(new Company())).thenThrow(new RuntimeException("Unexpected error"));

        GeneralException exception = assertThrows(GeneralException.class, () -> companyService.update(companyId, updateCompanyDto));
        assertEquals("Unexpected error", exception.getMessage());

        verify(companyRepository).findById(companyId);
        verify(companyMapper).toCompanyDto(new Company());
        verify(companyMapper).toCompany(CompanyDto.builder()
                .id(companyId)
                .name(updateCompanyDto.getName())
                .nit(updateCompanyDto.getNit())
                .build());
        verify(companyRepository).save(new Company());
    }

    @Test
    @DisplayName("Test delete company with valid ID")
    void testDeleteCompanyWithValidId() {
        Long companyId = 1L;

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(new Company()));

        companyService.delete(companyId);

        verify(companyRepository).findById(companyId);
        verify(companyRepository).deleteById(companyId);
    }

    @Test
    @DisplayName("Test delete company with invalid ID")
    void testDeleteCompanyWithInvalidId() {
        Long companyId = 999L;

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        GeneralException exception = assertThrows(GeneralException.class, () -> companyService.delete(companyId));
        assertEquals(CompanyErrorMessages.COMPANY_NOT_FOUND, exception.getMessage());

        verify(companyRepository).findById(companyId);
    }

    @Test
    @DisplayName("Test delete company with general exception")
    void testDeleteCompanyWithGeneralException() {
        Long companyId = 1L;

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(new Company()));
        doThrow(new RuntimeException("Unexpected error")).when(companyRepository).deleteById(companyId);

        GeneralException exception = assertThrows(GeneralException.class, () -> companyService.delete(companyId));
        assertEquals("Unexpected error", exception.getMessage());

        verify(companyRepository).findById(companyId);
    }

}
