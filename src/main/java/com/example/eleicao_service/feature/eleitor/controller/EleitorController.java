package com.example.eleicao_service.feature.eleitor.controller;

import com.example.eleicao_service.feature.eleitor.model.dto.EleitorRequest;
import com.example.eleicao_service.feature.eleitor.model.dto.EleitorResponse;
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

@Tag(name = "Eleitores", description = "Gerenciamento de eleitores (Restrito ao Chefe da Seção)")
@RequestMapping("/api/eleitores")
@PreAuthorize("hasRole('CHEFE_SECAO')")
public interface EleitorController {

    @Operation(summary = "Cadastrar eleitor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Eleitor cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer perfil CHEFE_SECAO)", content = @Content),
            @ApiResponse(responseCode = "409", description = "Eleitor já cadastrado (ex: CPF, username ou título duplicado)", content = @Content)
    })
    @PostMapping
    ResponseEntity<EleitorResponse> cadastrar(@Valid @RequestBody EleitorRequest request);

    @Operation(summary = "Alterar dados do eleitor", description = "Permite ao Chefe da Seção alterar todos os dados, incluindo Nome e CPF.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Eleitor atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer perfil CHEFE_SECAO)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Eleitor não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "CPF, username ou título já em uso por outro eleitor", content = @Content)
    })
    @PutMapping("/{id}")
    ResponseEntity<EleitorResponse> atualizar(@PathVariable Long id, @Valid @RequestBody EleitorRequest request);

    @Operation(summary = "Consultar eleitor por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Eleitor encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer perfil CHEFE_SECAO)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Eleitor não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    ResponseEntity<EleitorResponse> consultarPorId(@PathVariable Long id);

    @Operation(summary = "Listar todos os eleitores")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de eleitores retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer perfil CHEFE_SECAO)", content = @Content)
    })
    @GetMapping
    ResponseEntity<List<EleitorResponse>> listarTodos();

    @Operation(summary = "Excluir eleitor")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Eleitor excluído com sucesso", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer perfil CHEFE_SECAO)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Eleitor não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> excluir(@PathVariable Long id);
}