package com.example.eleicao_service.feature.partido.controller;

import com.example.eleicao_service.feature.partido.model.dto.PartidoRequest;
import com.example.eleicao_service.feature.partido.model.dto.PartidoResponse;
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

@Tag(name = "Partidos", description = "Gerenciamento de partidos políticos")
@RequestMapping("/api/partidos")
public interface PartidoController {

    @Operation(summary = "Cadastrar partido", description = "Restrito ao Chefe da Seção.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Partido cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer perfil CHEFE_SECAO)", content = @Content),
            @ApiResponse(responseCode = "409", description = "Partido já cadastrado (ex: sigla ou número duplicado)", content = @Content)
    })
    @PreAuthorize("hasRole('CHEFE_SECAO')")
    @PostMapping
    ResponseEntity<PartidoResponse> cadastrar(@Valid @RequestBody PartidoRequest request);

    @Operation(summary = "Listar partidos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de partidos retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content)
    })
    @GetMapping
    ResponseEntity<List<PartidoResponse>> listarTodos();
}