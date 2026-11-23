package com.example.eleicao_service.feature.candidato.controller.impl;


import com.example.eleicao_service.feature.candidato.controller.CandidatoController;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoRequest;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoResponse;
import com.example.eleicao_service.feature.candidato.service.CandidatoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CandidatoControllerImpl implements CandidatoController {

    private final CandidatoService candidatoService;

    @Override
    public ResponseEntity<CandidatoResponse> cadastrar(CandidatoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidatoService.cadastrar(request));
    }

    @Override
    public ResponseEntity<CandidatoResponse> atualizar(Long id, CandidatoRequest request) {
        return ResponseEntity.ok(candidatoService.atualizar(id, request));
    }

    @Override
    public ResponseEntity<CandidatoResponse> consultarPorId(Long id) {
        return ResponseEntity.ok(candidatoService.consultarPorId(id));
    }

    @Override
    public ResponseEntity<List<CandidatoResponse>> listarPorEleicaoECargo(Long idEleicao, Long idCargo) {
        return ResponseEntity.ok(candidatoService.listarPorEleicaoECargo(idEleicao, idCargo));
    }

    @Override
    public ResponseEntity<List<CandidatoResponse>> listarTodos() {
        return ResponseEntity.ok(candidatoService.listarTodos());
    }

    @Override
    public ResponseEntity<Void> excluir(Long id) {
        candidatoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}