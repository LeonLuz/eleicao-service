package com.example.eleicao_service.feature.chefe_secao.mapper;

import com.example.eleicao_service.core.mapper.BaseMapper;
import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoDTO;
import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoInputDTO;
import com.example.eleicao_service.feature.chefe_secao.model.entity.ChefeSecaoModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ChefeSecaoMapper extends BaseMapper<ChefeSecaoInputDTO, ChefeSecaoModel, ChefeSecaoDTO> {

    @Override
    ChefeSecaoDTO toDTO(ChefeSecaoModel entity);

    @Override
    ChefeSecaoModel toEntity(ChefeSecaoInputDTO input);

    @Override
    void copyToEntity(ChefeSecaoInputDTO input, @MappingTarget ChefeSecaoModel entity);
}
