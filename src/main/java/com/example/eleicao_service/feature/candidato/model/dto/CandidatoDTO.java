package com.example.eleicao_service.feature.candidato.model.dto;

import com.example.eleicao_service.feature.candidato.model.Cargo;

public record CandidatoDTO(
        Integer id,
        String nome,
        Integer numero,
        String partido,
        Cargo cargo
){}
