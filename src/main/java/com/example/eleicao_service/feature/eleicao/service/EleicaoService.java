package com.example.eleicao_service.feature.eleicao.service;


import com.example.eleicao_service.feature.eleicao.model.dto.EleicaoRequest;
import com.example.eleicao_service.feature.eleicao.model.dto.EleicaoResponse;

import java.util.List;

public interface EleicaoService {
    EleicaoResponse cadastrar(EleicaoRequest request);
    EleicaoResponse abrirVotacao(Long id);
    EleicaoResponse encerrarVotacao(Long id);
    EleicaoResponse consultarPorId(Long id);
    List<EleicaoResponse> listarTodas();
}