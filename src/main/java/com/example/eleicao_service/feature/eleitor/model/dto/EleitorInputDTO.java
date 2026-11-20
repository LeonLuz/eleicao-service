package com.example.eleicao_service.feature.eleitor.model.dto;
import jakarta.validation.constraints.NotBlank;
public record EleitorInputDTO(

        @NotBlank
        String cpf,

        @NotBlank
        String titulo,

        @NotBlank
        String nome
){}