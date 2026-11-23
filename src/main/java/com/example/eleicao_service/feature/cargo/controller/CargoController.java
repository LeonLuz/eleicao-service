package com.example.eleicao_service.feature.cargo.controller;

import com.example.eleicao_service.feature.cargo.model.CargoModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Cargos", description = "Consulta dos cargos eletivos disponíveis")
@RequestMapping("/api/cargos")
public interface CargoController {

    @Operation(summary = "Listar cargos", description = "Retorna todos os cargos eletivos cadastrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de cargos retornada com sucesso."),
            @ApiResponse(responseCode = "401", description = "Não autenticado.", content = @Content)
    })
    @GetMapping
    ResponseEntity<List<CargoModel>> listarTodos();
}