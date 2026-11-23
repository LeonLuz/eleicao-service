package com.example.eleicao_service.feature.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank
        @Size(max=150, message = "Username deve ter no máximo 150 caracteres")
        String username,

        @NotBlank String senha
) {}