package com.example.eleicao_service.feature.cargo.controller.impl;


import com.example.eleicao_service.feature.cargo.controller.CargoController;
import com.example.eleicao_service.feature.cargo.model.CargoModel;
import com.example.eleicao_service.feature.cargo.service.CargoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CargoControllerImpl implements CargoController {

    private final CargoService cargoService;

    @Override
    public ResponseEntity<List<CargoModel>> listarTodos() {
        return ResponseEntity.ok(cargoService.listarTodos());
    }
}