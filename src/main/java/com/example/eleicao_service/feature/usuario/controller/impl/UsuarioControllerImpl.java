package com.example.eleicao_service.feature.usuario.controller.impl;

import com.example.eleicao_service.feature.usuario.controller.UsuarioController;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioDTO;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioInputDTO;
import com.example.eleicao_service.feature.usuario.service.UsuarioServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class UsuarioControllerImpl implements UsuarioController {

    private final UsuarioServiceImpl usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioDTO> insert(@RequestBody @Valid UsuarioInputDTO usuarioInputDTO){
        UsuarioDTO usuarioDTO = usuarioService.insert(usuarioInputDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDTO);
    }
}
