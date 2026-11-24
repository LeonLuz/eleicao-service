package com.example.eleicao_service.feature.eleicao.service.impl;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.eleicao.model.StatusEleicao;
import com.example.eleicao_service.feature.eleicao.model.dto.EleicaoRequest;
import com.example.eleicao_service.feature.eleicao.model.dto.EleicaoResponse;
import com.example.eleicao_service.feature.eleicao.model.entity.EleicaoModel;
import com.example.eleicao_service.feature.eleicao.repository.EleicaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static com.example.eleicao_service.support.Fixtures.eleicao;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EleicaoServiceImplTest {

    @Mock
    private EleicaoRepository eleicaoRepository;

    @InjectMocks
    private EleicaoServiceImpl service;

    private static final LocalDate DATA = LocalDate.of(2026, 10, 4);

    private EleicaoRequest request(LocalTime abertura, LocalTime encerramento) {
        return new EleicaoRequest("Eleições Gerais", DATA, 1, abertura, encerramento);
    }

    @Test
    void cadastrar_criaEleicaoComStatusAgendada() {
        when(eleicaoRepository.existsByDataEleicaoAndTurno(DATA, 1)).thenReturn(false);
        when(eleicaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EleicaoResponse resposta = service.cadastrar(request(LocalTime.of(8, 0), LocalTime.of(17, 0)));

        assertThat(resposta.status()).isEqualTo(StatusEleicao.AGENDADA);
        assertThat(resposta.turno()).isEqualTo(1);
        assertThat(resposta.dataEleicao()).isEqualTo(DATA);
    }

    @Test
    void cadastrar_impedeDuasEleicoesNaMesmaDataETurno() {
        when(eleicaoRepository.existsByDataEleicaoAndTurno(DATA, 1)).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(request(LocalTime.of(8, 0), LocalTime.of(17, 0))))
                .isInstanceOf(RegraNegocioException.class);
        verify(eleicaoRepository, never()).save(any());
    }

    @Test
    void cadastrar_exigeEncerramentoDepoisDaAbertura() {
        when(eleicaoRepository.existsByDataEleicaoAndTurno(DATA, 1)).thenReturn(false);

        assertThatThrownBy(() -> service.cadastrar(request(LocalTime.of(17, 0), LocalTime.of(8, 0))))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("posterior");
        assertThatThrownBy(() -> service.cadastrar(request(LocalTime.of(8, 0), LocalTime.of(8, 0))))
                .isInstanceOf(RegraNegocioException.class);
        verify(eleicaoRepository, never()).save(any());
    }

    @Test
    void abrir_mudaDeAgendadaParaAberta() {
        EleicaoModel agendada = eleicao(1L, StatusEleicao.AGENDADA);
        when(eleicaoRepository.findById(1L)).thenReturn(Optional.of(agendada));
        when(eleicaoRepository.save(agendada)).thenReturn(agendada);

        assertThat(service.abrirVotacao(1L).status()).isEqualTo(StatusEleicao.ABERTA);
    }

    @ParameterizedTest
    @EnumSource(value = StatusEleicao.class, names = {"ABERTA", "ENCERRADA"})
    void abrir_soAceitaEleicaoAgendada(StatusEleicao status) {
        when(eleicaoRepository.findById(1L)).thenReturn(Optional.of(eleicao(1L, status)));

        assertThatThrownBy(() -> service.abrirVotacao(1L)).isInstanceOf(RegraNegocioException.class);
        verify(eleicaoRepository, never()).save(any());
    }

    @Test
    void encerrar_mudaDeAbertaParaEncerrada() {
        EleicaoModel aberta = eleicao(1L, StatusEleicao.ABERTA);
        when(eleicaoRepository.findById(1L)).thenReturn(Optional.of(aberta));
        when(eleicaoRepository.save(aberta)).thenReturn(aberta);

        assertThat(service.encerrarVotacao(1L).status()).isEqualTo(StatusEleicao.ENCERRADA);
    }

    @ParameterizedTest
    @EnumSource(value = StatusEleicao.class, names = {"AGENDADA", "ENCERRADA"})
    void encerrar_soAceitaEleicaoAberta(StatusEleicao status) {
        when(eleicaoRepository.findById(1L)).thenReturn(Optional.of(eleicao(1L, status)));

        assertThatThrownBy(() -> service.encerrarVotacao(1L)).isInstanceOf(RegraNegocioException.class);
        verify(eleicaoRepository, never()).save(any());
    }

    @Test
    void consultar_falhaQuandoEleicaoNaoExiste() {
        when(eleicaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultarPorId(99L)).isInstanceOf(EntidadeNaoEncontradaException.class);
    }
}
