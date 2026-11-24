package com.example.eleicao_service.feature.eleicao.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public record EleicaoRequest(
        @NotBlank
        @Size(max = 150, message = "Descrição deve ter no máximo 150 caracteres")
        String descricao,

        @NotNull LocalDate dataEleicao,

        @NotNull
        @Min(value = 1, message = "Turno deve ser 1 ou 2")
        @Max(value = 2, message = "Turno deve ser 1 ou 2")
        Integer turno,

        @NotNull LocalTime horarioAbertura,
        @NotNull LocalTime horarioEncerramento
) {}