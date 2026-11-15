package com.example.eleicao_service.feature.usuario.mapper;

import com.example.eleicao_service.core.mapper.BaseMapper;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioDTO;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioInputDTO;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UsuarioMapper extends BaseMapper<UsuarioInputDTO, UsuarioModel, UsuarioDTO>{

    @Override
    UsuarioDTO toDTO(UsuarioModel entity);

    @Override
    @Mapping(target = "id", ignore = true)
    UsuarioModel toEntity(UsuarioInputDTO input);

    @Override
    @Mapping(target = "id", ignore = true)
    void copyToEntity(UsuarioInputDTO input, @MappingTarget UsuarioModel entity);
}
