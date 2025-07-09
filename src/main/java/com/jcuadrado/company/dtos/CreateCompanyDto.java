package com.jcuadrado.company.dtos;

import com.jcuadrado.company.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCompanyDto {
    @NotBlank(message = ValidationMessages.NAME_REQUIRED)
    @Size(min = 3, max = 100, message = ValidationMessages.NAME_SIZE)
    private String name;

    @NotBlank(message = ValidationMessages.NIT_REQUIRED)
    @Size(min = 5, max = 20, message = ValidationMessages.NIT_SIZE)
    private String nit;

    private String address;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = ValidationMessages.PHONE_PATTERN)
    private String phone;
}
