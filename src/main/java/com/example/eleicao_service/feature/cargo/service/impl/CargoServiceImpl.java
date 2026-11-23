package com.example.eleicao_service.feature.cargo.service.impl;


import com.example.eleicao_service.feature.cargo.model.CargoModel;
import com.example.eleicao_service.feature.cargo.repository.CargoRepository;
import com.example.eleicao_service.feature.cargo.service.CargoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CargoServiceImpl implements CargoService {

    private final CargoRepository cargoRepository;

    @Override
    public List<CargoModel> listarTodos() {
        return cargoRepository.findAll();
    }
}