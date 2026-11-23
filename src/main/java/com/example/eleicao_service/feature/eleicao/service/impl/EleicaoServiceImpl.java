package com.example.eleicao_service.feature.eleicao.service.impl;


import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.eleicao.model.StatusEleicao;
import com.example.eleicao_service.feature.eleicao.model.dto.EleicaoRequest;
import com.example.eleicao_service.feature.eleicao.model.dto.EleicaoResponse;
import com.example.eleicao_service.feature.eleicao.model.entity.EleicaoModel;
import com.example.eleicao_service.feature.eleicao.repository.EleicaoRepository;
import com.example.eleicao_service.feature.eleicao.service.EleicaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EleicaoServiceImpl implements EleicaoService {

    private final EleicaoRepository eleicaoRepository;

    @Override
    @Transactional
    public EleicaoResponse cadastrar(EleicaoRequest request) {
        if (eleicaoRepository.existsByDataEleicaoAndTurno(request.dataEleicao(), request.turno())) {
            throw new RegraNegocioException("Já existe uma eleição cadastrada para esta data e turno.");
        }
        if (!request.horarioEncerramento().isAfter(request.horarioAbertura())) {
            throw new RegraNegocioException("O horário de encerramento deve ser posterior ao de abertura.");
        }

        EleicaoModel eleicao = eleicaoRepository.save(EleicaoModel.builder()
                .descricao(request.descricao())
                .dataEleicao(request.dataEleicao())
                .turno(request.turno())
                .horarioAbertura(request.horarioAbertura())
                .horarioEncerramento(request.horarioEncerramento())
                .status(StatusEleicao.AGENDADA)
                .build());
        return toResponse(eleicao);
    }

    @Override
    @Transactional
    public EleicaoResponse abrirVotacao(Long id) {
        EleicaoModel eleicao = buscarEntidade(id);
        if (eleicao.getStatus() != StatusEleicao.AGENDADA) {
            throw new RegraNegocioException("Apenas eleições AGENDADAS podem ser abertas.");
        }
        eleicao.setStatus(StatusEleicao.ABERTA);
        return toResponse(eleicaoRepository.save(eleicao));
    }

    @Override
    @Transactional
    public EleicaoResponse encerrarVotacao(Long id) {
        EleicaoModel eleicao = buscarEntidade(id);
        if (eleicao.getStatus() != StatusEleicao.ABERTA) {
            throw new RegraNegocioException("Apenas eleições ABERTAS podem ser encerradas.");
        }
        eleicao.setStatus(StatusEleicao.ENCERRADA);
        return toResponse(eleicaoRepository.save(eleicao));
    }

    @Override
    public EleicaoResponse consultarPorId(Long id) {
        return toResponse(buscarEntidade(id));
    }

    @Override
    public List<EleicaoResponse> listarTodas() {
        return eleicaoRepository.findAll().stream().map(this::toResponse).toList();
    }

    private EleicaoModel buscarEntidade(Long id) {
        return eleicaoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Eleição não encontrada."));
    }

    private EleicaoResponse toResponse(EleicaoModel e) {
        return new EleicaoResponse(e.getId(), e.getDescricao(), e.getDataEleicao(), e.getTurno(),
                e.getHorarioAbertura(), e.getHorarioEncerramento(), e.getStatus());
    }
}