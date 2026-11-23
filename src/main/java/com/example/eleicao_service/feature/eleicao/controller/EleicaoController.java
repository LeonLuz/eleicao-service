package com.example.eleicao_service.feature.eleicao.controller;


import com.example.eleicao_service.feature.eleicao.model.dto.EleicaoRequest;
import com.example.eleicao_service.feature.eleicao.model.dto.EleicaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Eleições", description = "Gerenciamento do ciclo de vida das eleições")
@RequestMapping("/api/eleicoes")
public interface EleicaoController {

    @Operation(summary = "Cadastrar eleição", description = "Restrito ao Chefe da Seção.")
    @PreAuthorize("hasRole('CHEFE_SECAO')")
    @PostMapping
    ResponseEntity<EleicaoResponse> cadastrar(@Valid @RequestBody EleicaoRequest request);

    @Operation(summary = "Abrir votação", description = "Altera o status da eleição de AGENDADA para ABERTA. Restrito ao Chefe da Seção.")
    @PreAuthorize("hasRole('CHEFE_SECAO')")
    @PatchMapping("/{id}/abrir")
    ResponseEntity<EleicaoResponse> abrirVotacao(@PathVariable Long id);

    @Operation(summary = "Encerrar votação", description = "Altera o status da eleição de ABERTA para ENCERRADA. Restrito ao Chefe da Seção.")
    @PreAuthorize("hasRole('CHEFE_SECAO')")
    @PatchMapping("/{id}/encerrar")
    ResponseEntity<EleicaoResponse> encerrarVotacao(@PathVariable Long id);

    @Operation(summary = "Consultar situação da eleição")
    @GetMapping("/{id}")
    ResponseEntity<EleicaoResponse> consultarPorId(@PathVariable Long id);

    @Operation(summary = "Listar todas as eleições")
    @GetMapping
    ResponseEntity<List<EleicaoResponse>> listarTodas();
}