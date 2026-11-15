package com.example.eleicao_service.feature.usuario.model.dto;

import com.example.eleicao_service.feature.usuario.model.Role;

import java.util.List;

public record UsuarioDTO(
        Integer id,
        String username,
        List<Role> roles
){}
