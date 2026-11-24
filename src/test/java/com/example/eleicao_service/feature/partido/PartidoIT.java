package com.example.eleicao_service.feature.partido;

import com.example.eleicao_service.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PartidoIT extends AbstractIntegrationTest {

    @Test
    void chefeCadastraEListaPartidos() throws Exception {
        String chefe = tokenChefe();

        mockMvc.perform(comToken(json(post("/api/partidos"), """
                        {"nome": "Partido Azul", "sigla": "PA", "numero": 10}"""), chefe))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Partido Azul"))
                .andExpect(jsonPath("$.sigla").value("PA"))
                .andExpect(jsonPath("$.numero").value(10));

        criarPartido(chefe, "PB", 20);

        mockMvc.perform(comToken(get("/api/partidos"), chefe))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].sigla").value(containsInAnyOrder("PA", "PB")));
    }

    @Test
    void naoPermiteNumeroSiglaOuNomeDuplicados() throws Exception {
        String chefe = tokenChefe();
        criarPartido(chefe, "PA", 10);

        mockMvc.perform(comToken(json(post("/api/partidos"), """
                        {"nome": "Outro Partido", "sigla": "OP", "numero": 10}"""), chefe))
                .andExpect(status().isConflict());

        mockMvc.perform(comToken(json(post("/api/partidos"), """
                        {"nome": "Partido Alternativo", "sigla": "PA", "numero": 30}"""), chefe))
                .andExpect(status().isConflict());

        mockMvc.perform(comToken(json(post("/api/partidos"), """
                        {"nome": "Partido PA", "sigla": "PPA", "numero": 40}"""), chefe))
                .andExpect(status().isConflict());
    }

    @Test
    void camposObrigatoriosAusentesOuInvalidosRetornam400() throws Exception {
        String chefe = tokenChefe();
        mockMvc.perform(comToken(json(post("/api/partidos"), """
                        {"nome": "", "sigla": "PA"}"""), chefe))
                .andExpect(status().isBadRequest());

        mockMvc.perform(comToken(json(post("/api/partidos"), """
                        {"nome": "Partido Zero", "sigla": "PZ", "numero": 0}"""), chefe))
                .andExpect(status().isBadRequest());
    }

    @Test
    void apenasChefeCadastraMasEleitorECandidatoPodemListar() throws Exception {
        String chefe = tokenChefe();
        long partido = criarPartido(chefe, "PA", 10);
        long eleicao = criarEleicao(chefe, "2026-10-04", 1);
        criarEleitor(chefe, "maria");
        criarCandidato(chefe, "ana", 10, partido, CARGO_GOVERNADOR, eleicao);

        String eleitor = login("maria", "senha123");
        String candidato = login("ana", "senha123");
        String novoPartido = """
                {"nome": "Partido Verde", "sigla": "PV", "numero": 43}""";

        mockMvc.perform(comToken(json(post("/api/partidos"), novoPartido), eleitor))
                .andExpect(status().isForbidden());
        mockMvc.perform(comToken(json(post("/api/partidos"), novoPartido), candidato))
                .andExpect(status().isForbidden());

        mockMvc.perform(comToken(get("/api/partidos"), eleitor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(comToken(get("/api/partidos"), candidato))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void semAutenticacaoRetorna401() throws Exception {
        mockMvc.perform(get("/api/partidos"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(json(post("/api/partidos"), """
                        {"nome": "Partido Azul", "sigla": "PA", "numero": 10}"""))
                .andExpect(status().isUnauthorized());
    }
}
