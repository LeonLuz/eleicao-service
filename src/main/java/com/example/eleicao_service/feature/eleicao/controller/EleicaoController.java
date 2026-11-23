package com.example.eleicao_service.feature.eleicao.controller;

import com.example.eleicao_service.feature.eleicao.model.dto.EleicaoRequest;
import com.example.eleicao_service.feature.eleicao.model.dto.EleicaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Eleição cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer perfil CHEFE_SECAO)", content = @Content),
            @ApiResponse(responseCode = "409", description = "Regra de negócio violada (ex: horário de encerramento anterior à abertura)", content = @Content)
    })
    @PreAuthorize("hasRole('CHEFE_SECAO')")
    @PostMapping
    ResponseEntity<EleicaoResponse> cadastrar(@Valid @RequestBody EleicaoRequest request);

    @Operation(summary = "Abrir votação", description = "Altera o status da eleição de AGENDADA para ABERTA. Restrito ao Chefe da Seção.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Votação aberta com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer perfil CHEFE_SECAO)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Eleição não encontrada", content = @Content),
            @ApiResponse(responseCode = "409", description = "Não é possível abrir a votação (eleição não está AGENDADA)", content = @Content)
    })
    @PreAuthorize("hasRole('CHEFE_SECAO')")
    @PatchMapping("/{id}/abrir")
    ResponseEntity<EleicaoResponse> abrirVotacao(@PathVariable Long id);

    @Operation(summary = "Encerrar votação", description = "Altera o status da eleição de ABERTA para ENCERRADA. Restrito ao Chefe da Seção.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Votação encerrada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer perfil CHEFE_SECAO)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Eleição não encontrada", content = @Content),
            @ApiResponse(responseCode = "409", description = "Não é possível encerrar a votação (eleição não está ABERTA)", content = @Content)
    })
    @PreAuthorize("hasRole('CHEFE_SECAO')")
    @PatchMapping("/{id}/encerrar")
    ResponseEntity<EleicaoResponse> encerrarVotacao(@PathVariable Long id);

    @Operation(summary = "Consultar situação da eleição")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Eleição encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Eleição não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    ResponseEntity<EleicaoResponse> consultarPorId(@PathVariable Long id);

    @Operation(summary = "Listar todas as eleições")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de eleições retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    })
    @GetMapping
    ResponseEntity<List<EleicaoResponse>> listarTodas();
}