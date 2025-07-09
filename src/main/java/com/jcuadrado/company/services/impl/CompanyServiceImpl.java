package com.jcuadrado.company.services.impl;

import com.jcuadrado.company.constants.CompanyErrorMessages;
import com.jcuadrado.company.dtos.*;
import com.jcuadrado.company.entities.Company;
import com.jcuadrado.company.exceptions.GeneralException;
import com.jcuadrado.company.mappers.CompanyMapper;
import com.jcuadrado.company.repositories.CompanyRepository;
import com.jcuadrado.company.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    @Override
    public CompanyDto create(CreateCompanyDto createCompanyDto) {
        this.validateNitExists(createCompanyDto.getNit(), null);
        try {
            Company company = companyMapper.toCompany(createCompanyDto);
            Company savedCompany = companyRepository.save(company);
            return companyMapper.toCompanyDto(savedCompany);
        } catch (DataIntegrityViolationException e) {
            throw new GeneralException(HttpStatus.CONFLICT, CompanyErrorMessages.DATA_INTEGRITY_ERROR);
        } catch (Exception e) {
            throw new GeneralException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public PaginatedResponseDto<CompanyDto> getAll(PaginationQueryDto queryDto) {
        try {
            int page = queryDto.getPage() <= 1 ? 0 : queryDto.getPage() - 1;
            Pageable pageable = PageRequest.of(page, queryDto.getSize(), Sort.Direction.ASC, "name");
            Page<Company> companyPage = companyRepository.findByNameContainingIgnoreCaseOrNitContainingIgnoreCase(
                    queryDto.getSearch(),
                    queryDto.getSearch(),
                    pageable
            );
            return PaginatedResponseDto.<CompanyDto>builder()
                    .data(companyMapper.toCompanyDtoList(companyPage.getContent()))
                    .totalElements(companyPage.getTotalElements())
                    .totalPages(companyPage.getTotalPages())
                    .pageSize(companyPage.getSize())
                    .currentPage(companyPage.getNumber() + 1)
                    .build();
        } catch (Exception e) {
            throw new GeneralException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public CompanyDto getById(Long id) {
        Optional<Company> company;
        try {
            company = this.companyRepository.findById(id);
        } catch (Exception e) {
            throw new GeneralException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        if (company.isPresent()) {
            return companyMapper.toCompanyDto(company.get());
        }
        throw new GeneralException(HttpStatus.NOT_FOUND, CompanyErrorMessages.COMPANY_NOT_FOUND);
    }

    @Override
    public CompanyDto update(Long id, UpdateCompanyDto updateCompanyDto) {
        CompanyDto companyDtoFound = this.getById(id);
        validateNitExists(updateCompanyDto.getNit(), id);
        try {
            CompanyDto mergedDto = CompanyDto.builder()
                    .id(companyDtoFound.getId())
                    .name(updateCompanyDto.getName() != null ? updateCompanyDto.getName() : companyDtoFound.getName())
                    .nit(updateCompanyDto.getNit() != null ? updateCompanyDto.getNit() : companyDtoFound.getNit())
                    .address(updateCompanyDto.getAddress() != null ? updateCompanyDto.getAddress() : companyDtoFound.getAddress())
                    .phone(updateCompanyDto.getPhone() != null ? updateCompanyDto.getPhone() : companyDtoFound.getPhone())
                    .build();
            Company company = companyMapper.toCompany(mergedDto);
            Company updatedCompany = this.companyRepository.save(company);
            return companyMapper.toCompanyDto(updatedCompany);
        }catch (DataIntegrityViolationException e){
            throw new GeneralException(HttpStatus.CONFLICT, CompanyErrorMessages.DATA_INTEGRITY_ERROR);
        }catch (Exception e){
            throw new GeneralException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        this.getById(id);
        try {
            companyRepository.deleteById(id);
        } catch (Exception e) {
            throw new GeneralException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void validateNitExists(String nit, Long excludeId) {
        Optional<Company> existingCompany = this.companyRepository.findByNit(nit);
        if (existingCompany.isPresent() && (excludeId == null || !existingCompany.get().getId().equals(excludeId))) {
            throw new GeneralException(HttpStatus.CONFLICT, CompanyErrorMessages.COMPANY_ALREADY_EXISTS);
        }
    }
}
