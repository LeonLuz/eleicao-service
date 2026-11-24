package com.example.eleicao_service.feature.candidato.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record CandidatoRequest(
        @NotBlank
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        String nome,

        @NotBlank
        @CPF(message = "CPF inválido")
        String cpf,

        @NotBlank
        @Size(max = 150, message = "Username deve ter no máximo 150 caracteres")
        String username,

        @Pattern(regexp = "^$|.{6,}", message = "A senha deve ter no mínimo 6 caracteres")
        String senha,

        @NotNull
        @Positive(message = "Número eleitoral deve ser positivo")
        Integer numeroEleitoral,

        @NotNull Long idPartido,
        @NotNull Long idCargo,
        @NotNull Long idEleicao
) {
    public String cpfLimpo(){
        return cpf != null ? cpf.replaceAll("\\D", "") : null;
    }
}