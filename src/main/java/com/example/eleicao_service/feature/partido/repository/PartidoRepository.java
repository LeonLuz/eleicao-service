package com.example.eleicao_service.feature.partido.repository;

import com.example.eleicao_service.feature.partido.model.entity.PartidoModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartidoRepository extends JpaRepository<PartidoModel, Integer> {
    boolean existsByIdLegenda(Integer idLegenda);
    boolean existsByNome(String nome);
}