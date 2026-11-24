package com.example.eleicao_service.feature.eleicao;

import com.example.eleicao_service.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EleicaoIT extends AbstractIntegrationTest {

    @Test
    void cicloDeVidaCompletoAgendadaAbertaEncerrada() throws Exception {
        String chefe = tokenChefe();
        long id = criarEleicao(chefe, "2026-10-04", 1);

        mockMvc.perform(comToken(get("/api/eleicoes/{id}", id), chefe))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AGENDADA"))
                .andExpect(jsonPath("$.turno").value(1))
                .andExpect(jsonPath("$.dataEleicao").value("2026-10-04"));

        mockMvc.perform(comToken(patch("/api/eleicoes/{id}/abrir", id), chefe))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ABERTA"));

        mockMvc.perform(comToken(patch("/api/eleicoes/{id}/encerrar", id), chefe))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ENCERRADA"));
    }

    @Test
    void transicoesInvalidasDeStatusRetornam409() throws Exception {
        String chefe = tokenChefe();
        long id = criarEleicao(chefe, "2026-10-04", 1);

        mockMvc.perform(comToken(patch("/api/eleicoes/{id}/encerrar", id), chefe))
                .andExpect(status().isConflict());

        mockMvc.perform(comToken(patch("/api/eleicoes/{id}/abrir", id), chefe)).andExpect(status().isOk());
        mockMvc.perform(comToken(patch("/api/eleicoes/{id}/abrir", id), chefe))
                .andExpect(status().isConflict());

        mockMvc.perform(comToken(patch("/api/eleicoes/{id}/encerrar", id), chefe)).andExpect(status().isOk());
        mockMvc.perform(comToken(patch("/api/eleicoes/{id}/abrir", id), chefe))
                .andExpect(status().isConflict());
    }

    @Test
    void naoPermiteDuasEleicoesNaMesmaDataETurno() throws Exception {
        String chefe = tokenChefe();
        criarEleicao(chefe, "2026-10-04", 1);
        criarEleicao(chefe, "2026-10-04", 2);

        mockMvc.perform(comToken(json(post("/api/eleicoes"), """
                        {"descricao": "Repetida", "dataEleicao": "2026-10-04", "turno": 1,
                         "horarioAbertura": "08:00:00", "horarioEncerramento": "17:00:00"}"""), chefe))
                .andExpect(status().isConflict());
    }

    @Test
    void horarioDeEncerramentoAntesDaAberturaRetorna409() throws Exception {
        mockMvc.perform(comToken(json(post("/api/eleicoes"), """
                        {"descricao": "Invertida", "dataEleicao": "2026-10-04", "turno": 1,
                         "horarioAbertura": "17:00:00", "horarioEncerramento": "08:00:00"}"""), tokenChefe()))
                .andExpect(status().isConflict());
    }

    @Test
    void camposObrigatoriosAusentesRetornam400() throws Exception {
        mockMvc.perform(comToken(json(post("/api/eleicoes"), """
                        {"descricao": "Sem data"}"""), tokenChefe()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void apenasChefeDeSecaoGerenciaEleicoes() throws Exception {
        String chefe = tokenChefe();
        long id = criarEleicao(chefe, "2026-10-04", 1);
        criarEleitor(chefe, "maria");
        String eleitor = login("maria", "senha123");

        mockMvc.perform(comToken(json(post("/api/eleicoes"), """
                        {"descricao": "X", "dataEleicao": "2026-11-01", "turno": 1,
                         "horarioAbertura": "08:00:00", "horarioEncerramento": "17:00:00"}"""), eleitor))
                .andExpect(status().isForbidden());
        mockMvc.perform(comToken(patch("/api/eleicoes/{id}/abrir", id), eleitor))
                .andExpect(status().isForbidden());

        // consultar a situação da eleição é permitido a qualquer usuário autenticado
        mockMvc.perform(comToken(get("/api/eleicoes/{id}", id), eleitor))
                .andExpect(status().isOk());
    }

    @Test
    void eleicaoInexistenteRetorna404() throws Exception {
        mockMvc.perform(comToken(get("/api/eleicoes/{id}", 999_999), tokenChefe()))
                .andExpect(status().isNotFound());
    }

    @Test
    void turnoInvalidoOuParametroDeTipoInvalidoRetorna400() throws Exception {
        String chefe = tokenChefe();
        mockMvc.perform(comToken(json(post("/api/eleicoes"), """
                        {"descricao": "Turno Zero", "dataEleicao": "2026-10-04", "turno": 0,
                         "horarioAbertura": "08:00:00", "horarioEncerramento": "17:00:00"}"""), chefe))
                .andExpect(status().isBadRequest());

        mockMvc.perform(comToken(json(post("/api/eleicoes"), """
                        {"descricao": "Turno Três", "dataEleicao": "2026-10-04", "turno": 3,
                         "horarioAbertura": "08:00:00", "horarioEncerramento": "17:00:00"}"""), chefe))
                .andExpect(status().isBadRequest());

        mockMvc.perform(comToken(get("/api/eleicoes/nao-numerico"), chefe))
                .andExpect(status().isBadRequest());
    }
}
