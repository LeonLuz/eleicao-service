package com.example.eleicao_service.feature.usuario.model.dto;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoInputDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoInputDTO;
import com.example.eleicao_service.feature.eleitor.model.dto.EleitorInputDTO;
import jakarta.validation.constraints.Size;

public record UsuarioInputDTO(
        @NotBlank(message = "Nome de usuário é obrigatório")
        String username,

        @NotBlank(message = "Senha é obrigatório")
        @Size(min = 5, message = "A senha deve ter no mínimo 5 dígitos")
        String password,

        @Valid
        ChefeSecaoInputDTO chefeSecao,

        @Valid
        EleitorInputDTO eleitor,

        @Valid
        CandidatoInputDTO candidato
) {}
