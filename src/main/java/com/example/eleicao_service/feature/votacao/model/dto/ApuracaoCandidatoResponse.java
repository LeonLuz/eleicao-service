package com.example.eleicao_service.feature.votacao.model.dto;

public record ApuracaoCandidatoResponse(
        Long idCandidato,
        String nomeCandidato,
        Integer numeroEleitoral,
        String partidoSigla,
        Long totalVotos
) {}