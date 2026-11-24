package com.example.eleicao_service.feature.usuario.service.impl;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.usuario.model.Perfil;
import com.example.eleicao_service.feature.usuario.model.dto.CriarChefeSecaoRequest;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioResponse;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioUpdateSelfRequest;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import com.example.eleicao_service.feature.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponse criarChefeSecao(CriarChefeSecaoRequest request) {
        UsuarioModel usuario = usuarioRepository.findByCpf(request.cpf())
                .map(existente -> {
                    if (existente.possuiPerfil(Perfil.CANDIDATO)) {
                        throw new RegraNegocioException("Um candidato não pode ser cadastrado como chefe de seção.");
                    }
                    if (existente.possuiPerfil(Perfil.CHEFE_SECAO)) {
                        throw new RegraNegocioException("Este usuário já possui o perfil de chefe de seção.");
                    }
                    existente.adicionarPerfil(Perfil.CHEFE_SECAO);
                    return usuarioRepository.save(existente);
                })
                .orElseGet(() -> {
                    if (usuarioRepository.existsByUsername(request.username())) {
                        throw new RegraNegocioException("Username já está em uso.");
                    }
                    UsuarioModel novoChefe = UsuarioModel.builder()
                            .nome(request.nome())
                            .cpf(request.cpf())
                            .username(request.username())
                            .senha(passwordEncoder.encode(request.senha()))
                            .perfis(new HashSet<>(Set.of(Perfil.CHEFE_SECAO)))
                            .ativo(true)
                            .build();
                    return usuarioRepository.save(novoChefe);
                });

        return toResponse(usuario);
    }

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
        return new UsuarioResponse(u.getId(), u.getNome(), u.getCpf(), u.getUsername(), u.getPerfis(), u.getAtivo());
    }
}