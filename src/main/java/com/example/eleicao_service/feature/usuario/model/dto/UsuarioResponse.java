package com.example.eleicao_service.feature.usuario.model.dto;

import com.example.eleicao_service.feature.usuario.model.Perfil;
import java.util.Set;

public record UsuarioResponse(
        Long id,
        String nome,
        String cpf,
        String username,
        Set<Perfil> perfis,
        Boolean ativo
){}