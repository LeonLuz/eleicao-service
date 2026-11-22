package com.example.eleicao_service.feature.candidato.mapper;

import com.example.eleicao_service.core.mapper.BaseMapper;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoDTO;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoInputDTO;
import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CandidatoMapper extends BaseMapper<CandidatoInputDTO, CandidatoModel, CandidatoDTO> {

    @Override
    @Mapping(target = "numeroCompleto", expression = "java(entity.getNumeroCompleto())")
    CandidatoDTO toDTO(CandidatoModel entity);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "cargo", ignore = true)
    @Mapping(target = "partido", ignore = true)
    CandidatoModel toEntity(CandidatoInputDTO input);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "cargo", ignore = true)
    @Mapping(target = "partido", ignore = true)
    void copyToEntity(CandidatoInputDTO input, @MappingTarget CandidatoModel entity);
}