package com.example.eleicao_service.feature.votacao;

import com.example.eleicao_service.support.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Fluxo completo da eleição pela API: cadastro, abertura, votação, acompanhamento,
 * encerramento e resultado final — incluindo as regras de negócio do minimundo.
 */
class VotacaoIT extends AbstractIntegrationTest {

    private String chefe;
    private long eleicao;
    private long gov1;
    private long gov2;
    private long sen1;

    @BeforeEach
    void cenario() throws Exception {
        chefe = tokenChefe();
        eleicao = criarEleicao(chefe, "2026-10-04", 1);
        long pa = criarPartido(chefe, "PA", 10);
        long pb = criarPartido(chefe, "PB", 20);
        gov1 = criarCandidato(chefe, "gov1", 10, pa, CARGO_GOVERNADOR, eleicao);
        gov2 = criarCandidato(chefe, "gov2", 20, pb, CARGO_GOVERNADOR, eleicao);
        sen1 = criarCandidato(chefe, "sen1", 100, pa, CARGO_SENADOR, eleicao);
        criarEleitor(chefe, "eleitor1");
        criarEleitor(chefe, "eleitor2");
        criarEleitor(chefe, "eleitor3");
    }

    private ResultActions votar(String token, long cargo, long candidato) throws Exception {
        return mockMvc.perform(comToken(json(post("/api/votacao/votar"), """
                {"idEleicao": %d, "idCargo": %d, "idCandidato": %d}
                """.formatted(eleicao, cargo, candidato)), token));
    }

    private void abrir() throws Exception {
        mockMvc.perform(comToken(patch("/api/eleicoes/{id}/abrir", eleicao), chefe)).andExpect(status().isOk());
    }

    private void encerrar() throws Exception {
        mockMvc.perform(comToken(patch("/api/eleicoes/{id}/encerrar", eleicao), chefe)).andExpect(status().isOk());
    }

    @Test
    void eleitorVotaUmaVezPorCargo() throws Exception {
        abrir();
        String eleitor = login("eleitor1", "senha123");

        votar(eleitor, CARGO_GOVERNADOR, gov1)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cargo").value("Governador"));
        votar(eleitor, CARGO_SENADOR, sen1).andExpect(status().isCreated());

        // segundo voto no mesmo cargo (mesmo em outro candidato) é bloqueado
        votar(eleitor, CARGO_GOVERNADOR, gov2).andExpect(status().isConflict());

        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM voto", Integer.class)).isEqualTo(2);
    }

    @Test
    void naoPermiteVotarAntesDaAberturaNemDepoisDoEncerramento() throws Exception {
        String eleitor = login("eleitor1", "senha123");

        votar(eleitor, CARGO_GOVERNADOR, gov1).andExpect(status().isConflict());

        abrir();
        encerrar();
        votar(eleitor, CARGO_GOVERNADOR, gov1).andExpect(status().isConflict());

        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM voto", Integer.class)).isZero();
    }

    @Test
    void apenasEleitorPodeVotar() throws Exception {
        abrir();

        votar(chefe, CARGO_GOVERNADOR, gov1).andExpect(status().isForbidden());
        votar(login("gov1", "senha123"), CARGO_GOVERNADOR, gov1).andExpect(status().isForbidden());
    }

    @Test
    void naoPermiteVotarEmCandidatoDeOutroCargo() throws Exception {
        abrir();

        votar(login("eleitor1", "senha123"), CARGO_SENADOR, gov1).andExpect(status().isConflict());
    }

    @Test
    void sigiloDoVotoNaoHaComoLigarEleitorAoCandidato() throws Exception {
        abrir();
        votar(login("eleitor1", "senha123"), CARGO_GOVERNADOR, gov1).andExpect(status().isCreated());

        // o controle registra QUE o eleitor votou para o cargo...
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM controle_voto", Integer.class)).isEqualTo(1);
        assertThat(jdbc.queryForList(
                "SELECT column_name FROM information_schema.columns WHERE table_schema = DATABASE() "
                        + "AND table_name = 'controle_voto'", String.class))
                .map(String::toLowerCase)
                .doesNotContain("id_candidato");

        // ...e o voto registra EM QUEM se votou, sem nenhuma coluna que identifique o eleitor
        List<String> colunasVoto = jdbc.queryForList(
                "SELECT column_name FROM information_schema.columns WHERE table_schema = DATABASE() "
                        + "AND table_name = 'voto'", String.class);
        assertThat(colunasVoto).map(String::toLowerCase)
                .contains("id_candidato")
                .noneMatch(c -> c.contains("eleitor") || c.contains("usuario"));
    }

