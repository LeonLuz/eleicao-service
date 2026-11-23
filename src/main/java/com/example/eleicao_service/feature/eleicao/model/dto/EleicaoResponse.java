package com.example.eleicao_service.feature.eleicao.model.dto;

import com.example.eleicao_service.feature.eleicao.model.StatusEleicao;
import java.time.LocalDate;
import java.time.LocalTime;

public record EleicaoResponse(
        Long id,
        String descricao,
        LocalDate dataEleicao,
        Integer turno,
        LocalTime horarioAbertura,
        LocalTime horarioEncerramento,
        StatusEleicao status
) {}