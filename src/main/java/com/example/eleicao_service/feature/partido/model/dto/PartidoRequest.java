package com.example.eleicao_service.feature.partido.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PartidoRequest(
        @NotBlank
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank
        @Size(max = 20, message = "Sigla deve ter no máximo 20 caracteres")
        String sigla,

        @NotNull
        @Positive(message = "Número do partido deve ser positivo")
        Integer numero
){}