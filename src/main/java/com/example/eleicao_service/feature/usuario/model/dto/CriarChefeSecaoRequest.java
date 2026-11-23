package com.example.eleicao_service.feature.usuario.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarChefeSecaoRequest(
        @NotBlank String nome,
        @NotBlank String cpf,
        @NotBlank String username,

        @NotBlank
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String senha
) {}