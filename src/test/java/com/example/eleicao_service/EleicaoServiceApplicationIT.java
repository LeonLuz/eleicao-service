package com.example.eleicao_service;

import com.example.eleicao_service.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EleicaoServiceApplicationIT extends AbstractIntegrationTest {

    @Test
    void contextoSobeECriaOChefeDeSecaoInicial() {
        Integer admins = jdbc.queryForObject(
                "SELECT COUNT(*) FROM usuario WHERE username = ? AND perfil = 'CHEFE_SECAO'",
                Integer.class, ADMIN_USERNAME);
        assertThat(admins).isEqualTo(1);
    }

    @Test
    void cargosDoMinimundoEstaoCadastrados() {
        assertThat(jdbc.queryForList("SELECT nome FROM cargo", String.class))
                .containsExactlyInAnyOrder("Deputado Estadual", "Deputado Federal", "Senador",
                        "Governador", "Presidente da República");
    }
}
