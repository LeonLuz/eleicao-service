package com.example.eleicao_service.feature.partido.controller;


import com.example.eleicao_service.feature.partido.model.dto.PartidoRequest;
import com.example.eleicao_service.feature.partido.model.dto.PartidoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Partidos", description = "Gerenciamento de partidos políticos")
@RequestMapping("/api/partidos")
public interface PartidoController {

    @Operation(summary = "Cadastrar partido", description = "Restrito ao Chefe da Seção.")
    @PreAuthorize("hasRole('CHEFE_SECAO')")
    @PostMapping
    ResponseEntity<PartidoResponse> cadastrar(@Valid @RequestBody PartidoRequest request);

    @Operation(summary = "Listar partidos")
    @GetMapping
    ResponseEntity<List<PartidoResponse>> listarTodos();
}