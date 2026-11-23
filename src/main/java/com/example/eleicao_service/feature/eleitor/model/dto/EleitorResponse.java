package com.example.eleicao_service.feature.eleitor.model.dto;

public record EleitorResponse(
        Long idUsuario,
        String nome,
        String cpf,
        String username,
        String tituloEleitor,
        String zona,
        String secao
) {}