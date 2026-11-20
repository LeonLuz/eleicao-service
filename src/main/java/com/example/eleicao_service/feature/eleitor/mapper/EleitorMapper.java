package com.example.eleicao_service.feature.eleitor.mapper;
import com.example.eleicao_service.feature.eleitor.model.dto.*;
import com.example.eleicao_service.feature.eleitor.model.entity.EleitorModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EleitorMapper {

    EleitorModel toEntity(EleitorInputDTO dto);
    EleitorDTO toDTO(EleitorModel model);
    void copyToEntity(EleitorInputDTO dto, @MappingTarget EleitorModel model);
}