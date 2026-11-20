package com.example.eleicao_service.feature.usuario.service;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.chefe_secao.mapper.ChefeSecaoMapper;
import com.example.eleicao_service.feature.eleitor.mapper.EleitorMapper;
import com.example.eleicao_service.feature.eleitor.repository.EleitorRepository;
import com.example.eleicao_service.feature.usuario.mapper.UsuarioMapper;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioDTO;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioInputDTO;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EleitorRepository eleitorRepository;
    private final UsuarioMapper usuarioMapper;
    private final ChefeSecaoMapper chefeSecaoMapper;
    private final EleitorMapper eleitorMapper;

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

        usuarioMapper.copyToEntity(inputDTO, usuario);

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioDTO delete(Integer id) {
        UsuarioModel usuario = findOrFail(id);
        usuario.setAtivo(false); // Soft delete
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

    private UsuarioModel findOrFail(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
    }

    // Método auxiliar para validar CPF e Título
    private void validarUnicidadeEleitor(UsuarioInputDTO inputDTO, Integer usuarioId) {
        if (inputDTO.eleitor() != null) {
            // Em caso de update, precisamos ignorar se o CPF/Título for do próprio usuário atual
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