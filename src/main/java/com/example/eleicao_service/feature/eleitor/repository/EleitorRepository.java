package com.example.eleicao_service.feature.eleitor.repository;


import com.example.eleicao_service.feature.eleitor.model.entity.EleitorModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EleitorRepository extends JpaRepository<EleitorModel, Long> {
    boolean existsByTituloEleitor(String tituloEleitor);
}