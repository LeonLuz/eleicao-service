package com.example.eleicao_service.support;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.MountableFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base dos testes de integração: sobe a aplicação inteira (controllers, segurança JWT, services, JPA)
 * contra um MySQL 8.4 real em container, criado com o mesmo schema usado pelo docker-compose.
 *
 * O container é iniciado uma única vez e compartilhado por todas as classes de teste (singleton),
 * e as tabelas são limpas antes de cada teste para que um teste não interfira no outro.
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    protected static final String ADMIN_USERNAME = "admin";
    protected static final String ADMIN_SENHA = "admin123";

    /** Ids dos cargos inseridos pelo 01-schema.sql. */
    protected static final long CARGO_SENADOR = 3L;
    protected static final long CARGO_GOVERNADOR = 4L;

    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.4")
            .withDatabaseName("eleicao")
            .withCopyFileToContainer(
                    MountableFile.forHostPath("docker/mysql/init/01-schema.sql"),
                    "/docker-entrypoint-initdb.d/01-schema.sql");

    static {
        MYSQL.start();
    }

    @DynamicPropertySource
    static void propriedades(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.jpa.show-sql", () -> "false");
        registry.add("jwt.secret", () -> "chave-jwt-dos-testes-de-integracao-com-32+-bytes");
        registry.add("jwt.expiration", () -> "3600000");
        registry.add("app.admin.username", () -> ADMIN_USERNAME);
        registry.add("app.admin.password", () -> ADMIN_SENHA);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JdbcTemplate jdbc;

    private int sequencia;

    @BeforeEach
    void limparBanco() {
        jdbc.update("DELETE FROM voto");
        jdbc.update("DELETE FROM controle_voto");
        jdbc.update("DELETE FROM candidato");
        jdbc.update("DELETE FROM eleitor");
        jdbc.update("DELETE FROM eleicao");
        jdbc.update("DELETE FROM partido");
        jdbc.update("DELETE FROM usuario WHERE username <> ?", ADMIN_USERNAME);
        sequencia = 0;
    }

    // ---------- HTTP ----------

    protected static MockHttpServletRequestBuilder comToken(MockHttpServletRequestBuilder req, String token) {
        return req.header("Authorization", "Bearer " + token);
    }

    protected static MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder req, String body) {
        return req.contentType(MediaType.APPLICATION_JSON).content(body);
    }

    protected static <T> T ler(ResultActions resultado, String jsonPath) throws Exception {
        return JsonPath.read(resultado.andReturn().getResponse().getContentAsString(), jsonPath);
    }

    protected static long lerId(ResultActions resultado, String jsonPath) throws Exception {
        return ((Number) ler(resultado, jsonPath)).longValue();
    }

    // ---------- Autenticação ----------

    protected String login(String username, String senha) throws Exception {
        ResultActions r = mockMvc.perform(json(post("/auth/login"),
                        """
                        {"username": "%s", "senha": "%s"}
                        """.formatted(username, senha)))
                .andExpect(status().isOk());
        return ler(r, "$.token");
    }

    protected String tokenChefe() throws Exception {
        return login(ADMIN_USERNAME, ADMIN_SENHA);
    }

    // ---------- Cadastros (sempre feitos pelo Chefe da Seção via API) ----------

    protected long criarEleicao(String tokenChefe, String data, int turno) throws Exception {
        return lerId(mockMvc.perform(comToken(json(post("/api/eleicoes"),
                        """
                        {"descricao": "Eleição %s", "dataEleicao": "%s", "turno": %d,
                         "horarioAbertura": "08:00:00", "horarioEncerramento": "17:00:00"}
                        """.formatted(data, data, turno)), tokenChefe))
                .andExpect(status().isCreated()), "$.id");
    }

    protected long criarPartido(String tokenChefe, String sigla, int numero) throws Exception {
        return lerId(mockMvc.perform(comToken(json(post("/api/partidos"),
                        """
                        {"nome": "Partido %s", "sigla": "%s", "numero": %d}
                        """.formatted(sigla, sigla, numero)), tokenChefe))
                .andExpect(status().isCreated()), "$.id");
    }

    /** Cadastra um eleitor com a senha "senha123" e devolve o id. */
    protected long criarEleitor(String tokenChefe, String username) throws Exception {
        int n = ++sequencia;
        return lerId(mockMvc.perform(comToken(json(post("/api/eleitores"),
                        """
                        {"nome": "Eleitor %s", "cpf": "%s", "username": "%s", "senha": "senha123",
                         "tituloEleitor": "%012d", "zona": "001", "secao": "0001"}
                        """.formatted(username, cpfValido(n), username, n)), tokenChefe))
                .andExpect(status().isCreated()), "$.idUsuario");
    }

    /** Cadastra um candidato com a senha "senha123" e devolve o id. */
    protected long criarCandidato(String tokenChefe, String username, int numero, long idPartido,
                                  long idCargo, long idEleicao) throws Exception {
        return lerId(mockMvc.perform(comToken(json(post("/api/candidatos"),
                        candidatoJson(username, cpfValido(++sequencia), numero, idPartido, idCargo, idEleicao)),
                        tokenChefe))
                .andExpect(status().isCreated()), "$.idUsuario");
    }

    protected static String candidatoJson(String username, String cpf, int numero, long idPartido,
                                          long idCargo, long idEleicao) {
        return """
               {"nome": "Candidato %s", "cpf": "%s", "username": "%s", "senha": "senha123",
                "numeroEleitoral": %d, "idPartido": %d, "idCargo": %d, "idEleicao": %d}
               """.formatted(username, cpf, username, numero, idPartido, idCargo, idEleicao);
    }

    /** Gera um CPF com dígitos verificadores válidos (passa no @CPF do Hibernate Validator). */
    protected static String cpfValido(int semente) {
        String base = String.format("%09d", 100_000_000 + semente);
        int d1 = digitoVerificador(base, 10);
        int d2 = digitoVerificador(base + d1, 11);
        return base + d1 + d2;
    }

    private static int digitoVerificador(String digitos, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < digitos.length(); i++) {
            soma += (digitos.charAt(i) - '0') * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
