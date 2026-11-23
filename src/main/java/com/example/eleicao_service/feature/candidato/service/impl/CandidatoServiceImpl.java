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
import com.example.eleicao_service.feature.eleicao.model.entity.EleicaoModel;
import com.example.eleicao_service.feature.eleicao.repository.EleicaoRepository;
import com.example.eleicao_service.feature.partido.model.entity.PartidoModel;
import com.example.eleicao_service.feature.partido.repository.PartidoRepository;
import com.example.eleicao_service.feature.usuario.model.Perfil;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidatoServiceImpl implements CandidatoService {

    private final CandidatoRepository candidatoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PartidoRepository partidoRepository;
    private final CargoRepository cargoRepository;
    private final EleicaoRepository eleicaoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CandidatoResponse cadastrar(CandidatoRequest request) {
        if (usuarioRepository.existsByCpf(request.cpf()) || usuarioRepository.existsByUsername(request.username())) {
            throw new RegraNegocioException("CPF ou Username já cadastrado.");
        }
        if (candidatoRepository.existsByNumeroEleitoralAndCargoIdAndEleicaoId(
                request.numeroEleitoral(), request.idCargo(), request.idEleicao())) {
            throw new RegraNegocioException("Já existe candidato com este número para este cargo nesta eleição.");
        }
        if (request.senha() == null || request.senha().isBlank()) {
            throw new RegraNegocioException("Senha é obrigatória no cadastro.");
        }

        PartidoModel partido = partidoRepository.findById(request.idPartido())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Partido não encontrado."));
        CargoModel cargo = cargoRepository.findById(request.idCargo())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cargo não encontrado."));
        EleicaoModel eleicao = eleicaoRepository.findById(request.idEleicao())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Eleição não encontrada."));

        UsuarioModel usuario = UsuarioModel.builder()
                .nome(request.nome())
                .cpf(request.cpf())
                .username(request.username())
                .senha(passwordEncoder.encode(request.senha()))
                .perfil(Perfil.CANDIDATO)
                .ativo(true)
                .build();

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

        UsuarioModel usuario = candidato.getUsuario();
        usuario.setNome(request.nome());
        usuario.setCpf(request.cpf());
        usuario.setUsername(request.username());
        if (request.senha() != null && !request.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }

        candidato.setNumeroEleitoral(request.numeroEleitoral());
        candidato.setPartido(partidoRepository.findById(request.idPartido())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Partido não encontrado.")));
        candidato.setCargo(cargoRepository.findById(request.idCargo())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cargo não encontrado.")));
        candidato.setEleicao(eleicaoRepository.findById(request.idEleicao())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Eleição não encontrada.")));

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
        candidatoRepository.delete(candidato);
    }

    private CandidatoResponse toResponse(CandidatoModel c) {
        return new CandidatoResponse(c.getIdUsuario(), c.getUsuario().getNome(), c.getUsuario().getCpf(),
                c.getNumeroEleitoral(), c.getPartido().getSigla(), c.getCargo().getNome(), c.getEleicao().getId());
    }
}