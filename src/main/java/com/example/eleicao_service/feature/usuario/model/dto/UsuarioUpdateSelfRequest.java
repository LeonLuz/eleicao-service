package com.example.eleicao_service.feature.usuario.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateSelfRequest(
        @NotBlank
        @Size(max = 150, message = "Username deve ter no máximo 150 caracteres")
        String username,

        @Pattern(regexp = "^$|.{6,}", message = "A senha deve ter no mínimo 6 caracteres")
        String novaSenha
) {}