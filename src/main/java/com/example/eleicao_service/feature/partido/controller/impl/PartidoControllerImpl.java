package com.example.eleicao_service.feature.partido.controller.impl;


import com.example.eleicao_service.feature.partido.controller.PartidoController;
import com.example.eleicao_service.feature.partido.model.dto.PartidoRequest;
import com.example.eleicao_service.feature.partido.model.dto.PartidoResponse;
import com.example.eleicao_service.feature.partido.service.PartidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PartidoControllerImpl implements PartidoController {

    private final PartidoService partidoService;

    @Override
    public ResponseEntity<PartidoResponse> cadastrar(PartidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partidoService.cadastrar(request));
    }

    @Override
    public ResponseEntity<List<PartidoResponse>> listarTodos() {
        return ResponseEntity.ok(partidoService.listarTodos());
    }
}