package com.example.eleicao_service.feature.cargo.controller;

import com.example.eleicao_service.feature.cargo.model.CargoModel;
import com.example.eleicao_service.feature.cargo.service.CargoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Cargos", description = "Consulta dos cargos eletivos disponíveis")
@RequestMapping("/api/cargos")
public interface CargoController {

    @Operation(summary = "Listar cargos", description = "Retorna todos os cargos eletivos cadastrados.")
    @GetMapping
    ResponseEntity<List<CargoModel>> listarTodos();
}