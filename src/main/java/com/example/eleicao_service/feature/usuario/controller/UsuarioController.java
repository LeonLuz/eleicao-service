package com.example.eleicao_service.feature.usuario.controller;

import com.example.eleicao_service.feature.usuario.model.dto.UsuarioDTO;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioInputDTO;
import org.springframework.http.ResponseEntity;

public interface UsuarioController {

    ResponseEntity<UsuarioDTO> findById(Integer id);

    ResponseEntity<UsuarioDTO> insert(UsuarioInputDTO inputDTO);

    ResponseEntity<UsuarioDTO> update(UsuarioInputDTO inputDTO, Integer id);

    ResponseEntity<UsuarioDTO> delete(Integer id);

    ResponseEntity<UsuarioDTO> removerChefe(Integer id);

    ResponseEntity<UsuarioDTO> removerEleitor(Integer id);
}