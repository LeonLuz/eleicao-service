package com.example.eleicao_service.feature.votacao.model.dto;

import jakarta.validation.constraints.NotNull;

public record VotoRequest(
        @NotNull Long idEleicao,
        @NotNull Long idCargo,
        @NotNull Long idCandidato
) {}