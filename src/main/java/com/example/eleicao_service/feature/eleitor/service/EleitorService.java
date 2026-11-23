package com.example.eleicao_service.feature.eleitor.service;


import com.example.eleicao_service.feature.eleitor.model.dto.EleitorRequest;
import com.example.eleicao_service.feature.eleitor.model.dto.EleitorResponse;

import java.util.List;

public interface EleitorService {
    EleitorResponse cadastrar(EleitorRequest request);
    EleitorResponse atualizar(Long id, EleitorRequest request);
    EleitorResponse consultarPorId(Long id);
    List<EleitorResponse> listarTodos();
    void excluir(Long id);
}