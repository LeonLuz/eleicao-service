package com.example.eleicao_service.feature.eleitor.model.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.br.CPF;

public record EleitorInputDTO(

        @NotBlank(message = "CPF é obrigatório")
        @NotEmpty
        @CPF
        String cpf,

        @NotBlank(message = "titulo de eleitor é obrigatório")
        @NotEmpty
        String titulo,

        @NotBlank(message = "Nome é obrigatório")
        String nome
){}