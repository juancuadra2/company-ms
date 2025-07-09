package com.jcuadrado.company.services.impl;

import com.jcuadrado.company.dtos.AuthDto;
import com.jcuadrado.company.dtos.LoginRequestDto;
import com.jcuadrado.company.services.AuthService;
import com.jcuadrado.company.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthDto login(LoginRequestDto request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails user = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateTokenFromUsername(user);
        return AuthDto.builder()
                .token(token)
                .build();
    }
}
