package com.jcuadrado.company.repositories;

import com.jcuadrado.company.entities.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Page<Company> findByNameContainingIgnoreCaseOrNitContainingIgnoreCase(String name, String nit, Pageable pageable);
    Optional<Company> findByNit(String nit);
}
