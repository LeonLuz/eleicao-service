package com.example.eleicao_service.feature.usuario.service.impl;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.usuario.model.Perfil;
import com.example.eleicao_service.feature.usuario.model.dto.CriarChefeSecaoRequest;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioResponse;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioUpdateSelfRequest;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.example.eleicao_service.support.Fixtures.usuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl service;

    private final CriarChefeSecaoRequest novoChefe =
            new CriarChefeSecaoRequest("Ana", "52998224725", "ana", "segredo");

    @Test
    void criarChefeSecao_criaUsuarioComPerfilChefe() {
        when(passwordEncoder.encode("segredo")).thenReturn("HASH");
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse resposta = service.criarChefeSecao(novoChefe);

        assertThat(resposta.perfis()).containsExactly(Perfil.CHEFE_SECAO);
        assertThat(resposta.ativo()).isTrue();
        verify(usuarioRepository).save(argThat(u -> "HASH".equals(u.getSenha())));
    }

    @Test
    void criarChefeSecao_impedeUsernameDuplicado() {
        when(usuarioRepository.existsByUsername("ana")).thenReturn(true);

        assertThatThrownBy(() -> service.criarChefeSecao(novoChefe))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Username");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void criarChefeSecao_impedeUsuarioQueJaEhChefe() {
        when(usuarioRepository.findByCpf("52998224725")).thenReturn(Optional.of(usuario(7L, Perfil.CHEFE_SECAO)));

        assertThatThrownBy(() -> service.criarChefeSecao(novoChefe))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("já possui o perfil");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void criarChefeSecao_impedeCandidato() {
        when(usuarioRepository.findByCpf("52998224725")).thenReturn(Optional.of(usuario(7L, Perfil.CANDIDATO)));

        assertThatThrownBy(() -> service.criarChefeSecao(novoChefe))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("candidato");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void criarChefeSecao_adicionaPerfilAoEleitorJaCadastrado() {
        UsuarioModel eleitor = usuario(7L, Perfil.ELEITOR);
        when(usuarioRepository.findByCpf("52998224725")).thenReturn(Optional.of(eleitor));
        when(usuarioRepository.save(eleitor)).thenReturn(eleitor);

        UsuarioResponse resposta = service.criarChefeSecao(novoChefe);

        assertThat(resposta.id()).isEqualTo(7L);
        assertThat(resposta.perfis()).containsExactlyInAnyOrder(Perfil.ELEITOR, Perfil.CHEFE_SECAO);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void atualizarMeusDados_mantendoOMesmoUsernameNaoAcusaDuplicidade() {
        UsuarioModel eu = usuario(1L, Perfil.ELEITOR);
        when(usuarioRepository.findByUsername("user1")).thenReturn(Optional.of(eu));

        service.atualizarMeusDados("user1", new UsuarioUpdateSelfRequest("user1", "nova"));

        verify(usuarioRepository, never()).existsByUsername(anyString());
        verify(passwordEncoder).encode("nova");
    }

    @Test
    void atualizarMeusDados_impedeUsernameDeOutroUsuario() {
        when(usuarioRepository.findByUsername("user1")).thenReturn(Optional.of(usuario(1L, Perfil.ELEITOR)));
        when(usuarioRepository.existsByUsername("ocupado")).thenReturn(true);

        assertThatThrownBy(() -> service.atualizarMeusDados("user1", new UsuarioUpdateSelfRequest("ocupado", null)))
                .isInstanceOf(RegraNegocioException.class);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void atualizarMeusDados_semNovaSenhaMantemASenhaAtual() {
        UsuarioModel eu = usuario(1L, Perfil.ELEITOR);
        when(usuarioRepository.findByUsername("user1")).thenReturn(Optional.of(eu));
        when(usuarioRepository.existsByUsername("novo")).thenReturn(false);

        UsuarioResponse resposta = service.atualizarMeusDados("user1", new UsuarioUpdateSelfRequest("novo", null));

        assertThat(resposta.username()).isEqualTo("novo");
        assertThat(eu.getSenha()).isEqualTo("hash");
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void buscarMeuPerfil_falhaQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarMeuPerfil("fantasma"))
                .isInstanceOf(EntidadeNaoEncontradaException.class);
    }
}
