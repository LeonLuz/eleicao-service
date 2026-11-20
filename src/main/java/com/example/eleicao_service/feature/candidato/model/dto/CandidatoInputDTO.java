package com.example.eleicao_service.feature.candidato.model.dto;

import com.example.eleicao_service.feature.candidato.model.Cargo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CandidatoInputDTO(

        /** Usuário (com role CANDIDATO) ao qual o candidato será vinculado. Usado apenas no cadastro. */
        @NotNull(message = "Id do usuário é obrigatório")
        Integer idUsuario,

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotNull(message = "Número é obrigatório")
        @Positive(message = "Número deve ser positivo")
        Integer numero,

        @NotBlank(message = "Partido é obrigatório")
        String partido,

        @NotNull(message = "Cargo é obrigatório")
        Cargo cargo
){}
