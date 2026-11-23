package com.example.eleicao_service.feature.auth.service;

import com.example.eleicao_service.feature.auth.dto.LoginRequest;
import com.example.eleicao_service.feature.auth.dto.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}