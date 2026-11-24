package com.example.eleicao_service.feature.candidato;

import com.example.eleicao_service.support.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CandidatoIT extends AbstractIntegrationTest {

    private String chefe;
    private long eleicao;
    private long partido;

    @BeforeEach
    void cenario() throws Exception {
        chefe = tokenChefe();
        eleicao = criarEleicao(chefe, "2026-10-04", 1);
        partido = criarPartido(chefe, "PA", 10);
    }

    @Test
    void cadastraConsultaAlteraEExcluiCandidato() throws Exception {
        long id = criarCandidato(chefe, "ana", 10, partido, CARGO_GOVERNADOR, eleicao);

        mockMvc.perform(comToken(get("/api/candidatos/{id}", id), chefe))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroEleitoral").value(10))
                .andExpect(jsonPath("$.partidoSigla").value("PA"))
                .andExpect(jsonPath("$.cargoNome").value("Governador"))
                .andExpect(jsonPath("$.idEleicao").value(eleicao))
                .andExpect(jsonPath("$.cpf").doesNotExist());

        mockMvc.perform(comToken(json(put("/api/candidatos/{id}", id),
                        candidatoJson("ana", cpfValido(500), 11, partido, CARGO_GOVERNADOR, eleicao)), chefe))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroEleitoral").value(11));

        mockMvc.perform(comToken(delete("/api/candidatos/{id}", id), chefe))
                .andExpect(status().isNoContent());
        mockMvc.perform(comToken(get("/api/candidatos/{id}", id), chefe))
                .andExpect(status().isNotFound());
    }

    @Test
    void candidatoCadastradoFazLoginComPerfilCandidato() throws Exception {
        criarCandidato(chefe, "ana", 10, partido, CARGO_GOVERNADOR, eleicao);

        mockMvc.perform(json(post("/auth/login"), """
                        {"username": "ana", "senha": "senha123"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfis").value(contains("CANDIDATO")));
    }

    @Test
    void listaCandidatosPorEleicaoECargo() throws Exception {
        criarCandidato(chefe, "gov1", 10, partido, CARGO_GOVERNADOR, eleicao);
        criarCandidato(chefe, "gov2", 20, partido, CARGO_GOVERNADOR, eleicao);
        criarCandidato(chefe, "sen1", 100, partido, CARGO_SENADOR, eleicao);

        mockMvc.perform(comToken(get("/api/candidatos/eleicao/{e}/cargo/{c}", eleicao, CARGO_GOVERNADOR), chefe))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].cargoNome").value(org.hamcrest.Matchers.everyItem(
                        org.hamcrest.Matchers.is("Governador"))));
    }

    @Test
    void mesmoNumeroNoMesmoCargoEEleicaoRetorna409() throws Exception {
        criarCandidato(chefe, "ana", 10, partido, CARGO_GOVERNADOR, eleicao);

        mockMvc.perform(comToken(json(post("/api/candidatos"),
                        candidatoJson("bia", cpfValido(600), 10, partido, CARGO_GOVERNADOR, eleicao)), chefe))
                .andExpect(status().isConflict());
    }

    @Test
    void mesmoNumeroEmCargoDiferenteEhPermitido() throws Exception {
        criarCandidato(chefe, "ana", 10, partido, CARGO_GOVERNADOR, eleicao);
        criarCandidato(chefe, "bia", 10, partido, CARGO_SENADOR, eleicao);
    }

    @Test
    void cpfDuplicadoEnviadoComPontuacaoRetorna409() throws Exception {
        String cpf = cpfValido(321);
        String cpfFormatado = cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9);
        mockMvc.perform(comToken(json(post("/api/candidatos"),
                        candidatoJson("ana", cpf, 10, partido, CARGO_GOVERNADOR, eleicao)), chefe))
                .andExpect(status().isCreated());

        mockMvc.perform(comToken(json(post("/api/candidatos"),
                        candidatoJson("bia", cpfFormatado, 20, partido, CARGO_GOVERNADOR, eleicao)), chefe))
                .andExpect(status().isConflict());
    }

    @Test
    void atualizarParaNumeroDeOutroCandidatoRetorna409() throws Exception {
        criarCandidato(chefe, "ana", 10, partido, CARGO_GOVERNADOR, eleicao);
        long bia = criarCandidato(chefe, "bia", 20, partido, CARGO_GOVERNADOR, eleicao);
        String cpfBia = jdbc.queryForObject("SELECT cpf FROM usuario WHERE id = ?", String.class, bia);

        mockMvc.perform(comToken(json(put("/api/candidatos/{id}", bia),
                        candidatoJson("bia", cpfBia, 10, partido, CARGO_GOVERNADOR, eleicao)), chefe))
                .andExpect(status().isConflict());
    }

    @Test
    void cpfInvalidoRetorna400() throws Exception {
        mockMvc.perform(comToken(json(post("/api/candidatos"),
                        candidatoJson("ana", "12345678900", 10, partido, CARGO_GOVERNADOR, eleicao)), chefe))
                .andExpect(status().isBadRequest());
    }

    @Test
    void partidoInexistenteRetorna404() throws Exception {
        mockMvc.perform(comToken(json(post("/api/candidatos"),
                        candidatoJson("ana", cpfValido(700), 10, 999_999, CARGO_GOVERNADOR, eleicao)), chefe))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarPorEleicaoOuCargoInexistenteRetorna404() throws Exception {
        mockMvc.perform(comToken(get("/api/candidatos/eleicao/{e}/cargo/{c}", 999_999, CARGO_GOVERNADOR), chefe))
                .andExpect(status().isNotFound());
        mockMvc.perform(comToken(get("/api/candidatos/eleicao/{e}/cargo/{c}", eleicao, 999_999), chefe))
                .andExpect(status().isNotFound());
    }

    @Test
    void apenasChefeDeSecaoCadastraEExcluiCandidatos() throws Exception {
        long id = criarCandidato(chefe, "ana", 10, partido, CARGO_GOVERNADOR, eleicao);
        criarEleitor(chefe, "maria");
        String eleitor = login("maria", "senha123");

        mockMvc.perform(comToken(json(post("/api/candidatos"),
                        candidatoJson("bia", cpfValido(800), 20, partido, CARGO_GOVERNADOR, eleicao)), eleitor))
                .andExpect(status().isForbidden());
        mockMvc.perform(comToken(delete("/api/candidatos/{id}", id), eleitor))
                .andExpect(status().isForbidden());

        // o eleitor pode consultar os candidatos disponíveis
        mockMvc.perform(comToken(get("/api/candidatos/eleicao/{e}/cargo/{c}", eleicao, CARGO_GOVERNADOR), eleitor))
                .andExpect(status().isOk());
    }

    @Test
    void naoPermiteCadastrarAlterarOuExcluirCandidatoComEleicaoAberta() throws Exception {
        long id = criarCandidato(chefe, "ana", 10, partido, CARGO_GOVERNADOR, eleicao);
        String cpfAna = jdbc.queryForObject("SELECT cpf FROM usuario WHERE id = ?", String.class, id);

        mockMvc.perform(comToken(patch("/api/eleicoes/{id}/abrir", eleicao), chefe))
                .andExpect(status().isOk());

        mockMvc.perform(comToken(json(post("/api/candidatos"),
                        candidatoJson("bia", cpfValido(850), 20, partido, CARGO_GOVERNADOR, eleicao)), chefe))
                .andExpect(status().isConflict());

        mockMvc.perform(comToken(json(put("/api/candidatos/{id}", id),
                        candidatoJson("ana", cpfAna, 15, partido, CARGO_GOVERNADOR, eleicao)), chefe))
                .andExpect(status().isConflict());

        mockMvc.perform(comToken(delete("/api/candidatos/{id}", id), chefe))
                .andExpect(status().isConflict());
    }
}
