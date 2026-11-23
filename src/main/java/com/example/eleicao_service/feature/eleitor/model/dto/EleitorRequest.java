package com.example.eleicao_service.feature.eleitor.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EleitorRequest(
        @NotBlank String nome,
        @NotBlank @Size(min = 11, max = 11) String cpf,
        @NotBlank String username,
        String senha,
        @NotBlank String tituloEleitor,
        @NotBlank String zona,
        @NotBlank String secao
) {}