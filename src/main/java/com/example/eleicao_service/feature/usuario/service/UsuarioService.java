package com.example.eleicao_service.feature.usuario.service;

import com.example.eleicao_service.feature.usuario.model.dto.UsuarioDTO;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioInputDTO;

public interface UsuarioService {

    UsuarioDTO findById(Integer id);

    UsuarioDTO insert(UsuarioInputDTO usuarioInputDTO);

    UsuarioDTO update(UsuarioInputDTO usuarioInputDTO, Integer id);

    UsuarioDTO delete(Integer id);
}
