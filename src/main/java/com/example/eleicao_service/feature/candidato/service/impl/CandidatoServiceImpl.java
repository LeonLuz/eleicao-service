package com.example.eleicao_service.feature.candidato.service.impl;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.candidato.mapper.CandidatoMapper;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoDTO;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoInputDTO;
import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import com.example.eleicao_service.feature.candidato.repository.CandidatoRepository;
import com.example.eleicao_service.feature.candidato.service.CandidatoService;
import com.example.eleicao_service.feature.cargo.model.entity.CargoModel;
import com.example.eleicao_service.feature.cargo.repository.CargoRepository;
import com.example.eleicao_service.feature.partido.model.entity.PartidoModel;
import com.example.eleicao_service.feature.partido.repository.PartidoRepository;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CandidatoServiceImpl implements CandidatoService {

    private final CandidatoRepository candidatoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CargoRepository cargoRepository;
    private final PartidoRepository partidoRepository;
    private final CandidatoMapper candidatoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CandidatoDTO> findAll(Integer cargoId) {
        List<CandidatoModel> candidatos = (cargoId == null)
                ? candidatoRepository.findAllByOrderByCargoIdAscPartidoIdLegendaAscIdEleitoralAsc()
                : candidatoRepository.findByCargoIdOrderByPartidoIdLegendaAscIdEleitoralAsc(cargoId);
        return candidatos.stream().map(candidatoMapper::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CandidatoDTO findById(Integer id) {
        return candidatoMapper.toDTO(findOrFail(id));
    }

    @Override
    @Transactional
    public CandidatoDTO insert(CandidatoInputDTO input) {
        UsuarioModel usuario = usuarioRepository.findById(input.idUsuario())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado: " + input.idUsuario()));

        if (candidatoRepository.existsById(usuario.getId())) {
            throw new RegraNegocioException("Usuário já está cadastrado como candidato");
        }

        CargoModel cargo = findCargoOrFail(input.idCargo());
        PartidoModel partido = findPartidoOrFail(input.idPartido());

        validarIdEleitoral(input.idEleitoral(), cargo, partido);

        CandidatoModel candidato = candidatoMapper.toEntity(input);
        candidato.setUsuario(usuario);
        candidato.setCargo(cargo);
        candidato.setPartido(partido);

        if (usuario.getRoles() == null) {
            usuario.setRoles(new ArrayList<>(List.of("USER", "CANDIDATO")));
        } else if (!usuario.getRoles().contains("CANDIDATO")) {
            usuario.getRoles().add("CANDIDATO");
        }

        candidato = candidatoRepository.save(candidato);
        return candidatoMapper.toDTO(candidato);
    }

    @Override
    @Transactional
    public CandidatoDTO update(CandidatoInputDTO input, Integer id) {
        CandidatoModel candidato = findOrFail(id);
        CargoModel cargo = findCargoOrFail(input.idCargo());
        PartidoModel partido = findPartidoOrFail(input.idPartido());

        boolean dadosEleitoraisMudaram = !Objects.equals(candidato.getIdEleitoral(), input.idEleitoral())
                || !candidato.getCargo().getId().equals(cargo.getId())
                || !candidato.getPartido().getId().equals(partido.getId());

        if (dadosEleitoraisMudaram) {
            validarIdEleitoral(input.idEleitoral(), cargo, partido);
        }

        candidatoMapper.copyToEntity(input, candidato);
        candidato.setCargo(cargo);
        candidato.setPartido(partido);

        candidato = candidatoRepository.save(candidato);
        return candidatoMapper.toDTO(candidato);
    }

    @Override
    @Transactional
    public CandidatoDTO delete(Integer id) {
        CandidatoModel candidato = findOrFail(id);
        CandidatoDTO dto = candidatoMapper.toDTO(candidato);

        UsuarioModel usuario = candidato.getUsuario();
        if (usuario != null) {
            usuario.setCandidato(null);
            if (usuario.getRoles() != null) {
                usuario.getRoles().remove("CANDIDATO");
            }
        }

        candidatoRepository.delete(candidato);
        return dto;
    }

    private void validarIdEleitoral(Integer idEleitoral, CargoModel cargo, PartidoModel partido) {
        if (!cargo.sufixoValido(idEleitoral)) {
            if (cargo.getQtdDigitos() == 0) {
                throw new RegraNegocioException("Para o cargo " + cargo.getNome()
                        + ", o sufixo eleitoral deve ser 0");
            }
            int maxValor = (int) Math.pow(10, cargo.getQtdDigitos()) - 1;
            throw new RegraNegocioException("Sufixo eleitoral de " + cargo.getNome()
                    + " deve estar entre 0 e " + maxValor);
        }

        if (candidatoRepository.existsByCargoIdAndPartidoIdAndIdEleitoral(cargo.getId(), partido.getId(), idEleitoral)) {
            throw new RegraNegocioException("Já existe candidato a " + cargo.getNome()
                    + " neste partido com este número eleitoral");
        }
    }

    private CandidatoModel findOrFail(Integer id) {
        return candidatoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Candidato não encontrado: " + id));
    }

    private CargoModel findCargoOrFail(Integer cargoId) {
        return cargoRepository.findById(cargoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cargo não encontrado: " + cargoId));
    }

    private PartidoModel findPartidoOrFail(Integer partidoId) {
        return partidoRepository.findById(partidoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Partido não encontrado: " + partidoId));
    }
}