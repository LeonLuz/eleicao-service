package com.example.eleicao_service.feature.chefe_secao.controller;

import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoDTO;
import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoInputDTO;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioDTO;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioInputDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface ChefeSecaoController {

    @GetMapping("/{id}")
    ResponseEntity<ChefeSecaoDTO> findById(@PathVariable("id") Integer id);

    @PostMapping
    ResponseEntity<ChefeSecaoDTO> insert(@RequestBody @Valid ChefeSecaoInputDTO chefeSecaoInputDTO);

    @PutMapping("/{id}")
    public ResponseEntity<ChefeSecaoDTO> update(@RequestBody @Valid ChefeSecaoInputDTO chefeSecaoInputDTO,
                                             @PathVariable("id") Integer id);

    @DeleteMapping("/{id}")
    public ResponseEntity<ChefeSecaoDTO> delete(@PathVariable("id") Integer id);
}
