package com.example.eleicao_service.feature.usuario.model.dto;

import jakarta.validation.constraints.NotBlank;

import com.example.eleicao_service.feature.usuario.model.Role;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public record UsuarioInputDTO(
        @NotBlank(message = "Nome de usuário é obrigatório")
        String username,

        @NotBlank(message = "Senha é obrigatório")
        @Size(min = 5, message = "A senha deve ter no mínimo 5 dígitos")
        String password,

        List<Role> roles
) {}
