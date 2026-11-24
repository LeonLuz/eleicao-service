package com.example.eleicao_service.feature.votacao.controller;

import com.example.eleicao_service.feature.votacao.model.dto.ApuracaoCargoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.ResultadoFinalCargoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.VotoConfirmacaoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.VotoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Voto registrado e confirmado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer perfil ELEITOR)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Eleição, cargo ou candidato não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Regra de negócio violada (ex: eleição não está aberta ou eleitor já votou para este cargo)", content = @Content)
    })
    @PreAuthorize("hasRole('ELEITOR')")
    @PostMapping("/votar")
    ResponseEntity<VotoConfirmacaoResponse> votar(Authentication authentication, @Valid @RequestBody VotoRequest request);

    @Operation(summary = "Acompanhar evolução da votação", description = "Disponível enquanto a eleição estiver ABERTA. Eleitores veem todos os cargos; Candidatos veem apenas o cargo e eleição aos quais concorrem.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Acompanhamento da votação retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Eleição não encontrada", content = @Content),
            @ApiResponse(responseCode = "409", description = "Eleição não está aberta (AGENDADA ou ENCERRADA) ou candidato não concorre nesta eleição", content = @Content)
    })
    @GetMapping("/eleicao/{idEleicao}/acompanhamento")
    ResponseEntity<List<ApuracaoCargoResponse>> acompanharVotacao(Authentication authentication, @PathVariable Long idEleicao);

    @Operation(summary = "Consultar resultado final", description = "Disponível apenas após o encerramento da eleição, exibindo votos e o mais votado por cargo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado final retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Eleição não encontrada", content = @Content),
            @ApiResponse(responseCode = "409", description = "Resultado indisponível (eleição ainda não foi encerrada)", content = @Content)
    })
    @GetMapping("/eleicao/{idEleicao}/resultado-final")
    ResponseEntity<List<ResultadoFinalCargoResponse>> resultadoFinal(@PathVariable Long idEleicao);
}