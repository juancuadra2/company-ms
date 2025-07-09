package com.jcuadrado.company.services;

import com.jcuadrado.company.dtos.*;

public interface CompanyService {
    CompanyDto create(CreateCompanyDto createCompanyDto);
    PaginatedResponseDto<CompanyDto> getAll(PaginationQueryDto paginationQueryDto);
    CompanyDto getById(Long id);
    CompanyDto update(Long id, UpdateCompanyDto updateCompanyDto);
    void delete(Long id);
}
