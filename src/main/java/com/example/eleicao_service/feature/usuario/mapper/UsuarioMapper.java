package com.example.eleicao_service.feature.usuario.mapper;

import com.example.eleicao_service.core.mapper.BaseMapper;
import com.example.eleicao_service.feature.candidato.mapper.CandidatoMapper;
import com.example.eleicao_service.feature.chefe_secao.mapper.ChefeSecaoMapper;
import com.example.eleicao_service.feature.eleitor.mapper.EleitorMapper;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioDTO;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioInputDTO;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ChefeSecaoMapper.class, EleitorMapper.class, CandidatoMapper.class})
public interface UsuarioMapper extends BaseMapper<UsuarioInputDTO, UsuarioModel, UsuarioDTO> {


    @Override
    UsuarioDTO toDTO(UsuarioModel model);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", constant = "true")
    @Mapping(target = "roles", ignore = true)
    UsuarioModel toEntity(UsuarioInputDTO dto);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void copyToEntity(UsuarioInputDTO dto, @MappingTarget UsuarioModel model);


    @AfterMapping
    default void vincularPerfisERoles(@MappingTarget UsuarioModel model) {
        List<String> roles = new ArrayList<>();
        roles.add("USER");

        if (model.getChefeSecao() != null) {
            model.getChefeSecao().setUsuario(model);
            roles.add("CHEFE_SECAO");
        }

        if (model.getEleitor() != null) {
            model.getEleitor().setUsuario(model);
            roles.add("ELEITOR");
        }

        if (model.getCandidato() != null) {
            model.getCandidato().setUsuario(model);
            roles.add("CANDIDATO");
        }

        model.setRoles(roles);
    }
}