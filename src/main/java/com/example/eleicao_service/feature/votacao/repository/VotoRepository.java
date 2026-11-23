package com.example.eleicao_service.feature.votacao.repository;


import com.example.eleicao_service.feature.votacao.model.entity.VotoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<VotoModel, Long> {
    long countByEleicaoIdAndCandidatoIdUsuario(Long eleicaoId, Long candidatoId);
}
