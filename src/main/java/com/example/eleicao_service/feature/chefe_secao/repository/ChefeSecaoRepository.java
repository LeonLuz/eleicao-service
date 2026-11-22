package com.example.eleicao_service.feature.chefe_secao.repository;

import com.example.eleicao_service.feature.chefe_secao.model.entity.ChefeSecaoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChefeSecaoRepository extends JpaRepository<ChefeSecaoModel, Integer> {

    List<ChefeSecaoModel> findAllByOrderByNomeAsc();
}