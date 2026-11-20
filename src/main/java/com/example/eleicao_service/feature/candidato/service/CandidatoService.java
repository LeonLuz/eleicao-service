package com.example.eleicao_service.feature.candidato.service;

import com.example.eleicao_service.feature.candidato.model.Cargo;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoDTO;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoInputDTO;

import java.util.List;

public interface CandidatoService {

    List<CandidatoDTO> findAll(Cargo cargo);

    CandidatoDTO findById(Integer id);

    CandidatoDTO insert(CandidatoInputDTO candidatoInputDTO);

    CandidatoDTO update(CandidatoInputDTO candidatoInputDTO, Integer id);

    CandidatoDTO delete(Integer id);
}
