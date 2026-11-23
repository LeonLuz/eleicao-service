package com.example.eleicao_service.feature.usuario.service.impl;

import com.example.eleicao_service.feature.usuario.model.dto.UsuarioResponse;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioUpdateSelfRequest;
import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import com.example.eleicao_service.feature.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponse atualizarMeusDados(String usernameLogado, UsuarioUpdateSelfRequest request) {
        UsuarioModel usuario = usuarioRepository.findByUsername(usernameLogado)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado."));

        if (!usuario.getUsername().equals(request.username()) && usuarioRepository.existsByUsername(request.username())) {
            throw new RegraNegocioException("Username já está em uso.");
        }

        usuario.setUsername(request.username());
        if (request.novaSenha() != null && !request.novaSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        }

        usuarioRepository.save(usuario);
        return toResponse(usuario);
    }

    @Override
    public UsuarioResponse buscarMeuPerfil(String usernameLogado) {
        UsuarioModel usuario = usuarioRepository.findByUsername(usernameLogado)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado."));
        return toResponse(usuario);
    }

    private UsuarioResponse toResponse(UsuarioModel u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getCpf(), u.getUsername(), u.getPerfil(), u.getAtivo());
    }
}