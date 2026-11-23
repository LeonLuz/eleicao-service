package com.example.eleicao_service.feature.usuario.controller.impl;

import com.example.eleicao_service.feature.usuario.controller.UsuarioController;
import com.example.eleicao_service.feature.usuario.model.dto.CriarChefeSecaoRequest;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioResponse;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioUpdateSelfRequest;
import com.example.eleicao_service.feature.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsuarioControllerImpl implements UsuarioController {

    private final UsuarioService usuarioService;

    @Override
    @PreAuthorize("hasRole('CHEFE_SECAO')")
    public ResponseEntity<UsuarioResponse> criarChefeSecao(CriarChefeSecaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criarChefeSecao(request));
    }

    @Override
    public ResponseEntity<UsuarioResponse> meuPerfil(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.buscarMeuPerfil(authentication.getName()));
    }

    @Override
    public ResponseEntity<UsuarioResponse> atualizarMeusDados(Authentication authentication, UsuarioUpdateSelfRequest request) {
        return ResponseEntity.ok(usuarioService.atualizarMeusDados(authentication.getName(), request));
    }
}