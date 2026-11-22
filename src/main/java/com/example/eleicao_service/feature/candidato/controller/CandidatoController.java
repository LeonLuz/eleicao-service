package com.example.eleicao_service.feature.candidato.controller;

import com.example.eleicao_service.feature.candidato.model.dto.CandidatoDTO;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoInputDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Candidatos", description = "Gerenciamento de candidatos das eleições")
public interface CandidatoController {

    @Operation(summary = "Lista todos os candidatos", description = "Retorna todos os candidatos cadastrados, com opção de filtro por cargo")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @GetMapping
    ResponseEntity<List<CandidatoDTO>> findAll(
            @Parameter(description = "ID do cargo para filtrar os candidatos")
            @RequestParam(name = "cargoId", required = false) Integer cargoId
    );

    @Operation(summary = "Busca um candidato por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Candidato encontrado"),
            @ApiResponse(responseCode = "404", description = "Candidato não encontrado")
    })
    @GetMapping("/{id}")
    ResponseEntity<CandidatoDTO> findById(
            @Parameter(description = "ID do candidato", required = true)
            @PathVariable("id") Integer id
    );

    @Operation(summary = "Cadastra um novo candidato")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Candidato cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "404", description = "Partido, cargo ou usuário não encontrado")
    })
    @PostMapping
    ResponseEntity<CandidatoDTO> insert(@RequestBody @Valid CandidatoInputDTO candidatoInputDTO);

    @Operation(summary = "Atualiza os dados de um candidato")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Candidato atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "404", description = "Candidato, partido ou cargo não encontrado")
    })
    @PutMapping("/{id}")
    ResponseEntity<CandidatoDTO> update(
            @RequestBody @Valid CandidatoInputDTO candidatoInputDTO,
            @Parameter(description = "ID do candidato", required = true)
            @PathVariable("id") Integer id
    );

    @Operation(summary = "Remove um candidato")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Candidato removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Candidato não encontrado")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<CandidatoDTO> delete(
            @Parameter(description = "ID do candidato", required = true)
            @PathVariable("id") Integer id
    );
}