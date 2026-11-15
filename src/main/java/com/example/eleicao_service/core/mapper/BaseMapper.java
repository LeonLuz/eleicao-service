package com.example.eleicao_service.core.mapper;

import org.mapstruct.MappingTarget;

public interface BaseMapper<I,E,O>{
    O toDTO(E entity);

    E toEntity(I input);

    void copyToEntity(I entrada, @MappingTarget E entity);

}