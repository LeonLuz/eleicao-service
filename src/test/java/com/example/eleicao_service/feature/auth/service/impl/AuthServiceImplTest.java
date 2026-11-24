package com.example.eleicao_service.feature.auth.service.impl;

import com.example.eleicao_service.core.security.jwt.service.JwtService;
import com.example.eleicao_service.feature.auth.dto.LoginRequest;
import com.example.eleicao_service.feature.auth.dto.LoginResponse;
import com.example.eleicao_service.feature.usuario.model.Perfil;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static com.example.eleicao_service.support.Fixtures.usuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl service;

    @Test
    void login_retornaTokenComPerfilENomeDoUsuario() {
        UsuarioModel usuario = usuario(1L, Perfil.ELEITOR);
        UserDetails principal = User.withUsername("user1").password("hash").roles("ELEITOR").build();
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
        when(usuarioRepository.findByUsername("user1")).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(principal)).thenReturn("jwt-token");

        LoginResponse resposta = service.login(new LoginRequest("user1", "senha"));

        assertThat(resposta.token()).isEqualTo("jwt-token");
        assertThat(resposta.tipo()).isEqualTo("Bearer");
        assertThat(resposta.perfil()).isEqualTo("ELEITOR");
        assertThat(resposta.nome()).isEqualTo(usuario.getNome());
    }

    @Test
    void login_comCredenciaisInvalidasNaoGeraToken() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("credenciais"));

        assertThatThrownBy(() -> service.login(new LoginRequest("user1", "errada")))
                .isInstanceOf(BadCredentialsException.class);
        verifyNoInteractions(jwtService);
    }
}
