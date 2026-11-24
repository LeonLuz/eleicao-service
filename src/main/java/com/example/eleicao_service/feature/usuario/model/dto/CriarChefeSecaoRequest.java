package com.example.eleicao_service.feature.usuario.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record CriarChefeSecaoRequest(
        @NotBlank String nome,

        @NotBlank
        @CPF(message = "CPF inválido")
        String cpf,

        @NotBlank String username,

        @NotBlank
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String senha
) {
    public String cpfLimpo() {
        return cpf != null ? cpf.replaceAll("\\D", "") : null;
    }
}
