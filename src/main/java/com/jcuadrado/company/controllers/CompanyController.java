package com.jcuadrado.company.controllers;

import com.jcuadrado.company.dtos.CreateCompanyDto;
import com.jcuadrado.company.dtos.PaginationQueryDto;
import com.jcuadrado.company.dtos.UpdateCompanyDto;
import com.jcuadrado.company.services.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateCompanyDto createCompanyDto){
        return new ResponseEntity<>(companyService.create(createCompanyDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "isActive", required = false, defaultValue = "true") Boolean isActive) {
        PaginationQueryDto queryDto = new PaginationQueryDto(search, page, size, isActive);
        return ResponseEntity.ok(companyService.getAll(queryDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateCompanyDto updateCompanyDto) {
        return ResponseEntity.ok(companyService.update(id, updateCompanyDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
