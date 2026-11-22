package com.example.eleicao_service.feature.candidato.model.dto;

import com.example.eleicao_service.feature.cargo.model.dto.CargoDTO;
import com.example.eleicao_service.feature.partido.model.dto.PartidoDTO;

public record CandidatoDTO(
        Integer id,
        String nome,
        Integer idEleitoral,
        String numeroCompleto,
        PartidoDTO partido,
        CargoDTO cargo
) {}