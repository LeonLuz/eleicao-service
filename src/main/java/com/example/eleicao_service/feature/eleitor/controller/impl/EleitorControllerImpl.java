package com.example.eleicao_service.feature.eleitor.controller.impl;


import com.example.eleicao_service.feature.eleitor.controller.EleitorController;
import com.example.eleicao_service.feature.eleitor.model.dto.EleitorRequest;
import com.example.eleicao_service.feature.eleitor.model.dto.EleitorResponse;
import com.example.eleicao_service.feature.eleitor.service.EleitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EleitorControllerImpl implements EleitorController {

    private final EleitorService eleitorService;

    @Override
    public ResponseEntity<EleitorResponse> cadastrar(EleitorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eleitorService.cadastrar(request));
    }

    @Override
    public ResponseEntity<EleitorResponse> atualizar(Long id, EleitorRequest request) {
        return ResponseEntity.ok(eleitorService.atualizar(id, request));
    }

    @Override
    public ResponseEntity<EleitorResponse> consultarPorId(Long id) {
        return ResponseEntity.ok(eleitorService.consultarPorId(id));
    }

    @Override
    public ResponseEntity<List<EleitorResponse>> listarTodos() {
        return ResponseEntity.ok(eleitorService.listarTodos());
    }

    @Override
    public ResponseEntity<Void> excluir(Long id) {
        eleitorService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}