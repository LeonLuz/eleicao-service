package com.example.eleicao_service.feature.partido.model.dto;

public record PartidoResponse(
        Long id,
        String nome,
        String sigla,
        Integer numero) {}