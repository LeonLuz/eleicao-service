package com.example.eleicao_service.feature.usuario.model.dto;

import jakarta.validation.constraints.NotBlank;

public record UsuarioUpdateSelfRequest(
        @NotBlank String username,
        String novaSenha
) {}