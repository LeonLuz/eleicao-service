package com.example.eleicao_service.feature.chefe_secao.model.dto;

import jakarta.validation.constraints.NotBlank;

public record ChefeSecaoInputDTO(

        @NotBlank(message = "Nome é obrigatório")
        String nome
){}
