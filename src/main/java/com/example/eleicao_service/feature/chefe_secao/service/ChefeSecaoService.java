package com.example.eleicao_service.feature.chefe_secao.service;

import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoDTO;
import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoInputDTO;

public interface ChefeSecaoService {

    ChefeSecaoDTO findById(Integer id);

    ChefeSecaoDTO update(ChefeSecaoInputDTO chefeSecaoInputDTO, Integer id);
}
