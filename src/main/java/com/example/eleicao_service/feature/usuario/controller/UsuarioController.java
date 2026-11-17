package com.example.eleicao_service.feature.usuario.controller;

import com.example.eleicao_service.feature.usuario.model.dto.UsuarioDTO;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioInputDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface UsuarioController {

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable("id") Integer id);

    @PostMapping
    public ResponseEntity<UsuarioDTO> insert(@RequestBody @Valid UsuarioInputDTO usuarioInputDTO);

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(@RequestBody @Valid UsuarioInputDTO usuarioInputDTO,
                                             @PathVariable("id") Integer id);

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDTO> delete(@PathVariable("id") Integer id);
}
