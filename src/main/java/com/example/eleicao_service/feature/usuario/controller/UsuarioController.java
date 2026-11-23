package com.example.eleicao_service.feature.usuario.controller;

import com.example.eleicao_service.feature.usuario.model.dto.UsuarioResponse;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioUpdateSelfRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuários", description = "Gerenciamento do próprio perfil do usuário autenticado")
@RequestMapping("/api/usuarios")
public interface UsuarioController {

    @Operation(summary = "Consultar meu perfil", description = "Retorna os dados do usuário atualmente autenticado.")
    @GetMapping("/me")
    ResponseEntity<UsuarioResponse> meuPerfil(Authentication authentication);

    @Operation(summary = "Editar meus dados", description = "Permite ao usuário alterar username e senha. Nome e CPF só podem ser alterados pelo Chefe da Seção.")
    @PutMapping("/me")
    ResponseEntity<UsuarioResponse> atualizarMeusDados(Authentication authentication, @Valid @RequestBody UsuarioUpdateSelfRequest request);
}