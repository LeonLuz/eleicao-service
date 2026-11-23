package com.example.eleicao_service.feature.candidato.repository;

import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CandidatoRepository extends JpaRepository<CandidatoModel, Long> {
    boolean existsByNumeroEleitoralAndCargoIdAndEleicaoId(Integer numeroEleitoral, Long cargoId, Long eleicaoId);
    List<CandidatoModel> findByEleicaoIdAndCargoId(Long eleicaoId, Long cargoId);
    List<CandidatoModel> findByEleicaoId(Long eleicaoId);
}