package com.example.eleicao_service.feature.partido.service.impl;


import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.partido.model.dto.PartidoRequest;
import com.example.eleicao_service.feature.partido.model.dto.PartidoResponse;
import com.example.eleicao_service.feature.partido.model.entity.PartidoModel;
import com.example.eleicao_service.feature.partido.repository.PartidoRepository;
import com.example.eleicao_service.feature.partido.service.PartidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartidoServiceImpl implements PartidoService {

    private final PartidoRepository partidoRepository;

    @Override
    public PartidoResponse cadastrar(PartidoRequest request) {
        if (partidoRepository.existsByNumero(request.numero()) || partidoRepository.existsBySigla(request.sigla())) {
            throw new RegraNegocioException("Partido com este número ou sigla já existe.");
        }
        PartidoModel partido = partidoRepository.save(PartidoModel.builder()
                .nome(request.nome())
                .sigla(request.sigla())
                .numero(request.numero())
                .build());
        return new PartidoResponse(partido.getId(), partido.getNome(), partido.getSigla(), partido.getNumero());
    }

    @Override
    public List<PartidoResponse> listarTodos() {
        return partidoRepository.findAll().stream()
                .map(p -> new PartidoResponse(p.getId(), p.getNome(), p.getSigla(), p.getNumero()))
                .toList();
    }
}