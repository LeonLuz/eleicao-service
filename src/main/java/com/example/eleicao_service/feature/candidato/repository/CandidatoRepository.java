package com.example.eleicao_service.feature.candidato.repository;

import com.example.eleicao_service.feature.candidato.model.Cargo;
import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidatoRepository extends JpaRepository<CandidatoModel, Integer> {

    boolean existsByNumeroAndCargo(Integer numero, Cargo cargo);

    List<CandidatoModel> findByCargoOrderByNumero(Cargo cargo);

    List<CandidatoModel> findAllByOrderByCargoAscNumeroAsc();
}
