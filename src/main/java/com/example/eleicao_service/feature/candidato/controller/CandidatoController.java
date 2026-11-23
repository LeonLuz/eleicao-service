package com.example.eleicao_service.feature.candidato.controller;

import com.example.eleicao_service.feature.candidato.model.dto.CandidatoRequest;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoResponse;
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

@Tag(name = "Candidatos", description = "Gerenciamento e consulta de candidatos")
@RequestMapping("/api/candidatos")
public interface CandidatoController {

    @Operation(summary = "Cadastrar candidato", description = "Restrito ao Chefe da Seção.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Candidato cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer perfil CHEFE_SECAO)", content = @Content),
            @ApiResponse(responseCode = "409", description = "Candidato já cadastrado (ex: CPF ou número duplicado)", content = @Content)
    })
    @PreAuthorize("hasRole('CHEFE_SECAO')")
    @PostMapping
    ResponseEntity<CandidatoResponse> cadastrar(@Valid @RequestBody CandidatoRequest request);

    @Operation(summary = "Alterar dados do candidato", description = "Permite ao Chefe da Seção alterar todos os dados, incluindo Nome e CPF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Candidato atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer perfil CHEFE_SECAO)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Candidato não encontrado", content = @Content)
    })
    @PreAuthorize("hasRole('CHEFE_SECAO')")
    @PutMapping("/{id}")
    ResponseEntity<CandidatoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody CandidatoRequest request);

    @Operation(summary = "Consultar candidato por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Candidato encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Candidato não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    ResponseEntity<CandidatoResponse> consultarPorId(@PathVariable Long id);

    @Operation(summary = "Listar candidatos por eleição e cargo", description = "Utilizado pelo Eleitor para visualizar candidatos disponíveis antes de votar.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de candidatos retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Eleição ou cargo não encontrado", content = @Content)
    })
    @GetMapping("/eleicao/{idEleicao}/cargo/{idCargo}")
    ResponseEntity<List<CandidatoResponse>> listarPorEleicaoECargo(@PathVariable Long idEleicao, @PathVariable Long idCargo);

    @Operation(summary = "Listar todos os candidatos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de candidatos retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    })
    @GetMapping
    ResponseEntity<List<CandidatoResponse>> listarTodos();

    @Operation(summary = "Excluir candidato", description = "Restrito ao Chefe da Seção.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Candidato excluído com sucesso", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer perfil CHEFE_SECAO)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Candidato não encontrado", content = @Content)
    })
    @PreAuthorize("hasRole('CHEFE_SECAO')")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> excluir(@PathVariable Long id);
}