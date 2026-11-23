package com.example.eleicao_service.feature.eleicao.controller.impl;


import com.example.eleicao_service.feature.eleicao.controller.EleicaoController;
import com.example.eleicao_service.feature.eleicao.model.dto.EleicaoRequest;
import com.example.eleicao_service.feature.eleicao.model.dto.EleicaoResponse;
import com.example.eleicao_service.feature.eleicao.service.EleicaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EleicaoControllerImpl implements EleicaoController {

    private final EleicaoService eleicaoService;

    @Override
    public ResponseEntity<EleicaoResponse> cadastrar(EleicaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eleicaoService.cadastrar(request));
    }

    @Override
    public ResponseEntity<EleicaoResponse> abrirVotacao(Long id) {
        return ResponseEntity.ok(eleicaoService.abrirVotacao(id));
    }

    @Override
    public ResponseEntity<EleicaoResponse> encerrarVotacao(Long id) {
        return ResponseEntity.ok(eleicaoService.encerrarVotacao(id));
    }

    @Override
    public ResponseEntity<EleicaoResponse> consultarPorId(Long id) {
        return ResponseEntity.ok(eleicaoService.consultarPorId(id));
    }

    @Override
    public ResponseEntity<List<EleicaoResponse>> listarTodas() {
        return ResponseEntity.ok(eleicaoService.listarTodas());
    }
}