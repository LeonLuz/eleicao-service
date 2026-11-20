package com.example.eleicao_service.feature.usuario.controller;

import com.example.eleicao_service.feature.usuario.model.dto.UsuarioDTO;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioInputDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

@Tag(name = "Usuários", description = "Gerenciamento do agregado de Usuários, Eleitores e Chefes de Seção")
public interface UsuarioController {

    @Operation(summary = "Busca um usuário por ID")
    ResponseEntity<UsuarioDTO> findById(Integer id);

    @Operation(summary = "Cria um novo usuário e seus perfis (Eleitor/Chefe)")
    ResponseEntity<UsuarioDTO> insert(UsuarioInputDTO inputDTO);

    @Operation(summary = "Atualiza um usuário e/ou adiciona/edita seus perfis")
    ResponseEntity<UsuarioDTO> update(UsuarioInputDTO inputDTO, Integer id);

    @Operation(summary = "Desativa um usuário (Soft Delete)")
    ResponseEntity<UsuarioDTO> delete(Integer id);

    @Operation(summary = "Remove o perfil de Chefe de Seção de um usuário")
    ResponseEntity<UsuarioDTO> removerChefe(Integer id);

    @Operation(summary = "Remove o perfil de Eleitor de um usuário")
    ResponseEntity<UsuarioDTO> removerEleitor(Integer id);
}