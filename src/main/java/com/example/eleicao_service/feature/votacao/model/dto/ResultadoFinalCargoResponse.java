package com.example.eleicao_service.feature.votacao.model.dto;

import java.util.List;

public record ResultadoFinalCargoResponse(
        Long idCargo,
        String nomeCargo,
        ApuracaoCandidatoResponse maisVotado,
        List<ApuracaoCandidatoResponse> candidatos
) {}