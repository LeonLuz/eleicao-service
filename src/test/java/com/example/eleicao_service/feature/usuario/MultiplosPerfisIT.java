package com.example.eleicao_service.feature.usuario;

import com.example.eleicao_service.support.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MultiplosPerfisIT extends AbstractIntegrationTest {

    private String chefe;
    private long eleicao;
    private long partido;

    @BeforeEach
    void cenario() throws Exception {
        chefe = tokenChefe();
        eleicao = criarEleicao(chefe, "2026-10-04", 1);
        partido = criarPartido(chefe, "PA", 10);
    }

    private String cpfDoUsuario(long id) {
        return jdbc.queryForObject("SELECT cpf FROM usuario WHERE id = ?", String.class, id);
    }

    private String chefeJson(String username, String cpf) {
        return """
               {"nome": "Chefe %s", "cpf": "%s", "username": "%s", "senha": "segredo"}
               """.formatted(username, cpf, username);
    }

    @Test
    void eleitorCadastradoComoCandidatoMantemOsDoisPerfisEPodeVotar() throws Exception {
        long eleitor = criarEleitor(chefe, "maria");

        long candidato = lerId(mockMvc.perform(comToken(json(post("/api/candidatos"),
                        candidatoJson("maria", cpfDoUsuario(eleitor), 10, partido, CARGO_GOVERNADOR, eleicao)), chefe))
                .andExpect(status().isCreated()), "$.idUsuario");

        assertThat(candidato).isEqualTo(eleitor);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM usuario WHERE username = 'maria'", Integer.class))
                .isEqualTo(1);

        mockMvc.perform(json(post("/auth/login"), """
                        {"username": "maria", "senha": "senha123"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfis").value(containsInAnyOrder("ELEITOR", "CANDIDATO")));

        mockMvc.perform(comToken(patch("/api/eleicoes/{id}/abrir", eleicao), chefe)).andExpect(status().isOk());
        mockMvc.perform(comToken(json(post("/api/votacao/votar"), """
                        {"idEleicao": %d, "idCargo": %d, "idCandidato": %d}
                        """.formatted(eleicao, CARGO_GOVERNADOR, candidato)), login("maria", "senha123")))
                .andExpect(status().isCreated());
    }

    @Test
    void eleitorQueTambemEhCandidatoAcompanhaTodosOsCargos() throws Exception {
        long eleitor = criarEleitor(chefe, "maria");
        mockMvc.perform(comToken(json(post("/api/candidatos"),
                        candidatoJson("maria", cpfDoUsuario(eleitor), 10, partido, CARGO_GOVERNADOR, eleicao)), chefe))
                .andExpect(status().isCreated());
        criarCandidato(chefe, "sen1", 100, partido, CARGO_SENADOR, eleicao);
        mockMvc.perform(comToken(patch("/api/eleicoes/{id}/abrir", eleicao), chefe)).andExpect(status().isOk());

        mockMvc.perform(comToken(get("/api/votacao/eleicao/{id}/acompanhamento", eleicao), login("maria", "senha123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nomeCargo").value(containsInAnyOrder("Governador", "Senador")));
    }

    @Test
    void eleitorJaCadastradoNaoPodeSerCadastradoDeNovo() throws Exception {
        long eleitor = criarEleitor(chefe, "maria");

        mockMvc.perform(comToken(json(post("/api/eleitores"), """
                        {"nome": "Maria", "cpf": "%s", "username": "maria2", "senha": "senha123",
                         "tituloEleitor": "999999999999", "zona": "001", "secao": "0001"}
                        """.formatted(cpfDoUsuario(eleitor))), chefe))
                .andExpect(status().isConflict());
    }

    @Test
    void chefeDeSecaoNaoPodeSerCandidato() throws Exception {
        String cpf = cpfValido(901);
        mockMvc.perform(comToken(json(post("/api/usuarios/chefe-secao"), chefeJson("chefe2", cpf)), chefe))
                .andExpect(status().isCreated());

        mockMvc.perform(comToken(json(post("/api/candidatos"),
                        candidatoJson("chefe2", cpf, 10, partido, CARGO_GOVERNADOR, eleicao)), chefe))
                .andExpect(status().isConflict());
    }

    @Test
    void candidatoNaoPodeSerChefeDeSecao() throws Exception {
        long candidato = criarCandidato(chefe, "ana", 10, partido, CARGO_GOVERNADOR, eleicao);

        mockMvc.perform(comToken(json(post("/api/usuarios/chefe-secao"),
                        chefeJson("ana", cpfDoUsuario(candidato))), chefe))
                .andExpect(status().isConflict());
    }

    @Test
    void eleitorPodeTambemSerChefeDeSecao() throws Exception {
        long eleitor = criarEleitor(chefe, "maria");

        mockMvc.perform(comToken(json(post("/api/usuarios/chefe-secao"),
                        chefeJson("maria", cpfDoUsuario(eleitor))), chefe))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(eleitor))
                .andExpect(jsonPath("$.perfis").value(containsInAnyOrder("ELEITOR", "CHEFE_SECAO")));
    }
}
