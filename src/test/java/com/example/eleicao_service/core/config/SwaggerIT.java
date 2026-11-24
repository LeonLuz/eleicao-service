package com.example.eleicao_service.core.config;

import com.example.eleicao_service.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SwaggerIT extends AbstractIntegrationTest {

    @Test
    void documentacaoOpenApiEstaPublicaECobreTodosOsModulos() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths", hasKey("/auth/login")))
                .andExpect(jsonPath("$.paths", hasKey("/api/eleitores")))
                .andExpect(jsonPath("$.paths", hasKey("/api/candidatos")))
                .andExpect(jsonPath("$.paths", hasKey("/api/eleicoes")))
                .andExpect(jsonPath("$.paths", hasKey("/api/partidos")))
                .andExpect(jsonPath("$.paths", hasKey("/api/cargos")))
                .andExpect(jsonPath("$.paths", hasKey("/api/votacao/votar")))
                .andExpect(jsonPath("$.paths['/auth/login'].post.tags[0]").value("Autenticação"));
    }

    @Test
    void swaggerUiEstaPublico() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
