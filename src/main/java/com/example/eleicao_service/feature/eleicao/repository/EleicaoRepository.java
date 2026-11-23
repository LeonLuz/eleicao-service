package com.example.eleicao_service.feature.eleicao.repository;

import com.example.eleicao_service.feature.eleicao.model.entity.EleicaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;

@Repository
public interface EleicaoRepository extends JpaRepository<EleicaoModel, Long> {
    boolean existsByDataEleicaoAndTurno(LocalDate dataEleicao, Integer turno);
}