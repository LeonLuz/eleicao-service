package com.example.eleicao_service.feature.usuario.service;

import com.example.eleicao_service.feature.usuario.model.dto.UsuarioDTO;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioInputDTO;

public interface UsuarioService {

    UsuarioDTO findById(Integer id);

    UsuarioDTO insert(UsuarioInputDTO inputDTO);

    UsuarioDTO update(UsuarioInputDTO inputDTO, Integer id);

    UsuarioDTO delete(Integer id);

    UsuarioDTO removerPerfilChefeSecao(Integer id);

    UsuarioDTO removerPerfilEleitor(Integer id);
}