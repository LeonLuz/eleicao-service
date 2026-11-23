package com.example.eleicao_service.feature.usuario.model.dto;

import com.example.eleicao_service.feature.usuario.model.Perfil;

public record UsuarioResponse(
        Long id,
        String nome,
        String cpf,
        String username,
        Perfil perfil,
        Boolean ativo
){}