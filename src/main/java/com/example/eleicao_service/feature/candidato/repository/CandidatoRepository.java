package com.example.eleicao_service.feature.candidato.repository;

import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidatoRepository extends JpaRepository<CandidatoModel, Integer> {

    boolean existsByCargoIdAndPartidoIdAndIdEleitoral(Integer idCargo, Integer idPartido, Integer idEleitoral);

    List<CandidatoModel> findByCargoIdOrderByPartidoIdLegendaAscIdEleitoralAsc(Integer idCargo);

    List<CandidatoModel> findAllByOrderByCargoIdAscPartidoIdLegendaAscIdEleitoralAsc();
}