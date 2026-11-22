package com.example.eleicao_service.feature.chefe_secao.service;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.chefe_secao.mapper.ChefeSecaoMapper;
import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoDTO;
import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoInputDTO;
import com.example.eleicao_service.feature.chefe_secao.model.entity.ChefeSecaoModel;
import com.example.eleicao_service.feature.chefe_secao.repository.ChefeSecaoRepository;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChefeSecaoServiceImpl implements ChefeSecaoService {

    private static final String ROLE_USER = "USER";
    private static final String ROLE_CHEFE_SECAO = "CHEFE_SECAO";
    private static final String ROLE_CANDIDATO = "CANDIDATO";

    private final ChefeSecaoRepository chefeSecaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ChefeSecaoMapper chefeSecaoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ChefeSecaoDTO> findAll() {
        return chefeSecaoRepository.findAllByOrderByNomeAsc()
                .stream()
                .map(chefeSecaoMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ChefeSecaoDTO findById(Integer id) {
        ChefeSecaoModel chefeSecaoModel = findOrFail(id);
        return chefeSecaoMapper.toDTO(chefeSecaoModel);
    }

    @Override
    @Transactional
    public ChefeSecaoDTO insert(ChefeSecaoInputDTO input) {
        UsuarioModel usuario = usuarioRepository.findById(input.id())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));

        if (Boolean.FALSE.equals(usuario.getAtivo())) {
            throw new RegraNegocioException("Não é possível cadastrar chefe de seção para um usuário inativo");
        }
        if (usuario.getRoles() != null && usuario.getRoles().contains(ROLE_CANDIDATO)) {
            throw new RegraNegocioException("Usuário já é candidato e não pode receber o perfil de chefe de seção");
        }
        if (chefeSecaoRepository.existsById(usuario.getId())) {
            throw new RegraNegocioException("Usuário já está cadastrado como chefe de seção");
        }

        ChefeSecaoModel chefe = chefeSecaoMapper.toEntity(input);
        chefe.setUsuario(usuario);

        if (usuario.getRoles() == null) {
            usuario.setRoles(new ArrayList<>(List.of(ROLE_USER, ROLE_CHEFE_SECAO)));
        } else if (!usuario.getRoles().contains(ROLE_CHEFE_SECAO)) {
            usuario.getRoles().add(ROLE_CHEFE_SECAO);
        }

        chefe = chefeSecaoRepository.save(chefe);
        return chefeSecaoMapper.toDTO(chefe);
    }

    @Override
    @Transactional
    public ChefeSecaoDTO update(ChefeSecaoInputDTO chefeSecaoInputDTO, Integer id) {
        ChefeSecaoModel chefeSecaoModel = findOrFail(id);

        chefeSecaoMapper.copyToEntity(chefeSecaoInputDTO, chefeSecaoModel);
        chefeSecaoModel = chefeSecaoRepository.save(chefeSecaoModel);
        return chefeSecaoMapper.toDTO(chefeSecaoModel);
    }

    @Override
    @Transactional
    public ChefeSecaoDTO delete(Integer id) {
        ChefeSecaoModel chefe = findOrFail(id);
        ChefeSecaoDTO dto = chefeSecaoMapper.toDTO(chefe);

        UsuarioModel usuario = chefe.getUsuario();
        if (usuario != null) {
            usuario.setChefeSecao(null);
            if (usuario.getRoles() != null) {
                usuario.getRoles().remove(ROLE_CHEFE_SECAO);
            }
        }

        chefeSecaoRepository.delete(chefe);
        return dto;
    }

    private ChefeSecaoModel findOrFail(Integer id) {
        return chefeSecaoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Chefe de seção não encontrado"));
    }
}