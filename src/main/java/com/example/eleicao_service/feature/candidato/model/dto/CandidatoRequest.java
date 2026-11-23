package com.example.eleicao_service.feature.candidato.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record CandidatoRequest(
        @NotBlank String nome,

        @NotBlank
        @CPF(message = "CPF inválido")
        String cpf,

        @NotBlank String username,
        String senha,
        @NotNull Integer numeroEleitoral,
        @NotNull Long idPartido,
        @NotNull Long idCargo,
        @NotNull Long idEleicao
) {
    public String cpfLimpo(){
        return cpf != null ? cpf.replaceAll("\\D", "") : null;
    }
}