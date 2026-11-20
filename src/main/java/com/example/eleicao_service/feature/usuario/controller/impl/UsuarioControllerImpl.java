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

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @Override
    @PostMapping
    public ResponseEntity<UsuarioDTO> insert(@RequestBody @Valid UsuarioInputDTO inputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.insert(inputDTO));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(@RequestBody @Valid UsuarioInputDTO inputDTO, @PathVariable("id") Integer id) {
        return ResponseEntity.ok(usuarioService.update(inputDTO, id));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<UsuarioDTO> delete(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(usuarioService.delete(id));
    }

    @Override
    @DeleteMapping("/{id}/chefe-secao")
    public ResponseEntity<UsuarioDTO> removerChefe(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(usuarioService.removerPerfilChefeSecao(id));
    }

    @Override
    @DeleteMapping("/{id}/eleitor")
    public ResponseEntity<UsuarioDTO> removerEleitor(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(usuarioService.removerPerfilEleitor(id));
    }
}