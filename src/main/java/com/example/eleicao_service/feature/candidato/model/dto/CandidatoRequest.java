package com.example.eleicao_service.feature.candidato.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CandidatoRequest(
        @NotBlank String nome,
        @NotBlank @Size(min = 11, max = 11) String cpf,
        @NotBlank String username,
        String senha,
        @NotNull Integer numeroEleitoral,
        @NotNull Long idPartido,
        @NotNull Long idCargo,
        @NotNull Long idEleicao
) {}