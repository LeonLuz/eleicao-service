package com.example.eleicao_service.feature.usuario.service;

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
public class UsuarioServiceImpl implements UsuarioService{

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;


    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO findById(Integer id){

        UsuarioModel user = findOrFail(id);
        return usuarioMapper.toDTO(user);
    }

    @Override
    @Transactional
    public UsuarioDTO insert(UsuarioInputDTO usuarioInputDTO) {

        if(usuarioRepository.existsByUsername(usuarioInputDTO.username())){
            throw new RuntimeException("Username já cadastrado");
        }

        UsuarioModel user = usuarioMapper.toEntity(usuarioInputDTO);
        user = usuarioRepository.save(user);
        return usuarioMapper.toDTO(user);
    }

    @Override
    @Transactional
    public UsuarioDTO update(UsuarioInputDTO usuarioInputDTO, Integer id) {

        UsuarioModel user = findOrFail(id);

        if(!user.getUsername().equalsIgnoreCase(usuarioInputDTO.username())
                && usuarioRepository.existsByUsername(usuarioInputDTO.username())){
            throw new RuntimeException("Username já cadastrado");
        }

        usuarioMapper.copyToEntity(usuarioInputDTO, user);
        user = usuarioRepository.save(user);
        return usuarioMapper.toDTO(user);
    }

    @Override
    @Transactional
    public UsuarioDTO delete (Integer id) {
        UsuarioModel user = findOrFail(id);
        usuarioRepository.delete(user);
        return usuarioMapper.toDTO(user);
    }

    private UsuarioModel findOrFail(Integer id){
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
}
