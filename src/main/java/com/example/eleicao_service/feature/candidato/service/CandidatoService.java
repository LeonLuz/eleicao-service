package com.example.eleicao_service.feature.candidato.service;

import com.example.eleicao_service.feature.candidato.model.dto.CandidatoRequest;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoResponse;

import java.util.List;

public interface CandidatoService {
    CandidatoResponse cadastrar(CandidatoRequest request);
    CandidatoResponse atualizar(Long id, CandidatoRequest request);
    CandidatoResponse consultarPorId(Long id);
    List<CandidatoResponse> listarPorEleicaoECargo(Long idEleicao, Long idCargo);
    List<CandidatoResponse> listarTodos();
    void excluir(Long id);
}