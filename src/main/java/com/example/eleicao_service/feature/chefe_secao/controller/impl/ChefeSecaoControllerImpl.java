package com.example.eleicao_service.feature.chefe_secao.controller.impl;

import com.example.eleicao_service.feature.chefe_secao.controller.ChefeSecaoController;
import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoDTO;
import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoInputDTO;
import com.example.eleicao_service.feature.chefe_secao.service.ChefeSecaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chefes-secao")
@RequiredArgsConstructor
public class ChefeSecaoControllerImpl implements ChefeSecaoController {

    private final ChefeSecaoService chefeSecaoService;

    @Override
    @GetMapping
    public ResponseEntity<List<ChefeSecaoDTO>> findAll() {
        return ResponseEntity.ok(chefeSecaoService.findAll());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ChefeSecaoDTO> findById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(chefeSecaoService.findById(id));
    }

    @Override
    @PostMapping
    public ResponseEntity<ChefeSecaoDTO> insert(@RequestBody @Valid ChefeSecaoInputDTO chefeSecaoInputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chefeSecaoService.insert(chefeSecaoInputDTO));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ChefeSecaoDTO> update(@RequestBody @Valid ChefeSecaoInputDTO chefeSecaoInputDTO,
                                                @PathVariable("id") Integer id) {
        return ResponseEntity.ok(chefeSecaoService.update(chefeSecaoInputDTO, id));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<ChefeSecaoDTO> delete(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(chefeSecaoService.delete(id));
    }
}