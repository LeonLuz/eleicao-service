package com.example.eleicao_service.feature.candidato.model.dto;

public record CandidatoResponse(
        Long idUsuario,
        String nome,
        String cpf,
        Integer numeroEleitoral,
        String partidoSigla,
        String cargoNome,
        Long idEleicao
) {}