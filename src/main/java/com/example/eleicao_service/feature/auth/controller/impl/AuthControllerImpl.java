package com.example.eleicao_service.feature.auth.controller.impl;

import com.example.eleicao_service.feature.auth.controller.AuthController;
import com.example.eleicao_service.feature.auth.dto.LoginRequest;
import com.example.eleicao_service.feature.auth.dto.LoginResponse;
import com.example.eleicao_service.feature.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
