package com.example.eleicao_service.support;

import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import com.example.eleicao_service.feature.cargo.model.CargoModel;
import com.example.eleicao_service.feature.eleicao.model.StatusEleicao;
import com.example.eleicao_service.feature.eleicao.model.entity.EleicaoModel;
import com.example.eleicao_service.feature.eleitor.model.entity.EleitorModel;
import com.example.eleicao_service.feature.partido.model.entity.PartidoModel;
import com.example.eleicao_service.feature.usuario.model.Perfil;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/** Objetos de domínio prontos para os testes unitários. */
public final class Fixtures {

    private Fixtures() {}

    public static UsuarioModel usuario(Long id, Perfil... perfis) {
        return UsuarioModel.builder()
                .id(id)
                .nome("Usuario " + id)
                .cpf(String.format("%011d", id))
                .username("user" + id)
                .senha("hash")
                .perfis(new HashSet<>(Set.of(perfis)))
                .ativo(true)
                .build();
    }

    public static CargoModel cargo(Long id, String nome) {
        return new CargoModel(id, nome);
    }

    public static PartidoModel partido(Long id, String sigla, int numero) {
        return PartidoModel.builder().id(id).nome("Partido " + sigla).sigla(sigla).numero(numero).build();
    }

    public static EleicaoModel eleicao(Long id, StatusEleicao status) {
        return EleicaoModel.builder()
                .id(id)
                .descricao("Eleição " + id)
                .dataEleicao(LocalDate.of(2026, 10, 4))
                .turno(1)
                .horarioAbertura(LocalTime.of(8, 0))
                .horarioEncerramento(LocalTime.of(17, 0))
                .status(status)
                .build();
    }

    public static EleitorModel eleitor(UsuarioModel usuario) {
        return EleitorModel.builder()
                .idUsuario(usuario.getId())
                .usuario(usuario)
                .tituloEleitor("000000000001")
                .zona("001")
                .secao("0001")
                .build();
    }

    public static CandidatoModel candidato(UsuarioModel usuario, int numero, PartidoModel partido,
                                           CargoModel cargo, EleicaoModel eleicao) {
        return CandidatoModel.builder()
                .idUsuario(usuario.getId())
                .usuario(usuario)
                .numeroEleitoral(numero)
                .partido(partido)
                .cargo(cargo)
                .eleicao(eleicao)
                .build();
    }
}
