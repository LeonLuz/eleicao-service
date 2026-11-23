package com.example.eleicao_service.feature.partido.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PartidoRequest(
        @NotBlank String nome,
        @NotBlank String sigla,
        @NotNull Integer numero
){}