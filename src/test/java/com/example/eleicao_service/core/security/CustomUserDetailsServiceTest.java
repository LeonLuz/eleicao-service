package com.example.eleicao_service.core.security;

import com.example.eleicao_service.feature.usuario.model.Perfil;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void loadUserByUsername_carregaUsuarioAtivoComAuthorityDoPerfil() {
        UsuarioModel usuario = UsuarioModel.builder()
                .id(1L)
                .nome("Maria")
                .cpf("52998224725")
                .username("maria")
                .senha("hash-bcrypt")
                .perfis(new HashSet<>(Set.of(Perfil.ELEITOR)))
                .ativo(true)
                .build();
        when(usuarioRepository.findByUsername("maria")).thenReturn(Optional.of(usuario));

        UserDetails details = service.loadUserByUsername("maria");

        assertThat(details.getUsername()).isEqualTo("maria");
        assertThat(details.getPassword()).isEqualTo("hash-bcrypt");
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ELEITOR");
    }

    @Test
    void loadUserByUsername_carregaTodasAsRolesQuandoUsuarioPossuiMultiplosPerfis() {
        UsuarioModel usuario = UsuarioModel.builder()
                .id(2L)
                .nome("Carlos")
                .cpf("11144477735")
                .username("carlos")
                .senha("hash-bcrypt")
                .perfis(new HashSet<>(Set.of(Perfil.CHEFE_SECAO, Perfil.ELEITOR)))
                .ativo(true)
                .build();
        when(usuarioRepository.findByUsername("carlos")).thenReturn(Optional.of(usuario));

        UserDetails details = service.loadUserByUsername("carlos");

        assertThat(details.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_CHEFE_SECAO", "ROLE_ELEITOR");
    }

    @Test
    void loadUserByUsername_marcaUsuarioComoDesabilitadoQuandoInativo() {
        UsuarioModel inativo = UsuarioModel.builder()
                .id(3L)
                .nome("Inativo")
                .cpf("98765432100")
                .username("inativo")
                .senha("hash")
                .perfis(new HashSet<>(Set.of(Perfil.ELEITOR)))
                .ativo(false)
                .build();
        when(usuarioRepository.findByUsername("inativo")).thenReturn(Optional.of(inativo));

        UserDetails details = service.loadUserByUsername("inativo");

        assertThat(details.isEnabled()).isFalse();
    }

    @Test
    void loadUserByUsername_lancaExcecaoQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findByUsername("fantasma")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("fantasma"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("fantasma");
    }
}
