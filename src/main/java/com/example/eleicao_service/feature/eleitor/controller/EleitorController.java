package com.example.eleicao_service.feature.eleitor.controller;


import com.example.eleicao_service.feature.eleitor.model.dto.EleitorRequest;
import com.example.eleicao_service.feature.eleitor.model.dto.EleitorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Eleitores", description = "Gerenciamento de eleitores (Restrito ao Chefe da Seção)")
@RequestMapping("/api/eleitores")
@PreAuthorize("hasRole('CHEFE_SECAO')")
public interface EleitorController {

    @Operation(summary = "Cadastrar eleitor")
    @PostMapping
    ResponseEntity<EleitorResponse> cadastrar(@Valid @RequestBody EleitorRequest request);

    @Operation(summary = "Alterar dados do eleitor", description = "Permite ao Chefe da Seção alterar todos os dados, incluindo Nome e CPF.")
    @PutMapping("/{id}")
    ResponseEntity<EleitorResponse> atualizar(@PathVariable Long id, @Valid @RequestBody EleitorRequest request);

    @Operation(summary = "Consultar eleitor por ID")
    @GetMapping("/{id}")
    ResponseEntity<EleitorResponse> consultarPorId(@PathVariable Long id);

    @Operation(summary = "Listar todos os eleitores")
    @GetMapping
    ResponseEntity<List<EleitorResponse>> listarTodos();

    @Operation(summary = "Excluir eleitor")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> excluir(@PathVariable Long id);
}