package com.example.eleicao_service.feature.candidato.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Cargo {
    DEPUTADO_ESTADUAL("Deputado Estadual", 5),
    DEPUTADO_FEDERAL("Deputado Federal", 4),
    SENADOR("Senador", 3),
    GOVERNADOR("Governador", 2),
    PRESIDENTE("Presidente da República", 2);

    private final String descricao;

    /** Quantidade de dígitos do número de identificação eleitoral para o cargo. */
    private final int digitosNumero;

    public boolean numeroValido(Integer numero) {
        return numero != null && numero > 0 && String.valueOf(numero).length() == digitosNumero;
    }
}
