package com.example.eleicao_service.feature.partido.service.impl;

import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.partido.model.dto.PartidoRequest;
import com.example.eleicao_service.feature.partido.model.dto.PartidoResponse;
import com.example.eleicao_service.feature.partido.model.entity.PartidoModel;
import com.example.eleicao_service.feature.partido.repository.PartidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.example.eleicao_service.support.Fixtures.partido;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartidoServiceImplTest {

    @Mock
    private PartidoRepository partidoRepository;

    @InjectMocks
    private PartidoServiceImpl service;

    private final PartidoRequest request = new PartidoRequest("Partido Azul", "PA", 10);

    @Test
    void cadastrar_salvaPartidoNovo() {
        when(partidoRepository.save(any())).thenAnswer(inv -> {
            PartidoModel p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        PartidoResponse resposta = service.cadastrar(request);

        assertThat(resposta).isEqualTo(new PartidoResponse(1L, "Partido Azul", "PA", 10));
    }

    @Test
    void cadastrar_impedeNumeroDuplicado() {
        when(partidoRepository.existsByNumero(10)).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(request)).isInstanceOf(RegraNegocioException.class);
        verify(partidoRepository, never()).save(any());
    }

    @Test
    void cadastrar_impedeSiglaDuplicada() {
        when(partidoRepository.existsBySigla("PA")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(request)).isInstanceOf(RegraNegocioException.class);
        verify(partidoRepository, never()).save(any());
    }

    @Test
    void cadastrar_impedeNomeDuplicado() {
        when(partidoRepository.existsByNome("Partido Azul")).thenReturn(true);

        assertThatThrownBy(() -> service.cadastrar(request)).isInstanceOf(RegraNegocioException.class);
        verify(partidoRepository, never()).save(any());
    }

    @Test
    void listarTodos_mapeiaParaResponse() {
        when(partidoRepository.findAll()).thenReturn(List.of(partido(1L, "PA", 10), partido(2L, "PB", 20)));

        assertThat(service.listarTodos()).extracting(PartidoResponse::sigla).containsExactly("PA", "PB");
    }
}
