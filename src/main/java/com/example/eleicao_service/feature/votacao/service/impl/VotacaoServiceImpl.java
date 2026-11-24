package com.example.eleicao_service.feature.votacao.service.impl;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import com.example.eleicao_service.feature.candidato.repository.CandidatoRepository;
import com.example.eleicao_service.feature.cargo.model.CargoModel;
import com.example.eleicao_service.feature.eleicao.model.StatusEleicao;
import com.example.eleicao_service.feature.eleicao.model.entity.EleicaoModel;
import com.example.eleicao_service.feature.eleicao.repository.EleicaoRepository;
import com.example.eleicao_service.feature.eleitor.model.entity.EleitorModel;
import com.example.eleicao_service.feature.eleitor.repository.EleitorRepository;
import com.example.eleicao_service.feature.usuario.model.Perfil;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import com.example.eleicao_service.feature.votacao.model.dto.*;
import com.example.eleicao_service.feature.votacao.model.entity.ControleVotoModel;
import com.example.eleicao_service.feature.votacao.model.entity.VotoModel;
import com.example.eleicao_service.feature.votacao.repository.ControleVotoRepository;
import com.example.eleicao_service.feature.votacao.repository.VotoRepository;
import com.example.eleicao_service.feature.votacao.service.VotacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VotacaoServiceImpl implements VotacaoService {

    private final EleicaoRepository eleicaoRepository;
    private final EleitorRepository eleitorRepository;
    private final CandidatoRepository candidatoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ControleVotoRepository controleVotoRepository;
    private final VotoRepository votoRepository;

    @Override
    @Transactional
    public VotoConfirmacaoResponse registrarVoto(String usernameEleitor, VotoRequest request) {
        UsuarioModel usuario = usuarioRepository.findByUsername(usernameEleitor)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado."));

        if (!usuario.possuiPerfil(Perfil.ELEITOR)) {
            throw new RegraNegocioException("Apenas usuários com perfil ELEITOR podem votar.");
        }

        EleitorModel eleitor = eleitorRepository.findById(usuario.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Eleitor não encontrado."));

        EleicaoModel eleicao = eleicaoRepository.findById(request.idEleicao())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Eleição não encontrada."));

        if (eleicao.getStatus() != StatusEleicao.ABERTA) {
            throw new RegraNegocioException("A votação não está aberta para esta eleição.");
        }

        if (controleVotoRepository.existsByEleicaoIdAndEleitorIdUsuarioAndCargoId(
                eleicao.getId(), eleitor.getIdUsuario(), request.idCargo())) {
            throw new RegraNegocioException("Eleitor já votou para este cargo nesta eleição.");
        }

        CandidatoModel candidato = candidatoRepository.findById(request.idCandidato())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Candidato não encontrado."));

        if (!candidato.getEleicao().getId().equals(eleicao.getId()) ||
                !candidato.getCargo().getId().equals(request.idCargo())) {
            throw new RegraNegocioException("Candidato não concorre a este cargo nesta eleição.");
        }

        LocalDateTime agora = LocalDateTime.now();

        controleVotoRepository.save(ControleVotoModel.builder()
                .eleicao(eleicao)
                .eleitor(eleitor)
                .cargo(candidato.getCargo())
                .dataHora(agora)
                .build());

        votoRepository.save(VotoModel.builder()
                .eleicao(eleicao)
                .cargo(candidato.getCargo())
                .candidato(candidato)
                .build());

        return new VotoConfirmacaoResponse("Voto confirmado com sucesso!", candidato.getCargo().getNome(), agora);
    }

    @Override
    public List<ApuracaoCargoResponse> acompanharVotacao(String usernameLogado, Long idEleicao) {
        UsuarioModel usuario = usuarioRepository.findByUsername(usernameLogado)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado."));

        EleicaoModel eleicao = eleicaoRepository.findById(idEleicao)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Eleição não encontrada."));

        if (eleicao.getStatus() == StatusEleicao.AGENDADA) {
            throw new RegraNegocioException("A eleição ainda não foi iniciada.");
        }

        List<CandidatoModel> candidatos;

        boolean apenasCandidato = usuario.possuiPerfil(Perfil.CANDIDATO)
                && !usuario.possuiPerfil(Perfil.ELEITOR)
                && !usuario.possuiPerfil(Perfil.CHEFE_SECAO);

        if (apenasCandidato) {
            CandidatoModel candidatoLogado = candidatoRepository.findById(usuario.getId())
                    .orElseThrow(() -> new EntidadeNaoEncontradaException("Candidato não encontrado."));
            candidatos = candidatoRepository.findByEleicaoIdAndCargoId(idEleicao, candidatoLogado.getCargo().getId());
        } else {
            candidatos = candidatoRepository.findByEleicaoId(idEleicao);
        }

        return agruparApuracaoPorCargo(idEleicao, candidatos);
    }

    @Override
    public List<ResultadoFinalCargoResponse> consultarResultadoFinal(Long idEleicao) {
        EleicaoModel eleicao = eleicaoRepository.findById(idEleicao)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Eleição não encontrada."));

        if (eleicao.getStatus() != StatusEleicao.ENCERRADA) {
            throw new RegraNegocioException("O resultado final só está disponível após o encerramento da eleição.");
        }

        List<CandidatoModel> candidatos = candidatoRepository.findByEleicaoId(idEleicao);
        List<ApuracaoCargoResponse> apuracaoPorCargo = agruparApuracaoPorCargo(idEleicao, candidatos);

        return apuracaoPorCargo.stream().map(ac -> {
            ApuracaoCandidatoResponse maisVotado = ac.candidatos().stream()
                    .max(Comparator.comparingLong(ApuracaoCandidatoResponse::totalVotos))
                    .orElse(null);
            return new ResultadoFinalCargoResponse(ac.idCargo(), ac.nomeCargo(), maisVotado, ac.candidatos());
        }).toList();
    }

    private List<ApuracaoCargoResponse> agruparApuracaoPorCargo(Long idEleicao, List<CandidatoModel> candidatos) {
        Map<CargoModel, List<CandidatoModel>> porCargo = candidatos.stream()
                .collect(Collectors.groupingBy(CandidatoModel::getCargo));

        return porCargo.entrySet().stream()
                .sorted(Map.Entry.<CargoModel, List<CandidatoModel>>comparingByKey(Comparator.comparing(CargoModel::getId)))
                .map(entry -> {
            CargoModel cargo = entry.getKey();
            List<ApuracaoCandidatoResponse> listaCandidatos = entry.getValue().stream()
                    .map(c -> new ApuracaoCandidatoResponse(
                            c.getIdUsuario(),
                            c.getUsuario().getNome(),
                            c.getNumeroEleitoral(),
                            c.getPartido().getSigla(),
                            votoRepository.countByEleicaoIdAndCandidatoIdUsuario(idEleicao, c.getIdUsuario())
                    ))
                    .sorted(Comparator.comparingLong(ApuracaoCandidatoResponse::totalVotos).reversed())
                    .toList();

            return new ApuracaoCargoResponse(cargo.getId(), cargo.getNome(), listaCandidatos);
        }).toList();
    }
}