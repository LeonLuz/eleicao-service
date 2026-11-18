package com.example.eleicao_service.feature.chefe_secao.service;

import com.example.eleicao_service.feature.chefe_secao.mapper.ChefeSecaoMapper;
import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoDTO;
import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoInputDTO;
import com.example.eleicao_service.feature.chefe_secao.model.entity.ChefeSecaoModel;
import com.example.eleicao_service.feature.chefe_secao.repository.ChefeSecaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChefeSecaoServiceImpl implements ChefeSecaoService {

    private final ChefeSecaoRepository chefeSecaoRepository;
    private final ChefeSecaoMapper chefeSecaoMapper;

    @Override
    @Transactional(readOnly = true)
    public ChefeSecaoDTO findById(Integer id){

        ChefeSecaoModel chefeSecaoModel = findOrFail(id);
        return chefeSecaoMapper.toDTO(chefeSecaoModel);
    }

    @Override
    @Transactional
    public ChefeSecaoDTO update(ChefeSecaoInputDTO chefeSecaoInputDTO, Integer id) {

        ChefeSecaoModel chefeSecaoModel = findOrFail(id);

        chefeSecaoMapper.copyToEntity(chefeSecaoInputDTO, chefeSecaoModel);
        chefeSecaoModel = chefeSecaoRepository.save(chefeSecaoModel);
        return chefeSecaoMapper.toDTO(chefeSecaoModel);
    }

    private ChefeSecaoModel findOrFail(Integer id){
        return chefeSecaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
