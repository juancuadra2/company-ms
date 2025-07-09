package com.jcuadrado.company.controllers;

import com.jcuadrado.company.dtos.AuthDto;
import com.jcuadrado.company.dtos.LoginRequestDto;
import com.jcuadrado.company.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return new ResponseEntity<>(this.authService.login(loginRequestDto), HttpStatus.OK);
    }

}
