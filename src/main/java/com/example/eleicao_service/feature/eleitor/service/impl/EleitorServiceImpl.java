package com.example.eleicao_service.feature.eleitor.service.impl;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.eleitor.model.dto.EleitorRequest;
import com.example.eleicao_service.feature.eleitor.model.dto.EleitorResponse;
import com.example.eleicao_service.feature.eleitor.model.entity.EleitorModel;
import com.example.eleicao_service.feature.eleitor.repository.EleitorRepository;
import com.example.eleicao_service.feature.eleitor.service.EleitorService;
import com.example.eleicao_service.feature.usuario.model.Perfil;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import com.example.eleicao_service.feature.votacao.repository.ControleVotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EleitorServiceImpl implements EleitorService {

    private final EleitorRepository eleitorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ControleVotoRepository controleVotoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public EleitorResponse cadastrar(EleitorRequest request) {
        String cpf = request.cpfLimpo();

        if (eleitorRepository.existsByTituloEleitor(request.tituloEleitor())) {
            throw new RegraNegocioException("Título de eleitor já cadastrado.");
        }

        UsuarioModel usuario = usuarioRepository.findByCpf(cpf)
                .map(existente -> {
                    if (eleitorRepository.existsById(existente.getId()) || existente.possuiPerfil(Perfil.ELEITOR)) {
                        throw new RegraNegocioException("Este usuário já está cadastrado como eleitor.");
                    }
                    existente.adicionarPerfil(Perfil.ELEITOR);
                    return usuarioRepository.save(existente);
                })
                .orElseGet(() -> {
                    if (usuarioRepository.existsByUsername(request.username())) {
                        throw new RegraNegocioException("Username já cadastrado.");
                    }
                    if (request.senha() == null || request.senha().isBlank()) {
                        throw new RegraNegocioException("Senha é obrigatória no cadastro.");
                    }
                    UsuarioModel novo = UsuarioModel.builder()
                            .nome(request.nome())
                            .cpf(cpf)
                            .username(request.username())
                            .senha(passwordEncoder.encode(request.senha()))
                            .perfis(new HashSet<>(Set.of(Perfil.ELEITOR)))
                            .ativo(true)
                            .build();
                    return usuarioRepository.save(novo);
                });

        EleitorModel eleitor = eleitorRepository.save(EleitorModel.builder()
                .usuario(usuario)
                .tituloEleitor(request.tituloEleitor())
                .zona(request.zona())
                .secao(request.secao())
                .build());

        return toResponse(eleitor);
    }

    @Override
    @Transactional
    public EleitorResponse atualizar(Long id, EleitorRequest request) {
        EleitorModel eleitor = eleitorRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Eleitor não encontrado."));

        UsuarioModel usuario = eleitor.getUsuario();
        String cpf = request.cpfLimpo();

        if (!usuario.getCpf().equals(cpf) && usuarioRepository.existsByCpf(cpf)) {
            throw new RegraNegocioException("CPF já cadastrado.");
        }
        if (!usuario.getUsername().equals(request.username()) && usuarioRepository.existsByUsername(request.username())) {
            throw new RegraNegocioException("Username já cadastrado.");
        }
        if (!eleitor.getTituloEleitor().equals(request.tituloEleitor())
                && eleitorRepository.existsByTituloEleitor(request.tituloEleitor())) {
            throw new RegraNegocioException("Título de eleitor já cadastrado.");
        }

        usuario.setNome(request.nome());
        usuario.setCpf(cpf);
        usuario.setUsername(request.username());
        if (request.senha() != null && !request.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }

        eleitor.setTituloEleitor(request.tituloEleitor());
        eleitor.setZona(request.zona());
        eleitor.setSecao(request.secao());

        return toResponse(eleitorRepository.save(eleitor));
    }

    @Override
    public EleitorResponse consultarPorId(Long id) {
        return toResponse(eleitorRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Eleitor não encontrado.")));
    }

    @Override
    public List<EleitorResponse> listarTodos() {
        return eleitorRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        EleitorModel eleitor = eleitorRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Eleitor não encontrado."));

        if (controleVotoRepository.existsByEleitorIdUsuario(eleitor.getIdUsuario())) {
            throw new RegraNegocioException("Não é possível excluir um eleitor que já registrou voto.");
        }

        UsuarioModel usuario = eleitor.getUsuario();
        usuario.getPerfis().remove(Perfil.ELEITOR);

        eleitorRepository.delete(eleitor);

        if (usuario.getPerfis().isEmpty()) {
            usuarioRepository.delete(usuario);
        } else {
            usuarioRepository.save(usuario);
        }
    }

    private EleitorResponse toResponse(EleitorModel e) {
        return new EleitorResponse(e.getIdUsuario(), e.getUsuario().getNome(), e.getUsuario().getCpf(),
                e.getUsuario().getUsername(), e.getTituloEleitor(), e.getZona(), e.getSecao());
    }
}