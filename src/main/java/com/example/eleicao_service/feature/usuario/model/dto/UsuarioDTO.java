package com.example.eleicao_service.feature.usuario.model.dto;
import java.util.List;
import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoDTO;
import com.example.eleicao_service.feature.eleitor.model.dto.EleitorDTO;
public record UsuarioDTO(
        Integer id,
        String username,
        List<String> roles,
        Boolean ativo,
        ChefeSecaoDTO chefeSecao,
        EleitorDTO eleitor
) {}