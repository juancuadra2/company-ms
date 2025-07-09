package com.jcuadrado.company.services;

import com.jcuadrado.company.dtos.AuthDto;
import com.jcuadrado.company.dtos.LoginRequestDto;

public interface AuthService {
    AuthDto login(LoginRequestDto loginRequestDto);
}
