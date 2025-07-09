package com.jcuadrado.company.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class GeneralException extends RuntimeException{
    private HttpStatus httpStatus;
    private String message;
}