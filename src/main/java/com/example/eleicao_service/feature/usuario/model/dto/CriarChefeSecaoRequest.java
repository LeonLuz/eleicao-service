package com.example.eleicao_service.feature.usuario.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record CriarChefeSecaoRequest(
        @NotBlank
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        String nome,

        @NotBlank
        @CPF(message = "CPF inválido")
        String cpf,

        @NotBlank
        @Size(max = 150, message = "Username deve ter no máximo 150 caracteres")
        String username,

        @NotBlank
        @Size(min = 6, max = 255, message = "A senha deve ter no mínimo 6 caracteres")
        String senha
) {
    public String cpfLimpo() {
        return cpf != null ? cpf.replaceAll("\\D", "") : null;
    }
}
