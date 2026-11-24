package com.example.eleicao_service.core.security.jwt.service.impl;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceImplTest {

    private static final String SECRET = "chave-de-teste-com-pelo-menos-32-bytes-para-hs256";

    private JwtServiceImpl jwtService;
    private final UserDetails maria = User.withUsername("maria").password("x").roles("ELEITOR").build();
    private final UserDetails joao = User.withUsername("joao").password("x").roles("ELEITOR").build();

    @BeforeEach
    void setUp() {
        jwtService = novoServico(SECRET, 60_000);
    }

    private static JwtServiceImpl novoServico(String secret, long expiracaoMs) {
        JwtServiceImpl service = new JwtServiceImpl();
        ReflectionTestUtils.setField(service, "secretKey", secret);
        ReflectionTestUtils.setField(service, "jwtExpiration", expiracaoMs);
        return service;
    }

    @Test
    void tokenGeradoCarregaOUsernameEEhValidoParaOMesmoUsuario() {
        String token = jwtService.generateToken(maria);

        assertThat(jwtService.extractUsername(token)).isEqualTo("maria");
        assertThat(jwtService.isTokenValid(token, maria)).isTrue();
    }

    @Test
    void tokenNaoEhValidoParaOutroUsuario() {
        String token = jwtService.generateToken(maria);

        assertThat(jwtService.isTokenValid(token, joao)).isFalse();
    }

    @Test
    void tokenExpiradoNaoEhValido() {
        JwtServiceImpl expiraNaHora = novoServico(SECRET, -1_000);
        String token = expiraNaHora.generateToken(maria);

        assertThat(expiraNaHora.isTokenValid(token, maria)).isFalse();
    }

    @Test
    void tokenAssinadoComOutraChaveNaoEhValido() {
        String tokenForjado = novoServico("outra-chave-qualquer-com-mais-de-32-bytes!!", 60_000).generateToken(maria);

        assertThat(jwtService.isTokenValid(tokenForjado, maria)).isFalse();
        assertThatThrownBy(() -> jwtService.extractUsername(tokenForjado)).isInstanceOf(JwtException.class);
    }

    @Test
    void tokenMalformadoNaoEhValido() {
        assertThat(jwtService.isTokenValid("nao.eh.jwt", maria)).isFalse();
    }
}
