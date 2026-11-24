package com.example.eleicao_service.feature.votacao.service.impl;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import com.example.eleicao_service.feature.candidato.repository.CandidatoRepository;
import com.example.eleicao_service.feature.cargo.model.CargoModel;
import com.example.eleicao_service.feature.eleicao.model.StatusEleicao;
import com.example.eleicao_service.feature.eleicao.model.entity.EleicaoModel;
import com.example.eleicao_service.feature.eleicao.repository.EleicaoRepository;
import com.example.eleicao_service.feature.eleitor.model.entity.EleitorModel;
import com.example.eleicao_service.feature.eleitor.repository.EleitorRepository;
import com.example.eleicao_service.feature.partido.model.entity.PartidoModel;
import com.example.eleicao_service.feature.usuario.model.Perfil;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import com.example.eleicao_service.feature.votacao.model.dto.ApuracaoCargoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.ResultadoFinalCargoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.VotoConfirmacaoResponse;
import com.example.eleicao_service.feature.votacao.model.dto.VotoRequest;
import com.example.eleicao_service.feature.votacao.model.entity.ControleVotoModel;
import com.example.eleicao_service.feature.votacao.model.entity.VotoModel;
import com.example.eleicao_service.feature.votacao.repository.ControleVotoRepository;
import com.example.eleicao_service.feature.votacao.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.eleicao_service.support.Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotacaoServiceImplTest {

    @Mock private EleicaoRepository eleicaoRepository;
    @Mock private EleitorRepository eleitorRepository;
    @Mock private CandidatoRepository candidatoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ControleVotoRepository controleVotoRepository;
    @Mock private VotoRepository votoRepository;

    @InjectMocks
    private VotacaoServiceImpl service;

    private CargoModel governador;
    private CargoModel senador;
    private PartidoModel partidoA;
    private PartidoModel partidoB;

    @BeforeEach
    void setUp() {
        governador = cargo(4L, "Governador");
        senador = cargo(3L, "Senador");
        partidoA = partido(1L, "PA", 10);
        partidoB = partido(2L, "PB", 20);
    }

    @Nested
    class RegistrarVoto {

        private UsuarioModel usuarioEleitor;
        private EleitorModel eleitor;
        private EleicaoModel eleicaoAberta;
        private CandidatoModel candidato;

        @BeforeEach
        void setUp() {
            usuarioEleitor = usuario(1L, Perfil.ELEITOR);
            eleitor = eleitor(usuarioEleitor);
            eleicaoAberta = eleicao(100L, StatusEleicao.ABERTA);
            candidato = candidato(usuario(50L, Perfil.CANDIDATO), 10, partidoA, governador, eleicaoAberta);
        }

        private void eleitorEEleicaoExistem(EleicaoModel eleicao) {
            when(usuarioRepository.findByUsername("user1")).thenReturn(Optional.of(usuarioEleitor));
            when(eleitorRepository.findById(1L)).thenReturn(Optional.of(eleitor));
            when(eleicaoRepository.findById(eleicao.getId())).thenReturn(Optional.of(eleicao));
        }

        @Test
        void deveRegistrarControleEVotoSemVincularOEleitorAoVoto() {
            eleitorEEleicaoExistem(eleicaoAberta);
            when(controleVotoRepository.existsByEleicaoIdAndEleitorIdUsuarioAndCargoId(100L, 1L, 4L)).thenReturn(false);
            when(candidatoRepository.findById(50L)).thenReturn(Optional.of(candidato));

            VotoConfirmacaoResponse resposta = service.registrarVoto("user1", new VotoRequest(100L, 4L, 50L));

            assertThat(resposta.cargo()).isEqualTo("Governador");
            assertThat(resposta.dataHora()).isNotNull();

            ArgumentCaptor<ControleVotoModel> controle = ArgumentCaptor.forClass(ControleVotoModel.class);
            verify(controleVotoRepository).save(controle.capture());
            assertThat(controle.getValue().getEleitor()).isSameAs(eleitor);
            assertThat(controle.getValue().getCargo()).isSameAs(governador);

            ArgumentCaptor<VotoModel> voto = ArgumentCaptor.forClass(VotoModel.class);
            verify(votoRepository).save(voto.capture());
            assertThat(voto.getValue().getCandidato()).isSameAs(candidato);
            assertThat(voto.getValue().getEleicao()).isSameAs(eleicaoAberta);
            // Sigilo: o voto não guarda nenhuma referência ao eleitor
            assertThat(VotoModel.class.getDeclaredFields())
                    .noneMatch(f -> EleitorModel.class.equals(f.getType()) || UsuarioModel.class.equals(f.getType()));
        }

        @Test
        void votoNaoGuardaHorarioNemIdSequencial() throws Exception {
            assertThat(VotoModel.class.getDeclaredFields())
                    .noneMatch(f -> Temporal.class.isAssignableFrom(f.getType()));
            assertThat(VotoModel.class.getDeclaredField("id").getType()).isEqualTo(UUID.class);
        }

        @Test
        void eleitorQueTambemEhCandidatoPodeVotar() {
            usuarioEleitor.adicionarPerfil(Perfil.CANDIDATO);
            eleitorEEleicaoExistem(eleicaoAberta);
            when(controleVotoRepository.existsByEleicaoIdAndEleitorIdUsuarioAndCargoId(100L, 1L, 4L)).thenReturn(false);
            when(candidatoRepository.findById(50L)).thenReturn(Optional.of(candidato));

            service.registrarVoto("user1", new VotoRequest(100L, 4L, 50L));

            verify(controleVotoRepository).save(any());
            verify(votoRepository).save(any());
        }

        @ParameterizedTest
        @EnumSource(value = Perfil.class, names = {"CANDIDATO", "CHEFE_SECAO"})
        void deveImpedirVotoDeQuemNaoEhEleitor(Perfil perfil) {
            when(usuarioRepository.findByUsername("outro")).thenReturn(Optional.of(usuario(9L, perfil)));

            assertThatThrownBy(() -> service.registrarVoto("outro", new VotoRequest(100L, 4L, 50L)))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("ELEITOR");
            verifyNoInteractions(controleVotoRepository, votoRepository);
        }

        @ParameterizedTest
        @EnumSource(value = StatusEleicao.class, names = {"AGENDADA", "ENCERRADA"})
        void deveImpedirVotoEmEleicaoQueNaoEstaAberta(StatusEleicao status) {
            eleitorEEleicaoExistem(eleicao(100L, status));

            assertThatThrownBy(() -> service.registrarVoto("user1", new VotoRequest(100L, 4L, 50L)))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("não está aberta");
            verifyNoInteractions(controleVotoRepository, votoRepository);
        }

        @Test
        void deveImpedirSegundoVotoParaOMesmoCargo() {
            eleitorEEleicaoExistem(eleicaoAberta);
            when(controleVotoRepository.existsByEleicaoIdAndEleitorIdUsuarioAndCargoId(100L, 1L, 4L)).thenReturn(true);

            assertThatThrownBy(() -> service.registrarVoto("user1", new VotoRequest(100L, 4L, 50L)))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("já votou");
            verify(controleVotoRepository, never()).save(any());
            verifyNoInteractions(votoRepository);
        }

        @Test
        void deveImpedirVotoEmCandidatoDeOutroCargo() {
            eleitorEEleicaoExistem(eleicaoAberta);
            when(controleVotoRepository.existsByEleicaoIdAndEleitorIdUsuarioAndCargoId(100L, 1L, 3L)).thenReturn(false);
            when(candidatoRepository.findById(50L)).thenReturn(Optional.of(candidato));

            assertThatThrownBy(() -> service.registrarVoto("user1", new VotoRequest(100L, 3L, 50L)))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("não concorre");
            verifyNoInteractions(votoRepository);
        }

        @Test
        void deveImpedirVotoEmCandidatoDeOutraEleicao() {
            eleitorEEleicaoExistem(eleicaoAberta);
            CandidatoModel deOutraEleicao = candidato(usuario(51L, Perfil.CANDIDATO), 11, partidoA, governador,
                    eleicao(999L, StatusEleicao.ABERTA));
            when(controleVotoRepository.existsByEleicaoIdAndEleitorIdUsuarioAndCargoId(100L, 1L, 4L)).thenReturn(false);
            when(candidatoRepository.findById(51L)).thenReturn(Optional.of(deOutraEleicao));

            assertThatThrownBy(() -> service.registrarVoto("user1", new VotoRequest(100L, 4L, 51L)))
                    .isInstanceOf(RegraNegocioException.class);
            verifyNoInteractions(votoRepository);
        }

        @Test
        void deveFalharQuandoCandidatoNaoExiste() {
            eleitorEEleicaoExistem(eleicaoAberta);
            when(controleVotoRepository.existsByEleicaoIdAndEleitorIdUsuarioAndCargoId(100L, 1L, 4L)).thenReturn(false);
            when(candidatoRepository.findById(77L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.registrarVoto("user1", new VotoRequest(100L, 4L, 77L)))
                    .isInstanceOf(EntidadeNaoEncontradaException.class);
        }
    }

    @Nested
    class AcompanharVotacao {

        private EleicaoModel eleicao;
        private CandidatoModel gov1;
        private CandidatoModel gov2;
        private CandidatoModel sen1;

        @BeforeEach
        void setUp() {
            eleicao = eleicao(100L, StatusEleicao.ABERTA);
            gov1 = candidato(usuario(50L, Perfil.CANDIDATO), 10, partidoA, governador, eleicao);
            gov2 = candidato(usuario(51L, Perfil.CANDIDATO), 20, partidoB, governador, eleicao);
            sen1 = candidato(usuario(52L, Perfil.CANDIDATO), 100, partidoA, senador, eleicao);
        }

        @Test
        void eleitorVeTodosOsCargosComCandidatosOrdenadosPorVotos() {
            when(usuarioRepository.findByUsername("eleitor")).thenReturn(Optional.of(usuario(1L, Perfil.ELEITOR)));
            when(eleicaoRepository.findById(100L)).thenReturn(Optional.of(eleicao));
            when(candidatoRepository.findByEleicaoId(100L)).thenReturn(List.of(gov1, gov2, sen1));
            when(votoRepository.countByEleicaoIdAndCandidatoIdUsuario(100L, 50L)).thenReturn(3L);
            when(votoRepository.countByEleicaoIdAndCandidatoIdUsuario(100L, 51L)).thenReturn(7L);
            when(votoRepository.countByEleicaoIdAndCandidatoIdUsuario(100L, 52L)).thenReturn(1L);

            List<ApuracaoCargoResponse> apuracao = service.acompanharVotacao("eleitor", 100L);

            assertThat(apuracao).extracting(ApuracaoCargoResponse::nomeCargo)
                    .containsExactlyInAnyOrder("Governador", "Senador");
            ApuracaoCargoResponse gov = apuracao.stream()
                    .filter(a -> a.idCargo().equals(4L)).findFirst().orElseThrow();
            assertThat(gov.candidatos()).extracting(c -> c.idCandidato()).containsExactly(51L, 50L);
            assertThat(gov.candidatos()).extracting(c -> c.totalVotos()).containsExactly(7L, 3L);
        }

        @Test
        void candidatoVeApenasOCargoAoQualConcorre() {
            UsuarioModel usuarioCandidato = gov1.getUsuario();
            when(usuarioRepository.findByUsername(usuarioCandidato.getUsername())).thenReturn(Optional.of(usuarioCandidato));
            when(eleicaoRepository.findById(100L)).thenReturn(Optional.of(eleicao));
            when(candidatoRepository.findById(50L)).thenReturn(Optional.of(gov1));
            when(candidatoRepository.findByEleicaoIdAndCargoId(100L, 4L)).thenReturn(List.of(gov1, gov2));
            when(votoRepository.countByEleicaoIdAndCandidatoIdUsuario(eq(100L), anyLong())).thenReturn(0L);

            List<ApuracaoCargoResponse> apuracao = service.acompanharVotacao(usuarioCandidato.getUsername(), 100L);

            assertThat(apuracao).singleElement()
                    .extracting(ApuracaoCargoResponse::nomeCargo).isEqualTo("Governador");
            verify(candidatoRepository, never()).findByEleicaoId(any());
        }

        @Test
        void candidatoQueTambemEhEleitorVeTodosOsCargos() {
            UsuarioModel usuarioCandidato = gov1.getUsuario();
            usuarioCandidato.adicionarPerfil(Perfil.ELEITOR);
            when(usuarioRepository.findByUsername(usuarioCandidato.getUsername())).thenReturn(Optional.of(usuarioCandidato));
            when(eleicaoRepository.findById(100L)).thenReturn(Optional.of(eleicao));
            when(candidatoRepository.findByEleicaoId(100L)).thenReturn(List.of(gov1, gov2, sen1));
            when(votoRepository.countByEleicaoIdAndCandidatoIdUsuario(eq(100L), anyLong())).thenReturn(0L);

            List<ApuracaoCargoResponse> apuracao = service.acompanharVotacao(usuarioCandidato.getUsername(), 100L);

            assertThat(apuracao).extracting(ApuracaoCargoResponse::nomeCargo)
                    .containsExactlyInAnyOrder("Governador", "Senador");
            verify(candidatoRepository, never()).findByEleicaoIdAndCargoId(any(), any());
        }

        @Test
        void naoPermiteAcompanharEleicaoAgendada() {
            when(usuarioRepository.findByUsername("eleitor")).thenReturn(Optional.of(usuario(1L, Perfil.ELEITOR)));
            when(eleicaoRepository.findById(100L)).thenReturn(Optional.of(eleicao(100L, StatusEleicao.AGENDADA)));

            assertThatThrownBy(() -> service.acompanharVotacao("eleitor", 100L))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("não foi iniciada");
        }

        @Test
        void naoPermiteAcompanharEleicaoEncerrada() {
            when(usuarioRepository.findByUsername("eleitor")).thenReturn(Optional.of(usuario(1L, Perfil.ELEITOR)));
            when(eleicaoRepository.findById(100L)).thenReturn(Optional.of(eleicao(100L, StatusEleicao.ENCERRADA)));

            assertThatThrownBy(() -> service.acompanharVotacao("eleitor", 100L))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("encerrada");
        }

        @Test
        void candidatoNaoPodeAcompanharEleicaoEmQueNaoConcorre() {
            EleicaoModel outraEleicao = eleicao(200L, StatusEleicao.ABERTA);
            UsuarioModel usuarioCandidato = gov1.getUsuario();
            when(usuarioRepository.findByUsername(usuarioCandidato.getUsername())).thenReturn(Optional.of(usuarioCandidato));
            when(eleicaoRepository.findById(200L)).thenReturn(Optional.of(outraEleicao));
            when(candidatoRepository.findById(50L)).thenReturn(Optional.of(gov1));

            assertThatThrownBy(() -> service.acompanharVotacao(usuarioCandidato.getUsername(), 200L))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("eleição em que está concorrendo");
            verify(candidatoRepository, never()).findByEleicaoIdAndCargoId(any(), any());
        }
    }

    @Nested
    class ResultadoFinal {

        @ParameterizedTest
        @EnumSource(value = StatusEleicao.class, names = {"AGENDADA", "ABERTA"})
        void soFicaDisponivelAposEncerramento(StatusEleicao status) {
            when(eleicaoRepository.findById(100L)).thenReturn(Optional.of(eleicao(100L, status)));

            assertThatThrownBy(() -> service.consultarResultadoFinal(100L))
                    .isInstanceOf(RegraNegocioException.class)
                    .hasMessageContaining("encerramento");
            verifyNoInteractions(votoRepository);
        }

        @Test
        void apontaOMaisVotadoDeCadaCargo() {
            EleicaoModel encerrada = eleicao(100L, StatusEleicao.ENCERRADA);
            CandidatoModel gov1 = candidato(usuario(50L, Perfil.CANDIDATO), 10, partidoA, governador, encerrada);
            CandidatoModel gov2 = candidato(usuario(51L, Perfil.CANDIDATO), 20, partidoB, governador, encerrada);
            CandidatoModel sen1 = candidato(usuario(52L, Perfil.CANDIDATO), 100, partidoA, senador, encerrada);
            when(eleicaoRepository.findById(100L)).thenReturn(Optional.of(encerrada));
            when(candidatoRepository.findByEleicaoId(100L)).thenReturn(List.of(gov1, gov2, sen1));
            when(votoRepository.countByEleicaoIdAndCandidatoIdUsuario(100L, 50L)).thenReturn(12L);
            when(votoRepository.countByEleicaoIdAndCandidatoIdUsuario(100L, 51L)).thenReturn(5L);
            when(votoRepository.countByEleicaoIdAndCandidatoIdUsuario(100L, 52L)).thenReturn(2L);

            List<ResultadoFinalCargoResponse> resultado = service.consultarResultadoFinal(100L);

            assertThat(resultado).hasSize(2);
            ResultadoFinalCargoResponse gov = resultado.stream()
                    .filter(r -> r.idCargo().equals(4L)).findFirst().orElseThrow();
            assertThat(gov.empate()).isFalse();
            assertThat(gov.empatados()).isEmpty();
            assertThat(gov.maisVotado().idCandidato()).isEqualTo(50L);
            assertThat(gov.maisVotado().totalVotos()).isEqualTo(12L);
            assertThat(gov.candidatos()).hasSize(2);
            ResultadoFinalCargoResponse sen = resultado.stream()
                    .filter(r -> r.idCargo().equals(3L)).findFirst().orElseThrow();
            assertThat(sen.empate()).isFalse();
            assertThat(sen.maisVotado().idCandidato()).isEqualTo(52L);
        }

        @Test
        void indicaEmpateQuandoDoisCandidatosDividemMaiorVotacao() {
            EleicaoModel encerrada = eleicao(100L, StatusEleicao.ENCERRADA);
            CandidatoModel gov1 = candidato(usuario(50L, Perfil.CANDIDATO), 10, partidoA, governador, encerrada);
            CandidatoModel gov2 = candidato(usuario(51L, Perfil.CANDIDATO), 20, partidoB, governador, encerrada);
            when(eleicaoRepository.findById(100L)).thenReturn(Optional.of(encerrada));
            when(candidatoRepository.findByEleicaoId(100L)).thenReturn(List.of(gov1, gov2));
            when(votoRepository.countByEleicaoIdAndCandidatoIdUsuario(100L, 50L)).thenReturn(5L);
            when(votoRepository.countByEleicaoIdAndCandidatoIdUsuario(100L, 51L)).thenReturn(5L);

            List<ResultadoFinalCargoResponse> resultado = service.consultarResultadoFinal(100L);

            ResultadoFinalCargoResponse gov = resultado.get(0);
            assertThat(gov.empate()).isTrue();
            assertThat(gov.maisVotado()).isNull();
            assertThat(gov.empatados()).extracting(c -> c.idCandidato()).containsExactly(50L, 51L);
        }

        @Test
        void naoApontaMaisVotadoQuandoCargoNaoRecebeuVotos() {
            EleicaoModel encerrada = eleicao(100L, StatusEleicao.ENCERRADA);
            CandidatoModel gov1 = candidato(usuario(50L, Perfil.CANDIDATO), 10, partidoA, governador, encerrada);
            CandidatoModel gov2 = candidato(usuario(51L, Perfil.CANDIDATO), 20, partidoB, governador, encerrada);
            when(eleicaoRepository.findById(100L)).thenReturn(Optional.of(encerrada));
            when(candidatoRepository.findByEleicaoId(100L)).thenReturn(List.of(gov1, gov2));
            when(votoRepository.countByEleicaoIdAndCandidatoIdUsuario(eq(100L), anyLong())).thenReturn(0L);

            List<ResultadoFinalCargoResponse> resultado = service.consultarResultadoFinal(100L);

            ResultadoFinalCargoResponse gov = resultado.get(0);
            assertThat(gov.empate()).isFalse();
            assertThat(gov.maisVotado()).isNull();
            assertThat(gov.empatados()).isEmpty();
            assertThat(gov.candidatos()).hasSize(2);
        }

        @Test
        void falhaQuandoEleicaoNaoExiste() {
            when(eleicaoRepository.findById(404L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.consultarResultadoFinal(404L))
                    .isInstanceOf(EntidadeNaoEncontradaException.class);
        }
    }
}
