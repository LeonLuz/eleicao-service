package com.example.eleicao_service.feature.eleitor;

import com.example.eleicao_service.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EleitorIT extends AbstractIntegrationTest {

    private String eleitorJson(String username, String cpf, String titulo, String secao) {
        return """
               {"nome": "Eleitor %s", "cpf": "%s", "username": "%s", "senha": "senha123",
                "tituloEleitor": "%s", "zona": "001", "secao": "%s"}
               """.formatted(username, cpf, username, titulo, secao);
    }

    @Test
    void crudCompletoPeloChefeDeSecao() throws Exception {
        String chefe = tokenChefe();
        long id = criarEleitor(chefe, "maria");

        mockMvc.perform(comToken(get("/api/eleitores/{id}", id), chefe))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("maria"))
                .andExpect(jsonPath("$.zona").value("001"));

        mockMvc.perform(comToken(get("/api/eleitores"), chefe))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(comToken(json(put("/api/eleitores/{id}", id),
                        eleitorJson("maria", cpfValido(1), "000000000001", "0099")), chefe))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.secao").value("0099"));

        mockMvc.perform(comToken(delete("/api/eleitores/{id}", id), chefe))
                .andExpect(status().isNoContent());
        mockMvc.perform(comToken(get("/api/eleitores/{id}", id), chefe))
                .andExpect(status().isNotFound());
    }

    @Test
    void eleitorCadastradoConsegueFazerLogin() throws Exception {
        criarEleitor(tokenChefe(), "maria");

        mockMvc.perform(json(post("/auth/login"), """
                        {"username": "maria", "senha": "senha123"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil").value("ELEITOR"));

        String senhaNoBanco = jdbc.queryForObject("SELECT senha FROM usuario WHERE username = 'maria'", String.class);
        assertThat(senhaNoBanco).isNotEqualTo("senha123").startsWith("$2"); // BCrypt
    }

    @Test
    void tituloDuplicadoRetorna409() throws Exception {
        String chefe = tokenChefe();
        mockMvc.perform(comToken(json(post("/api/eleitores"),
                        eleitorJson("a", cpfValido(11), "111111111111", "0001")), chefe))
                .andExpect(status().isCreated());

        mockMvc.perform(comToken(json(post("/api/eleitores"),
                        eleitorJson("b", cpfValido(12), "111111111111", "0001")), chefe))
                .andExpect(status().isConflict());
    }

    @Test
    void usernameDuplicadoRetorna409() throws Exception {
        String chefe = tokenChefe();
        criarEleitor(chefe, "maria");

        mockMvc.perform(comToken(json(post("/api/eleitores"),
                        eleitorJson("maria", cpfValido(50), "222222222222", "0001")), chefe))
                .andExpect(status().isConflict());
    }

    @Test
    void atualizarParaTituloOuUsernameDeOutroEleitorRetorna409() throws Exception {
        String chefe = tokenChefe();
        criarEleitor(chefe, "maria");                 // título 000000000001
        long joao = criarEleitor(chefe, "joao");     // título 000000000002, cpfValido(2)

        mockMvc.perform(comToken(json(put("/api/eleitores/{id}", joao),
                        eleitorJson("joao", cpfValido(2), "000000000001", "0001")), chefe))
                .andExpect(status().isConflict());
        mockMvc.perform(comToken(json(put("/api/eleitores/{id}", joao),
                        eleitorJson("maria", cpfValido(2), "000000000002", "0001")), chefe))
                .andExpect(status().isConflict());
    }

    @Test
    void exemploDoSwaggerParaTituloPassaNaValidacao() throws Exception {
        mockMvc.perform(comToken(json(post("/api/eleitores"),
                        eleitorJson("swagger", cpfValido(77), "012345678901", "0045")), tokenChefe()))
                .andExpect(status().isCreated());
    }

    @Test
    void formatoInvalidoDeTituloRetorna400() throws Exception {
        mockMvc.perform(comToken(json(post("/api/eleitores"),
                        eleitorJson("x", cpfValido(13), "123", "0001")), tokenChefe()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eleitorNaoAcessaGerenciamentoDeEleitores() throws Exception {
        criarEleitor(tokenChefe(), "maria");
        String eleitor = login("maria", "senha123");

        mockMvc.perform(comToken(get("/api/eleitores"), eleitor)).andExpect(status().isForbidden());
        mockMvc.perform(comToken(json(post("/api/eleitores"),
                        eleitorJson("y", cpfValido(14), "333333333333", "0001")), eleitor))
                .andExpect(status().isForbidden());
    }
}
