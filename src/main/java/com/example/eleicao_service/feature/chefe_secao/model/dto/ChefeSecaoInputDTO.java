package com.example.eleicao_service.feature.chefe_secao.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChefeSecaoInputDTO(
        @NotNull(message = "Id do usuário é obrigatório")
        Integer id,

        @NotBlank(message = "Nome é obrigatório")
        String nome
){}
