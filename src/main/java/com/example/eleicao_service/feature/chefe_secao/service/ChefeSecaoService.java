package com.example.eleicao_service.feature.chefe_secao.service;

import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoDTO;
import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoInputDTO;

import java.util.List;

public interface ChefeSecaoService {

    List<ChefeSecaoDTO> findAll();

    ChefeSecaoDTO findById(Integer id);

    ChefeSecaoDTO insert(ChefeSecaoInputDTO input);

    ChefeSecaoDTO update(ChefeSecaoInputDTO chefeSecaoInputDTO, Integer id);

    ChefeSecaoDTO delete(Integer id);
}