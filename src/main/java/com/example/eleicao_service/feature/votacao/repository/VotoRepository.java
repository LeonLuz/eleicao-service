package com.example.eleicao_service.feature.votacao.repository;


import com.example.eleicao_service.feature.votacao.model.entity.VotoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VotoRepository extends JpaRepository<VotoModel, UUID> {
    long countByEleicaoIdAndCandidatoIdUsuario(Long eleicaoId, Long candidatoId);
    boolean existsByCandidatoIdUsuario(Long candidatoId);
}
