package com.example.eleicao_service.feature.auth;

import com.example.eleicao_service.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIT extends AbstractIntegrationTest {

    @Test
    void loginComCredenciaisValidasRetornaTokenEPerfil() throws Exception {
        mockMvc.perform(json(post("/auth/login"), """
                        {"username": "admin", "senha": "admin123"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.perfis").value(contains("CHEFE_SECAO")));
    }

    @Test
    void loginComSenhaErradaRetorna401() throws Exception {
        mockMvc.perform(json(post("/auth/login"), """
                        {"username": "admin", "senha": "errada"}"""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginDeUsuarioInexistenteRetorna401() throws Exception {
        mockMvc.perform(json(post("/auth/login"), """
                        {"username": "ninguem", "senha": "qualquer"}"""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void endpointProtegidoSemTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/eleicoes")).andExpect(status().isUnauthorized());
    }

    @Test
    void endpointProtegidoComTokenInvalidoRetorna401() throws Exception {
        mockMvc.perform(comToken(get("/api/eleicoes"), "token.invalido.qualquer"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioAutenticadoConsultaOProprioPerfil() throws Exception {
        mockMvc.perform(comToken(get("/api/usuarios/me"), tokenChefe()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(ADMIN_USERNAME))
                .andExpect(jsonPath("$.perfis").value(contains("CHEFE_SECAO")));
    }

    @Test
    void usuarioTrocaASenhaEPassaAEntrarComANovaSenha() throws Exception {
        criarEleitor(tokenChefe(), "maria");
        String token = login("maria", "senha123");

        mockMvc.perform(comToken(json(put("/api/usuarios/me"), """
                        {"username": "maria", "novaSenha": "nova-senha"}"""), token))
                .andExpect(status().isOk());

        login("maria", "nova-senha");
        mockMvc.perform(json(post("/auth/login"), """
                        {"username": "maria", "senha": "senha123"}"""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void apenasChefeDeSecaoCadastraOutroChefe() throws Exception {
        String novoChefe = """
                {"nome": "Outro Chefe", "cpf": "%s", "username": "chefe2", "senha": "segredo"}
                """.formatted(cpfValido(900));

        criarEleitor(tokenChefe(), "maria");
        mockMvc.perform(comToken(json(post("/api/usuarios/chefe-secao"), novoChefe), login("maria", "senha123")))
                .andExpect(status().isForbidden());

        mockMvc.perform(comToken(json(post("/api/usuarios/chefe-secao"), novoChefe), tokenChefe()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.perfis").value(contains("CHEFE_SECAO")));
        login("chefe2", "segredo");
    }

    @Test
    void tokenAntigoAposTrocarOUsernameRetorna401() throws Exception {
        criarEleitor(tokenChefe(), "maria");
        String token = login("maria", "senha123");

        mockMvc.perform(comToken(json(put("/api/usuarios/me"), """
                        {"username": "maria2"}"""), token))
                .andExpect(status().isOk());

        mockMvc.perform(comToken(get("/api/usuarios/me"), token))
                .andExpect(status().isUnauthorized());
        login("maria2", "senha123");
    }
}
