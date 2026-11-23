package com.example.eleicao_service.feature.votacao.controller.impl;

import com.example.eleicao_service.feature.votacao.controller.VotacaoController;
import com.example.eleicao_service.feature.votacao.model.dto.ApuracaoCargoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.ResultadoFinalCargoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.VotoConfirmacaoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.VotoRequest;
import com.example.eleicao_service.feature.votacao.service.VotacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VotacaoControllerImpl implements VotacaoController {

    private final VotacaoService votacaoService;

    @Override
    public ResponseEntity<VotoConfirmacaoResponse> votar(Authentication authentication, VotoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(votacaoService.registrarVoto(authentication.getName(), request));
    }

    @Override
    public ResponseEntity<List<ApuracaoCargoResponse>> acompanharVotacao(Authentication authentication, Long idEleicao) {
        return ResponseEntity.ok(votacaoService.acompanharVotacao(authentication.getName(), idEleicao));
    }

    @Override
    public ResponseEntity<List<ResultadoFinalCargoResponse>> resultadoFinal(Long idEleicao) {
        return ResponseEntity.ok(votacaoService.consultarResultadoFinal(idEleicao));
    }
}