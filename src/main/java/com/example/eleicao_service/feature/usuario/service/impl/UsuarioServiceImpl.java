package com.example.eleicao_service.feature.usuario.service.impl;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoInputDTO;
import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import com.example.eleicao_service.feature.candidato.repository.CandidatoRepository;
import com.example.eleicao_service.feature.cargo.model.entity.CargoModel;
import com.example.eleicao_service.feature.cargo.repository.CargoRepository;
import com.example.eleicao_service.feature.eleitor.repository.EleitorRepository;
import com.example.eleicao_service.feature.partido.model.entity.PartidoModel;
import com.example.eleicao_service.feature.partido.repository.PartidoRepository;
import com.example.eleicao_service.feature.usuario.mapper.UsuarioMapper;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioDTO;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioInputDTO;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import com.example.eleicao_service.feature.usuario.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EleitorRepository eleitorRepository;
    private final CandidatoRepository candidatoRepository;
    private final CargoRepository cargoRepository;
    private final PartidoRepository partidoRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO findById(Integer id) {
        return usuarioMapper.toDTO(findOrFail(id));
    }

    @Override
    @Transactional
    public UsuarioDTO insert(UsuarioInputDTO inputDTO) {
        if (usuarioRepository.existsByUsername(inputDTO.username())) {
            throw new RegraNegocioException("Username já cadastrado");
        }

        validarUnicidadeEleitor(inputDTO, null);

        UsuarioModel usuario = usuarioMapper.toEntity(inputDTO);
        prepararCandidato(usuario, inputDTO.candidato(), null);

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO update(UsuarioInputDTO inputDTO, Integer id) {
        UsuarioModel usuario = findOrFail(id);

        if (!usuario.getUsername().equals(inputDTO.username()) && usuarioRepository.existsByUsername(inputDTO.username())) {
            throw new RegraNegocioException("Username já cadastrado");
        }

        validarUnicidadeEleitor(inputDTO, id);

        CandidatoModel candidatoAnterior = usuario.getCandidato();
        usuarioMapper.copyToEntity(inputDTO, usuario);
        prepararCandidato(usuario, inputDTO.candidato(), candidatoAnterior);

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO delete(Integer id) {
        UsuarioModel usuario = findOrFail(id);
        usuario.setAtivo(false);
        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO removerPerfilChefeSecao(Integer id) {
        UsuarioModel usuario = findOrFail(id);
        if (usuario.getChefeSecao() != null) {
            usuario.setChefeSecao(null);
            usuario.getRoles().remove("CHEFE_SECAO");
            usuario = usuarioRepository.save(usuario);
        }
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO removerPerfilEleitor(Integer id) {
        UsuarioModel usuario = findOrFail(id);
        if (usuario.getEleitor() != null) {
            usuario.setEleitor(null);
            usuario.getRoles().remove("ELEITOR");
            usuario = usuarioRepository.save(usuario);
        }
        return usuarioMapper.toDTO(usuario);
    }

    private void prepararCandidato(UsuarioModel usuario, CandidatoInputDTO candidatoInput, CandidatoModel candidatoAnterior) {
        if (candidatoInput == null || usuario.getCandidato() == null) {
            return;
        }

        CargoModel cargo = cargoRepository.findById(candidatoInput.idCargo())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Cargo não encontrado: " + candidatoInput.idCargo()));
        PartidoModel partido = partidoRepository.findById(candidatoInput.idPartido())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Partido não encontrado: " + candidatoInput.idPartido()));

        boolean dadosEleitoraisMudaram = candidatoAnterior == null
                || !Objects.equals(candidatoAnterior.getIdEleitoral(), candidatoInput.idEleitoral())
                || !candidatoAnterior.getCargo().getId().equals(cargo.getId())
                || !candidatoAnterior.getPartido().getId().equals(partido.getId());

        if (dadosEleitoraisMudaram) {
            if (!cargo.sufixoValido(candidatoInput.idEleitoral())) {
                if (cargo.getQtdDigitos() == 0) {
                    throw new RegraNegocioException("Para o cargo " + cargo.getNome()
                            + ", o sufixo eleitoral deve ser 0");
                }
                int maxValor = (int) Math.pow(10, cargo.getQtdDigitos()) - 1;
                throw new RegraNegocioException("Sufixo eleitoral de " + cargo.getNome()
                        + " deve estar entre 0 e " + maxValor);
            }

            if (candidatoRepository.existsByCargoIdAndPartidoIdAndIdEleitoral(
                    cargo.getId(), partido.getId(), candidatoInput.idEleitoral())) {
                throw new RegraNegocioException("Já existe candidato a " + cargo.getNome()
                        + " neste partido com este número eleitoral");
            }
        }

        usuario.getCandidato().setCargo(cargo);
        usuario.getCandidato().setPartido(partido);
    }

    private UsuarioModel findOrFail(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
    }

    private void validarUnicidadeEleitor(UsuarioInputDTO inputDTO, Integer usuarioId) {
        if (inputDTO.eleitor() != null) {
            if (usuarioId == null || !usuarioRepository.findById(usuarioId).map(UsuarioModel::getEleitor)
                    .filter(e -> e != null && e.getCpf().equals(inputDTO.eleitor().cpf())).isPresent()) {
                if (eleitorRepository.existsByCpf(inputDTO.eleitor().cpf())) {
                    throw new RegraNegocioException("CPF já cadastrado");
                }
            }
            if (usuarioId == null || !usuarioRepository.findById(usuarioId).map(UsuarioModel::getEleitor)
                    .filter(e -> e != null && e.getTitulo().equals(inputDTO.eleitor().titulo())).isPresent()) {
                if (eleitorRepository.existsByTitulo(inputDTO.eleitor().titulo())) {
                    throw new RegraNegocioException("Título já cadastrado");
                }
            }
        }
    }
}