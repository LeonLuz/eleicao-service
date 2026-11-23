package com.example.eleicao_service.feature.votacao.model.dto;

import java.time.LocalDateTime;

public record VotoConfirmacaoResponse(
        String mensagem,
        String cargo,
        LocalDateTime dataHora
) {}
