package com.example.eleicao_service.feature.cargo.repository;

import com.example.eleicao_service.feature.cargo.model.CargoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CargoRepository extends JpaRepository<CargoModel, Long> {
}