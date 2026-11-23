package com.example.eleicao_service.feature.votacao.controller;

import com.example.eleicao_service.feature.votacao.model.dto.ApuracaoCargoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.ResultadoFinalCargoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.VotoConfirmacaoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.VotoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Votação e Resultados", description = "Registro de votos sigilosos, acompanhamento em tempo real e resultado final")
@RequestMapping("/api/votacao")
public interface VotacaoController {

    @Operation(summary = "Registrar e confirmar voto", description = "Restrito a Eleitores. Impede voto duplicado por cargo e garante sigilo.")
    @PreAuthorize("hasRole('ELEITOR')")
    @PostMapping("/votar")
    ResponseEntity<VotoConfirmacaoResponse> votar(Authentication authentication, @Valid @RequestBody VotoRequest request);

    @Operation(summary = "Acompanhar evolução da votação", description = "Eleitores veem todos os cargos; Candidatos veem apenas o cargo ao qual concorrem.")
    @GetMapping("/eleicao/{idEleicao}/acompanhamento")
    ResponseEntity<List<ApuracaoCargoResponse>> acompanharVotacao(Authentication authentication, @PathVariable Long idEleicao);

    @Operation(summary = "Consultar resultado final", description = "Disponível apenas após o encerramento da eleição, exibindo votos e o mais votado por cargo.")
    @GetMapping("/eleicao/{idEleicao}/resultado-final")
    ResponseEntity<List<ResultadoFinalCargoResponse>> resultadoFinal(@PathVariable Long idEleicao);
}