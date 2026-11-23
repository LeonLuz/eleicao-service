package com.example.eleicao_service.feature.usuario.controller;

import com.example.eleicao_service.feature.usuario.model.dto.CriarChefeSecaoRequest;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioResponse;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioUpdateSelfRequest;
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

@Tag(name = "Usuários", description = "Gerenciamento de perfil do usuário autenticado e cadastro de chefes de seção")
@RequestMapping("/api/usuarios")
public interface UsuarioController {

    @Operation(summary = "Cadastrar novo chefe de seção", description = "Permite que um Chefe de Seção autenticado cadastre outro Chefe de Seção.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Chefe de seção cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acesso negado (requer perfil CHEFE_SECAO)", content = @Content),
            @ApiResponse(responseCode = "409", description = "Usuário já cadastrado (ex: username ou CPF duplicado)", content = @Content)
    })
    @PreAuthorize("hasRole('CHEFE_SECAO')")
    @PostMapping("/chefe-secao")
    ResponseEntity<UsuarioResponse> criarChefeSecao(@Valid @RequestBody CriarChefeSecaoRequest request);

    @Operation(summary = "Consultar meu perfil", description = "Retorna os dados do usuário atualmente autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @GetMapping("/me")
    ResponseEntity<UsuarioResponse> meuPerfil(Authentication authentication);

    @Operation(summary = "Editar meus dados", description = "Permite ao usuário alterar username e senha. Nome e CPF só podem ser alterados pelo Chefe da Seção.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autenticado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Username já está em uso por outro usuário", content = @Content)
    })
    @PutMapping("/me")
    ResponseEntity<UsuarioResponse> atualizarMeusDados(Authentication authentication, @Valid @RequestBody UsuarioUpdateSelfRequest request);
}