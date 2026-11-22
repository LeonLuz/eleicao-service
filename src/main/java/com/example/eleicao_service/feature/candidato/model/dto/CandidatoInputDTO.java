package com.example.eleicao_service.feature.candidato.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CandidatoInputDTO(

        @NotNull(message = "Id do usuário é obrigatório")
        Integer idUsuario,

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotNull(message = "Id do cargo é obrigatório")
        Integer idCargo,

        @NotNull(message = "Id do partido é obrigatório")
        Integer idPartido,

        @NotNull(message = "Número eleitoral (sufixo) é obrigatório. Use 0 para cargos sem sufixo.")
        @Min(value = 0, message = "Sufixo eleitoral não pode ser negativo")
        @Max(value = 999, message = "Sufixo eleitoral deve ser no máximo 999")
        Integer idEleitoral
) {}