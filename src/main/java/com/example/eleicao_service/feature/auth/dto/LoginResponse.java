package com.example.eleicao_service.feature.auth.dto;

import java.util.Set;

public record LoginResponse(
        String token,
        String tipo,
        Set<String> perfis,
        String nome
) {}