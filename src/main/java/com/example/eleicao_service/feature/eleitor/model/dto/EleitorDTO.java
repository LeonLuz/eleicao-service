package com.example.eleicao_service.feature.eleitor.model.dto;
public record EleitorDTO(
        Integer id,
        String cpf,
        String titulo,
        String nome
){}