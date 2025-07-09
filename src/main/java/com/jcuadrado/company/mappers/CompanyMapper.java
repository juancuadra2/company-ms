package com.jcuadrado.company.mappers;

import com.jcuadrado.company.dtos.CompanyDto;
import com.jcuadrado.company.dtos.CreateCompanyDto;
import com.jcuadrado.company.dtos.UpdateCompanyDto;
import com.jcuadrado.company.entities.Company;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    CompanyDto toCompanyDto(Company company);
    List<CompanyDto> toCompanyDtoList(List<Company> companyList);
    Company toCompany(CompanyDto companyDto);

    CompanyDto toCompanyDto(CreateCompanyDto createCompanyDto);
    CompanyDto toCompanyDto(UpdateCompanyDto updateCompanyDto);
    Company toCompany(CreateCompanyDto createCompanyDto);
    Company toCompany(UpdateCompanyDto updateCompanyDto);
}
