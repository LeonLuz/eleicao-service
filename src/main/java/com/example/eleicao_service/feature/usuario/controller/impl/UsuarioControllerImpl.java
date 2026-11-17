package com.example.eleicao_service.feature.usuario.controller.impl;

import com.example.eleicao_service.feature.usuario.controller.UsuarioController;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioDTO;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioInputDTO;
import com.example.eleicao_service.feature.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioControllerImpl implements UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable("id") Integer id){
        UsuarioDTO usuarioDTO = usuarioService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(usuarioDTO);
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> insert(@RequestBody @Valid UsuarioInputDTO usuarioInputDTO){
        UsuarioDTO usuarioDTO = usuarioService.insert(usuarioInputDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(@RequestBody @Valid UsuarioInputDTO usuarioInputDTO,
                                             @PathVariable("id") Integer id){
        UsuarioDTO usuarioDTO = usuarioService.update(usuarioInputDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(usuarioDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDTO> delete(@PathVariable("id") Integer id){
        UsuarioDTO usuarioDTO = usuarioService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(usuarioDTO);
    }
}
