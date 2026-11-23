package com.example.eleicao_service.feature.votacao.repository;


import com.example.eleicao_service.feature.votacao.model.entity.ControleVotoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControleVotoRepository extends JpaRepository<ControleVotoModel, Long> {
    boolean existsByEleicaoIdAndEleitorIdUsuarioAndCargoId(Long eleicaoId, Long eleitorId, Long cargoId);
}