package com.example.eleicao_service.feature.eleicao.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record EleicaoRequest(
        @NotBlank String descricao,
        @NotNull LocalDate dataEleicao,
        @NotNull Integer turno,
        @NotNull LocalTime horarioAbertura,
        @NotNull LocalTime horarioEncerramento
) {}