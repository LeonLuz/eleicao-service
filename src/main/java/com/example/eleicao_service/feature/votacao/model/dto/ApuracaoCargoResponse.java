package com.example.eleicao_service.feature.votacao.model.dto;

import java.util.List;

public record ApuracaoCargoResponse(
        Long idCargo,
        String nomeCargo,
        List<ApuracaoCandidatoResponse> candidatos
) {}