package com.example.eleicao_service.feature.eleitor.service.impl;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.eleitor.model.dto.EleitorRequest;
import com.example.eleicao_service.feature.eleitor.model.dto.EleitorResponse;
import com.example.eleicao_service.feature.eleitor.model.entity.EleitorModel;
import com.example.eleicao_service.feature.eleitor.repository.EleitorRepository;
import com.example.eleicao_service.feature.usuario.model.Perfil;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.example.eleicao_service.support.Fixtures.eleitor;
import static com.example.eleicao_service.support.Fixtures.usuario;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EleitorServiceImplTest {

    @Mock private EleitorRepository eleitorRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EleitorServiceImpl service;

    private EleitorRequest request(String senha) {
        return new EleitorRequest("João", "52998224725", "joao", senha, "123456789012", "001", "0002");
    }

    @Test
    void cadastrar_criaUsuarioComPerfilEleitorESenhaCriptografada() {
        when(passwordEncoder.encode("segredo")).thenReturn("HASH");
        when(eleitorRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EleitorResponse resposta = service.cadastrar(request("segredo"));

        ArgumentCaptor<EleitorModel> captor = ArgumentCaptor.forClass(EleitorModel.class);
        verify(eleitorRepository).save(captor.capture());
        assertThat(captor.getValue().getUsuario().getPerfil()).isEqualTo(Perfil.ELEITOR);
        assertThat(captor.getValue().getUsuario().getSenha()).isEqualTo("HASH");
        assertThat(resposta.tituloEleitor()).isEqualTo("123456789012");
        assertThat(resposta.username()).isEqualTo("joao");
    }

    @Test
    void cadastrar_impedeCpfDuplicado() {
        when(usuarioRepository.existsByCpf("52998224725")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(request("segredo"))).isInstanceOf(RegraNegocioException.class);
        verify(eleitorRepository, never()).save(any());
    }

    @Test
    void cadastrar_impedeTituloDuplicado() {
        when(eleitorRepository.existsByTituloEleitor("123456789012")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(request("segredo")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Título");
        verify(eleitorRepository, never()).save(any());
    }

    @Test
    void cadastrar_exigeSenha() {
        assertThatThrownBy(() -> service.cadastrar(request(null)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Senha");
    }

    @Test
    void atualizar_alteraDadosEMantemSenhaQuandoEmBranco() {
        EleitorModel existente = eleitor(usuario(1L, Perfil.ELEITOR));
        when(eleitorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(eleitorRepository.save(existente)).thenReturn(existente);

        EleitorResponse resposta = service.atualizar(1L, request(""));

        assertThat(resposta.nome()).isEqualTo("João");
        assertThat(resposta.secao()).isEqualTo("0002");
        assertThat(existente.getUsuario().getSenha()).isEqualTo("hash");
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void atualizar_trocaSenhaQuandoInformada() {
        EleitorModel existente = eleitor(usuario(1L, Perfil.ELEITOR));
        when(eleitorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(eleitorRepository.save(existente)).thenReturn(existente);
        when(passwordEncoder.encode("nova")).thenReturn("HASH-NOVA");

        service.atualizar(1L, request("nova"));

        assertThat(existente.getUsuario().getSenha()).isEqualTo("HASH-NOVA");
    }

    @Test
    void atualizar_impedeTituloDeOutroEleitor() {
        EleitorModel existente = eleitor(usuario(1L, Perfil.ELEITOR));
        when(eleitorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(eleitorRepository.existsByTituloEleitor("123456789012")).thenReturn(true);

        assertThatThrownBy(() -> service.atualizar(1L, request(null)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Título");
        verify(eleitorRepository, never()).save(any());
    }

    @Test
    void atualizar_impedeUsernameDeOutroUsuario() {
        EleitorModel existente = eleitor(usuario(1L, Perfil.ELEITOR));
        when(eleitorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByUsername("joao")).thenReturn(true);

        assertThatThrownBy(() -> service.atualizar(1L, request(null)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Username");
        verify(eleitorRepository, never()).save(any());
    }

    @Test
    void consultar_falhaQuandoEleitorNaoExiste() {
        when(eleitorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultarPorId(1L)).isInstanceOf(EntidadeNaoEncontradaException.class);
    }

    @Test
    void excluir_removeEleitorExistente() {
        EleitorModel existente = eleitor(usuario(1L, Perfil.ELEITOR));
        when(eleitorRepository.findById(1L)).thenReturn(Optional.of(existente));

        service.excluir(1L);

        verify(eleitorRepository).delete(existente);
    }
}
