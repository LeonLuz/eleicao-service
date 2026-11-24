package com.example.eleicao_service.feature.candidato.model.dto;

public record CandidatoResponse(
        Long idUsuario,
        String nome,
        Integer numeroEleitoral,
        String partidoSigla,
        String cargoNome,
        Long idEleicao
) {}