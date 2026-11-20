package com.example.eleicao_service.feature.candidato.controller;

import com.example.eleicao_service.feature.candidato.model.Cargo;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoDTO;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoInputDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface CandidatoController {

    @GetMapping
    ResponseEntity<List<CandidatoDTO>> findAll(@RequestParam(name = "cargo", required = false) Cargo cargo);

    @GetMapping("/{id}")
    ResponseEntity<CandidatoDTO> findById(@PathVariable("id") Integer id);

    @PostMapping
    ResponseEntity<CandidatoDTO> insert(@RequestBody @Valid CandidatoInputDTO candidatoInputDTO);

    @PutMapping("/{id}")
    ResponseEntity<CandidatoDTO> update(@RequestBody @Valid CandidatoInputDTO candidatoInputDTO,
                                        @PathVariable("id") Integer id);

    @DeleteMapping("/{id}")
    ResponseEntity<CandidatoDTO> delete(@PathVariable("id") Integer id);
}
