package com.example.eleicao_service.feature.votacao.model.dto;

import java.util.List;

public record ResultadoFinalCargoResponse(
        Long idCargo,
        String nomeCargo,
        boolean empate,
        ApuracaoCandidatoResponse maisVotado,
        List<ApuracaoCandidatoResponse> empatados,
        List<ApuracaoCandidatoResponse> candidatos
) {}