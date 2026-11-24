package com.example.eleicao_service.feature.eleitor.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record EleitorRequest(
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

        @NotBlank(message = "Título de eleitor é obrigatório")
        @Pattern(regexp = "^\\d{12}$", message = "Título de eleitor deve conter exatamente 12 dígitos numéricos")
        @Schema(example = "012345678901")
        String tituloEleitor,

        @NotBlank(message = "Zona é obrigatória")
        @Pattern(regexp = "^\\d{3}$", message = "Zona deve conter exatamente 3 dígitos numéricos")
        @Schema(example = "012")
        String zona,

        @NotBlank(message = "Seção é obrigatória")
        @Pattern(regexp = "^\\d{4}$", message = "Seção deve conter exatamente 4 dígitos numéricos")
        @Schema(example = "0045")
        String secao
) {
    public String cpfLimpo() {
        return cpf != null ? cpf.replaceAll("\\D", "") : null;
    }
}
