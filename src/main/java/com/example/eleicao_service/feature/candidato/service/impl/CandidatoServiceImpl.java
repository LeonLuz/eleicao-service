package com.example.eleicao_service.feature.candidato.service.impl;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoRequest;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoResponse;
import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import com.example.eleicao_service.feature.candidato.repository.CandidatoRepository;
import com.example.eleicao_service.feature.candidato.service.CandidatoService;
import com.example.eleicao_service.feature.cargo.model.CargoModel;
import com.example.eleicao_service.feature.cargo.repository.CargoRepository;
import com.example.eleicao_service.feature.eleicao.model.StatusEleicao;
import com.example.eleicao_service.feature.eleicao.model.entity.EleicaoModel;
import com.example.eleicao_service.feature.eleicao.repository.EleicaoRepository;
import com.example.eleicao_service.feature.partido.model.entity.PartidoModel;
import com.example.eleicao_service.feature.partido.repository.PartidoRepository;
import com.example.eleicao_service.feature.usuario.model.Perfil;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import com.example.eleicao_service.feature.votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CandidatoServiceImpl implements CandidatoService {

    private final CandidatoRepository candidatoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PartidoRepository partidoRepository;
    private final CargoRepository cargoRepository;
    private final EleicaoRepository eleicaoRepository;
    private final VotoRepository votoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CandidatoResponse cadastrar(CandidatoRequest request) {
        String cpf = request.cpfLimpo();

        if (candidatoRepository.existsByNumeroEleitoralAndCargoIdAndEleicaoId(
                request.numeroEleitoral(), request.idCargo(), request.idEleicao())) {
            throw new RegraNegocioException("Já existe candidato com este número para este cargo nesta eleição.");
        }

        PartidoModel partido = partidoRepository.findById(request.idPartido())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Partido não encontrado."));
        CargoModel cargo = cargoRepository.findById(request.idCargo())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cargo não encontrado."));
        EleicaoModel eleicao = eleicaoRepository.findById(request.idEleicao())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Eleição não encontrada."));

        if (eleicao.getStatus() != StatusEleicao.AGENDADA) {
            throw new RegraNegocioException("Só é possível cadastrar candidatos em uma eleição AGENDADA.");
        }

        UsuarioModel usuario = usuarioRepository.findByCpf(cpf)
                .map(existente -> {
                    if (existente.possuiPerfil(Perfil.CHEFE_SECAO)) {
                        throw new RegraNegocioException("Um chefe de seção não pode ser cadastrado como candidato.");
                    }
                    if (candidatoRepository.existsById(existente.getId()) || existente.possuiPerfil(Perfil.CANDIDATO)) {
                        throw new RegraNegocioException("Este usuário já está cadastrado como candidato.");
                    }
                    existente.adicionarPerfil(Perfil.CANDIDATO);
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
                            .perfis(new HashSet<>(Set.of(Perfil.CANDIDATO)))
                            .ativo(true)
                            .build();
                    return usuarioRepository.save(novo);
                });

        CandidatoModel candidato = candidatoRepository.save(CandidatoModel.builder()
                .usuario(usuario)
                .numeroEleitoral(request.numeroEleitoral())
                .partido(partido)
                .cargo(cargo)
                .eleicao(eleicao)
                .build());

        return toResponse(candidato);
    }

    @Override
    @Transactional
    public CandidatoResponse atualizar(Long id, CandidatoRequest request) {
        CandidatoModel candidato = candidatoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Candidato não encontrado."));

        if (candidato.getEleicao().getStatus() != StatusEleicao.AGENDADA) {
            throw new RegraNegocioException("Só é possível alterar candidatos enquanto a eleição estiver AGENDADA.");
        }

        UsuarioModel usuario = candidato.getUsuario();
        String cpf = request.cpfLimpo();

        if (!usuario.getCpf().equals(cpf) && usuarioRepository.existsByCpf(cpf)) {
            throw new RegraNegocioException("CPF já cadastrado.");
        }
        if (!usuario.getUsername().equals(request.username()) && usuarioRepository.existsByUsername(request.username())) {
            throw new RegraNegocioException("Username já cadastrado.");
        }
        boolean mudouNumeroCargoOuEleicao = !candidato.getNumeroEleitoral().equals(request.numeroEleitoral())
                || !candidato.getCargo().getId().equals(request.idCargo())
                || !candidato.getEleicao().getId().equals(request.idEleicao());
        if (mudouNumeroCargoOuEleicao && candidatoRepository.existsByNumeroEleitoralAndCargoIdAndEleicaoId(
                request.numeroEleitoral(), request.idCargo(), request.idEleicao())) {
            throw new RegraNegocioException("Já existe candidato com este número para este cargo nesta eleição.");
        }

        PartidoModel partido = partidoRepository.findById(request.idPartido())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Partido não encontrado."));
        CargoModel cargo = cargoRepository.findById(request.idCargo())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cargo não encontrado."));
        EleicaoModel eleicao = eleicaoRepository.findById(request.idEleicao())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Eleição não encontrada."));

        if (eleicao.getStatus() != StatusEleicao.AGENDADA) {
            throw new RegraNegocioException("Só é possível associar candidato a uma eleição AGENDADA.");
        }

        usuario.setNome(request.nome());
        usuario.setCpf(cpf);
        usuario.setUsername(request.username());
        if (request.senha() != null && !request.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }

        candidato.setNumeroEleitoral(request.numeroEleitoral());
        candidato.setPartido(partido);
        candidato.setCargo(cargo);
        candidato.setEleicao(eleicao);

        return toResponse(candidatoRepository.save(candidato));
    }

    @Override
    public CandidatoResponse consultarPorId(Long id) {
        return toResponse(candidatoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Candidato não encontrado.")));
    }

    @Override
    public List<CandidatoResponse> listarPorEleicaoECargo(Long idEleicao, Long idCargo) {
        return candidatoRepository.findByEleicaoIdAndCargoId(idEleicao, idCargo)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<CandidatoResponse> listarTodos() {
        return candidatoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        CandidatoModel candidato = candidatoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Candidato não encontrado."));

        if (votoRepository.existsByCandidatoIdUsuario(candidato.getIdUsuario())) {
            throw new RegraNegocioException("Não é possível excluir um candidato que já recebeu votos.");
        }
        if (candidato.getEleicao().getStatus() != StatusEleicao.AGENDADA) {
            throw new RegraNegocioException("Só é possível excluir candidatos enquanto a eleição estiver AGENDADA.");
        }

        UsuarioModel usuario = candidato.getUsuario();
        usuario.getPerfis().remove(Perfil.CANDIDATO);

        candidatoRepository.delete(candidato);

        if (usuario.getPerfis().isEmpty()) {
            usuarioRepository.delete(usuario);
        } else {
            usuarioRepository.save(usuario);
        }
    }

    private CandidatoResponse toResponse(CandidatoModel c) {
        return new CandidatoResponse(c.getIdUsuario(), c.getUsuario().getNome(), c.getUsuario().getCpf(),
                c.getNumeroEleitoral(), c.getPartido().getSigla(), c.getCargo().getNome(), c.getEleicao().getId());
    }
}