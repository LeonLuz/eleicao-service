package com.example.eleicao_service.feature.candidato.service;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.candidato.mapper.CandidatoMapper;
import com.example.eleicao_service.feature.candidato.model.Cargo;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoDTO;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoInputDTO;
import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import com.example.eleicao_service.feature.candidato.repository.CandidatoRepository;
import com.example.eleicao_service.feature.usuario.model.Role;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidatoServiceImpl implements CandidatoService {

    private final CandidatoRepository candidatoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CandidatoMapper candidatoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CandidatoDTO> findAll(Cargo cargo) {
        List<CandidatoModel> candidatos = (cargo == null)
                ? candidatoRepository.findAllByOrderByCargoAscNumeroAsc()
                : candidatoRepository.findByCargoOrderByNumero(cargo);
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
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));

        if (usuario.getRoles() == null || !usuario.getRoles().contains(Role.CANDIDATO)) {
            throw new RegraNegocioException("Usuário não possui o perfil CANDIDATO");
        }
        if (candidatoRepository.existsById(usuario.getId())) {
            throw new RegraNegocioException("Usuário já está cadastrado como candidato");
        }
        validarNumero(input);

        CandidatoModel candidato = candidatoMapper.toEntity(input);
        candidato.setUsuario(usuario);
        candidato = candidatoRepository.save(candidato);
        return candidatoMapper.toDTO(candidato);
    }

    @Override
    @Transactional
    public CandidatoDTO update(CandidatoInputDTO input, Integer id) {
        CandidatoModel candidato = findOrFail(id);

        boolean numeroOuCargoMudou = !candidato.getNumero().equals(input.numero())
                || candidato.getCargo() != input.cargo();
        if (numeroOuCargoMudou) {
            validarNumero(input);
        }

        candidatoMapper.copyToEntity(input, candidato);
        candidato = candidatoRepository.save(candidato);
        return candidatoMapper.toDTO(candidato);
    }

    @Override
    @Transactional
    public CandidatoDTO delete(Integer id) {
        CandidatoModel candidato = findOrFail(id);
        candidatoRepository.delete(candidato);
        return candidatoMapper.toDTO(candidato);
    }

    private void validarNumero(CandidatoInputDTO input) {
        Cargo cargo = input.cargo();
        if (!cargo.numeroValido(input.numero())) {
            throw new RegraNegocioException("Número de " + cargo.getDescricao()
                    + " deve ter " + cargo.getDigitosNumero() + " dígitos");
        }
        if (candidatoRepository.existsByNumeroAndCargo(input.numero(), cargo)) {
            throw new RegraNegocioException("Já existe candidato a " + cargo.getDescricao()
                    + " com o número " + input.numero());
        }
    }

    private CandidatoModel findOrFail(Integer id) {
        return candidatoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Candidato não encontrado"));
    }
}
