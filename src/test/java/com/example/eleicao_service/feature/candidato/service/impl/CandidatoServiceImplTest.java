package com.example.eleicao_service.feature.candidato.service.impl;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoRequest;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoResponse;
import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import com.example.eleicao_service.feature.candidato.repository.CandidatoRepository;
import com.example.eleicao_service.feature.cargo.model.CargoModel;
import com.example.eleicao_service.feature.cargo.repository.CargoRepository;
import com.example.eleicao_service.feature.eleicao.model.StatusEleicao;
import com.example.eleicao_service.feature.eleicao.model.entity.EleicaoModel;
import com.example.eleicao_service.feature.eleicao.repository.EleicaoRepository;
import com.example.eleicao_service.feature.partido.model.entity.PartidoModel;
import com.example.eleicao_service.feature.partido.repository.PartidoRepository;
import com.example.eleicao_service.feature.usuario.model.Perfil;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.example.eleicao_service.support.Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidatoServiceImplTest {

    @Mock private CandidatoRepository candidatoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PartidoRepository partidoRepository;
    @Mock private CargoRepository cargoRepository;
    @Mock private EleicaoRepository eleicaoRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CandidatoServiceImpl service;

    private PartidoModel partido;
    private CargoModel cargo;
    private EleicaoModel eleicao;

    @BeforeEach
    void setUp() {
        partido = partido(1L, "PA", 10);
        cargo = cargo(4L, "Governador");
        eleicao = eleicao(100L, StatusEleicao.AGENDADA);
    }

    private CandidatoRequest request(String senha) {
        return new CandidatoRequest("Maria", "529.982.247-25", "maria", senha, 10, 1L, 4L, 100L);
    }

    private void referenciasExistem() {
        when(partidoRepository.findById(1L)).thenReturn(Optional.of(partido));
        when(cargoRepository.findById(4L)).thenReturn(Optional.of(cargo));
        when(eleicaoRepository.findById(100L)).thenReturn(Optional.of(eleicao));
    }

    @Test
    void cadastrar_criaUsuarioComPerfilCandidatoESenhaCriptografada() {
        referenciasExistem();
        when(passwordEncoder.encode("segredo")).thenReturn("HASH");
        when(usuarioRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(candidatoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CandidatoResponse resposta = service.cadastrar(request("segredo"));

        ArgumentCaptor<CandidatoModel> captor = ArgumentCaptor.forClass(CandidatoModel.class);
        verify(candidatoRepository).save(captor.capture());
        CandidatoModel salvo = captor.getValue();
        assertThat(salvo.getUsuario().getPerfis()).containsExactly(Perfil.CANDIDATO);
        assertThat(salvo.getUsuario().getSenha()).isEqualTo("HASH");
        assertThat(salvo.getUsuario().getCpf()).isEqualTo("52998224725");
        assertThat(salvo.getUsuario().getAtivo()).isTrue();
        assertThat(salvo.getCargo()).isSameAs(cargo);
        assertThat(salvo.getEleicao()).isSameAs(eleicao);

        assertThat(resposta.partidoSigla()).isEqualTo("PA");
        assertThat(resposta.cargoNome()).isEqualTo("Governador");
        assertThat(resposta.numeroEleitoral()).isEqualTo(10);
    }

    @Test
    void cadastrar_impedeUsernameDuplicado() {
        referenciasExistem();
        when(usuarioRepository.existsByUsername("maria")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(request("segredo")))
                .isInstanceOf(RegraNegocioException.class);
        verify(candidatoRepository, never()).save(any());
    }

    @Test
    void cadastrar_impedeNumeroRepetidoNoMesmoCargoEEleicao() {
        when(candidatoRepository.existsByNumeroEleitoralAndCargoIdAndEleicaoId(10, 4L, 100L)).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(request("segredo")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("número");
        verify(candidatoRepository, never()).save(any());
    }

    @Test
    void cadastrar_exigeSenha() {
        referenciasExistem();
        assertThatThrownBy(() -> service.cadastrar(request("  ")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Senha");
        verify(candidatoRepository, never()).save(any());
    }

    @Test
    void cadastrar_falhaQuandoPartidoNaoExiste() {
        when(partidoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cadastrar(request("segredo")))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessageContaining("Partido");
    }

    @Test
    void cadastrar_falhaQuandoEleicaoNaoExiste() {
        when(partidoRepository.findById(1L)).thenReturn(Optional.of(partido));
        when(cargoRepository.findById(4L)).thenReturn(Optional.of(cargo));
        when(eleicaoRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cadastrar(request("segredo")))
                .isInstanceOf(EntidadeNaoEncontradaException.class)
                .hasMessageContaining("Eleição");
    }

    @Test
    void atualizar_mantemSenhaQuandoNaoInformada() {
        CandidatoModel existente = candidato(usuario(50L, Perfil.CANDIDATO), 99, partido, cargo, eleicao);
        when(candidatoRepository.findById(50L)).thenReturn(Optional.of(existente));
        referenciasExistem();
        when(candidatoRepository.save(existente)).thenReturn(existente);

        CandidatoResponse resposta = service.atualizar(50L, request(null));

        assertThat(existente.getUsuario().getSenha()).isEqualTo("hash");
        assertThat(existente.getUsuario().getNome()).isEqualTo("Maria");
        assertThat(resposta.numeroEleitoral()).isEqualTo(10);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void cadastrar_detectaCandidatoJaCadastradoMesmoComCpfComPontuacao() {
        referenciasExistem();
        when(usuarioRepository.findByCpf("52998224725")).thenReturn(Optional.of(usuario(50L, Perfil.CANDIDATO)));

        assertThatThrownBy(() -> service.cadastrar(request("segredo")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("já está cadastrado como candidato");
        verify(candidatoRepository, never()).save(any());
    }

    @Test
    void cadastrar_impedeChefeDeSecaoComoCandidato() {
        referenciasExistem();
        when(usuarioRepository.findByCpf("52998224725")).thenReturn(Optional.of(usuario(7L, Perfil.CHEFE_SECAO)));

        assertThatThrownBy(() -> service.cadastrar(request("segredo")))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("chefe de seção");
        verify(candidatoRepository, never()).save(any());
    }

    @Test
    void cadastrar_adicionaPerfilCandidatoAoEleitorJaCadastrado() {
        referenciasExistem();
        UsuarioModel eleitor = usuario(1L, Perfil.ELEITOR);
        when(usuarioRepository.findByCpf("52998224725")).thenReturn(Optional.of(eleitor));
        when(usuarioRepository.save(eleitor)).thenReturn(eleitor);
        when(candidatoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.cadastrar(request(null));

        ArgumentCaptor<CandidatoModel> captor = ArgumentCaptor.forClass(CandidatoModel.class);
        verify(candidatoRepository).save(captor.capture());
        assertThat(captor.getValue().getUsuario()).isSameAs(eleitor);
        assertThat(eleitor.getPerfis()).containsExactlyInAnyOrder(Perfil.ELEITOR, Perfil.CANDIDATO);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void atualizar_impedeCpfDeOutroUsuario() {
        CandidatoModel existente = candidato(usuario(50L, Perfil.CANDIDATO), 10, partido, cargo, eleicao);
        when(candidatoRepository.findById(50L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByCpf("52998224725")).thenReturn(true);

        assertThatThrownBy(() -> service.atualizar(50L, request(null)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("CPF");
        verify(candidatoRepository, never()).save(any());
    }

    @Test
    void atualizar_impedeNumeroJaUsadoNoCargoEEleicao() {
        CandidatoModel existente = candidato(usuario(50L, Perfil.CANDIDATO), 99, partido, cargo, eleicao);
        when(candidatoRepository.findById(50L)).thenReturn(Optional.of(existente));
        when(candidatoRepository.existsByNumeroEleitoralAndCargoIdAndEleicaoId(10, 4L, 100L)).thenReturn(true);

        assertThatThrownBy(() -> service.atualizar(50L, request(null)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("número");
        verify(candidatoRepository, never()).save(any());
    }

    @Test
    void atualizar_semMudarCpfUsernameENumeroNaoAcusaDuplicidade() {
        UsuarioModel u = usuario(50L, Perfil.CANDIDATO);
        u.setCpf("52998224725");
        u.setUsername("maria");
        CandidatoModel existente = candidato(u, 10, partido, cargo, eleicao);
        when(candidatoRepository.findById(50L)).thenReturn(Optional.of(existente));
        referenciasExistem();
        when(candidatoRepository.save(existente)).thenReturn(existente);

        service.atualizar(50L, request(null));

        verify(usuarioRepository, never()).existsByCpf(any());
        verify(usuarioRepository, never()).existsByUsername(any());
        verify(candidatoRepository, never()).existsByNumeroEleitoralAndCargoIdAndEleicaoId(any(), any(), any());
    }

    @Test
    void atualizar_falhaQuandoCandidatoNaoExiste() {
        when(candidatoRepository.findById(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(50L, request("x")))
                .isInstanceOf(EntidadeNaoEncontradaException.class);
    }

    @Test
    void listarPorEleicaoECargo_retornaCandidatosDaConsulta() {
        CandidatoModel c = candidato(usuario(50L, Perfil.CANDIDATO), 10, partido, cargo, eleicao);
        when(candidatoRepository.findByEleicaoIdAndCargoId(100L, 4L)).thenReturn(List.of(c));

        assertThat(service.listarPorEleicaoECargo(100L, 4L))
                .singleElement()
                .satisfies(r -> {
                    assertThat(r.idUsuario()).isEqualTo(50L);
                    assertThat(r.idEleicao()).isEqualTo(100L);
                });
    }

    @Test
    void excluir_removeCandidatoExistente() {
        CandidatoModel c = candidato(usuario(50L, Perfil.CANDIDATO), 10, partido, cargo, eleicao);
        when(candidatoRepository.findById(50L)).thenReturn(Optional.of(c));

        service.excluir(50L);

        verify(candidatoRepository).delete(c);
        verify(usuarioRepository).delete(c.getUsuario());
    }

    @Test
    void excluir_mantemUsuarioQueTambemEhEleitor() {
        UsuarioModel usuario = usuario(50L, Perfil.CANDIDATO, Perfil.ELEITOR);
        CandidatoModel c = candidato(usuario, 10, partido, cargo, eleicao);
        when(candidatoRepository.findById(50L)).thenReturn(Optional.of(c));

        service.excluir(50L);

        verify(candidatoRepository).delete(c);
        verify(usuarioRepository).save(usuario);
        verify(usuarioRepository, never()).delete(any());
        assertThat(usuario.getPerfis()).containsExactly(Perfil.ELEITOR);
    }

    @Test
    void excluir_falhaQuandoCandidatoNaoExiste() {
        when(candidatoRepository.findById(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(50L)).isInstanceOf(EntidadeNaoEncontradaException.class);
        verify(candidatoRepository, never()).delete(any());
    }
}