    @Test
    void acompanhamentoEmTempoRealRespeitaOPerfil() throws Exception {
        abrir();
        votar(login("eleitor1", "senha123"), CARGO_GOVERNADOR, gov2).andExpect(status().isCreated());
        votar(login("eleitor2", "senha123"), CARGO_GOVERNADOR, gov2).andExpect(status().isCreated());
        votar(login("eleitor3", "senha123"), CARGO_GOVERNADOR, gov1).andExpect(status().isCreated());

        // eleitor acompanha todos os cargos, com os candidatos ordenados por votos
        ResultActions apuracao = mockMvc.perform(comToken(
                        get("/api/votacao/eleicao/{id}/acompanhamento", eleicao), login("eleitor1", "senha123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nomeCargo").value(containsInAnyOrder("Governador", "Senador")));
        List<Map<String, Object>> candidatosGov = lista(cargo(apuracao, "Governador"), "candidatos");
        assertThat(candidatosGov).extracting(c -> numero(c, "idCandidato")).containsExactly(gov2, gov1);
        assertThat(candidatosGov).extracting(c -> numero(c, "totalVotos")).containsExactly(2L, 1L);

        // candidato a Governador só vê a disputa de Governador
        mockMvc.perform(comToken(get("/api/votacao/eleicao/{id}/acompanhamento", eleicao), login("gov1", "senha123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nomeCargo").value("Governador"));

        // candidato a Senador só vê a disputa de Senador
        mockMvc.perform(comToken(get("/api/votacao/eleicao/{id}/acompanhamento", eleicao), login("sen1", "senha123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nomeCargo").value("Senador"));
    }

    @Test
    void acompanhamentoDeEleicaoAgendadaRetorna409() throws Exception {
        mockMvc.perform(comToken(get("/api/votacao/eleicao/{id}/acompanhamento", eleicao), login("eleitor1", "senha123")))
                .andExpect(status().isConflict());
    }

    @Test
    void resultadoFinalSoAposEncerramentoComOMaisVotadoPorCargo() throws Exception {
        abrir();
        votar(login("eleitor1", "senha123"), CARGO_GOVERNADOR, gov1).andExpect(status().isCreated());
        votar(login("eleitor2", "senha123"), CARGO_GOVERNADOR, gov1).andExpect(status().isCreated());
        votar(login("eleitor3", "senha123"), CARGO_GOVERNADOR, gov2).andExpect(status().isCreated());
        votar(login("eleitor1", "senha123"), CARGO_SENADOR, sen1).andExpect(status().isCreated());

        mockMvc.perform(comToken(get("/api/votacao/eleicao/{id}/resultado-final", eleicao), chefe))
                .andExpect(status().isConflict());

        encerrar();

        ResultActions resultado = mockMvc.perform(comToken(
                        get("/api/votacao/eleicao/{id}/resultado-final", eleicao), chefe))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        Map<String, Object> governador = cargo(resultado, "Governador");
        assertThat(numero(mapa(governador, "maisVotado"), "idCandidato")).isEqualTo(gov1);
        assertThat(numero(mapa(governador, "maisVotado"), "totalVotos")).isEqualTo(2L);
        assertThat(lista(governador, "candidatos")).hasSize(2);

        Map<String, Object> senador = cargo(resultado, "Senador");
        assertThat(numero(mapa(senador, "maisVotado"), "idCandidato")).isEqualTo(sen1);
        assertThat(numero(mapa(senador, "maisVotado"), "totalVotos")).isEqualTo(1L);
    }

    // ---------- leitura da resposta JSON ----------

    private static Map<String, Object> cargo(ResultActions resposta, String nomeCargo) throws Exception {
        List<Map<String, Object>> cargos = ler(resposta, "$");
        return cargos.stream().filter(c -> nomeCargo.equals(c.get("nomeCargo"))).findFirst()
                .orElseThrow(() -> new AssertionError("Cargo ausente na resposta: " + nomeCargo));
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> lista(Map<String, Object> json, String campo) {
        return (List<Map<String, Object>>) json.get(campo);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> mapa(Map<String, Object> json, String campo) {
        return (Map<String, Object>) json.get(campo);
    }

    private static long numero(Map<String, Object> json, String campo) {
        return ((Number) json.get(campo)).longValue();
    }
}
