package com.example.eleicao_service.feature.votacao.service;


import com.example.eleicao_service.feature.votacao.model.dto.ApuracaoCargoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.ResultadoFinalCargoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.VotoConfirmacaoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.VotoRequest;

import java.util.List;

public interface VotacaoService {
    VotoConfirmacaoResponse registrarVoto(String usernameEleitor, VotoRequest request);
    List<ApuracaoCargoResponse> acompanharVotacao(String usernameLogado, Long idEleicao);
    List<ResultadoFinalCargoResponse> consultarResultadoFinal(Long idEleicao);
}