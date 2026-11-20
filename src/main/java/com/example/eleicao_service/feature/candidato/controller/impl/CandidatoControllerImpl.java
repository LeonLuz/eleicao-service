package com.example.eleicao_service.feature.candidato.controller.impl;

import com.example.eleicao_service.feature.candidato.controller.CandidatoController;
import com.example.eleicao_service.feature.candidato.model.Cargo;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoDTO;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoInputDTO;
import com.example.eleicao_service.feature.candidato.service.CandidatoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/candidatos")
@RequiredArgsConstructor
public class CandidatoControllerImpl implements CandidatoController {

    private final CandidatoService candidatoService;

    @Override
    public ResponseEntity<List<CandidatoDTO>> findAll(Cargo cargo) {
        return ResponseEntity.ok(candidatoService.findAll(cargo));
    }

    @Override
    public ResponseEntity<CandidatoDTO> findById(Integer id) {
        return ResponseEntity.ok(candidatoService.findById(id));
    }

    @Override
    public ResponseEntity<CandidatoDTO> insert(CandidatoInputDTO candidatoInputDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidatoService.insert(candidatoInputDTO));
    }

    @Override
    public ResponseEntity<CandidatoDTO> update(CandidatoInputDTO candidatoInputDTO, Integer id) {
        return ResponseEntity.ok(candidatoService.update(candidatoInputDTO, id));
    }

    @Override
    public ResponseEntity<CandidatoDTO> delete(Integer id) {
        return ResponseEntity.ok(candidatoService.delete(id));
    }
}
