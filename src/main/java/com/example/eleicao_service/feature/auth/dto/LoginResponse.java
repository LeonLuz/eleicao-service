package com.example.eleicao_service.feature.auth.dto;

public record LoginResponse(
        String token,
        String tipo,
        String perfil,
        String nome
) {}