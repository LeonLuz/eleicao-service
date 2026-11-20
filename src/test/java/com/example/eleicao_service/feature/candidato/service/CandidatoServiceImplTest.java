package com.example.eleicao_service.feature.candidato.service;

import com.example.eleicao_service.core.handler.exception.EntidadeNaoEncontradaException;
import com.example.eleicao_service.core.handler.exception.RegraNegocioException;
import com.example.eleicao_service.feature.candidato.mapper.CandidatoMapper;
import com.example.eleicao_service.feature.candidato.model.Cargo;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoDTO;
import com.example.eleicao_service.feature.candidato.model.dto.CandidatoInputDTO;
import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import com.example.eleicao_service.feature.candidato.repository.CandidatoRepository;
import com.example.eleicao_service.feature.usuario.model.Role;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidatoServiceImplTest {

    @Mock
    private CandidatoRepository candidatoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CandidatoMapper candidatoMapper;

    @InjectMocks
    private CandidatoServiceImpl service;

    private static UsuarioModel usuario(Integer id, Role... roles) {
        UsuarioModel u = new UsuarioModel();
        u.setId(id);
        u.setUsername("user" + id);
        u.setRoles(List.of(roles));
        return u;
    }

    private static CandidatoInputDTO input(Integer numero, Cargo cargo) {
        return new CandidatoInputDTO(1, "Fulano", numero, "PXX", cargo);
    }

    @Test
    void insert_deveCadastrarCandidatoValido() {
        CandidatoInputDTO in = input(13, Cargo.PRESIDENTE);
        CandidatoModel model = new CandidatoModel();
        CandidatoDTO dto = new CandidatoDTO(1, "Fulano", 13, "PXX", Cargo.PRESIDENTE);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario(1, Role.CANDIDATO)));
        when(candidatoRepository.existsById(1)).thenReturn(false);
        when(candidatoRepository.existsByNumeroAndCargo(13, Cargo.PRESIDENTE)).thenReturn(false);
        when(candidatoMapper.toEntity(in)).thenReturn(model);
        when(candidatoRepository.save(model)).thenReturn(model);
        when(candidatoMapper.toDTO(model)).thenReturn(dto);

        assertThat(service.insert(in)).isEqualTo(dto);
        assertThat(model.getUsuario().getId()).isEqualTo(1);
    }

    @Test
    void insert_deveFalharQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.insert(input(13, Cargo.PRESIDENTE)))
                .isInstanceOf(EntidadeNaoEncontradaException.class);
        verify(candidatoRepository, never()).save(any());
    }

    @Test
    void insert_deveFalharQuandoUsuarioNaoTemPerfilCandidato() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario(1, Role.ELEITOR)));

        assertThatThrownBy(() -> service.insert(input(13, Cargo.PRESIDENTE)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("CANDIDATO");
    }

    @Test
    void insert_deveFalharQuandoUsuarioJaEhCandidato() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario(1, Role.CANDIDATO)));
        when(candidatoRepository.existsById(1)).thenReturn(true);

        assertThatThrownBy(() -> service.insert(input(13, Cargo.PRESIDENTE)))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void insert_deveFalharQuandoNumeroTemDigitosErradosParaOCargo() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario(1, Role.CANDIDATO)));
        when(candidatoRepository.existsById(1)).thenReturn(false);

        assertThatThrownBy(() -> service.insert(input(13, Cargo.SENADOR)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("3 dígitos");
    }

    @Test
    void insert_deveFalharQuandoNumeroJaUsadoNoMesmoCargo() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario(1, Role.CANDIDATO)));
        when(candidatoRepository.existsById(1)).thenReturn(false);
        when(candidatoRepository.existsByNumeroAndCargo(13, Cargo.PRESIDENTE)).thenReturn(true);

        assertThatThrownBy(() -> service.insert(input(13, Cargo.PRESIDENTE)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("número 13");
    }

    @Test
    void update_naoDeveRevalidarNumeroQuandoNumeroECargoNaoMudam() {
        CandidatoModel model = new CandidatoModel();
        model.setNumero(13);
        model.setCargo(Cargo.PRESIDENTE);
        when(candidatoRepository.findById(1)).thenReturn(Optional.of(model));
        when(candidatoRepository.save(model)).thenReturn(model);

        service.update(input(13, Cargo.PRESIDENTE), 1);

        verify(candidatoRepository, never()).existsByNumeroAndCargo(any(), any());
        verify(candidatoMapper).copyToEntity(any(), any());
    }

    @Test
    void findById_deveFalharQuandoCandidatoNaoExiste() {
        when(candidatoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(EntidadeNaoEncontradaException.class);
    }
}
