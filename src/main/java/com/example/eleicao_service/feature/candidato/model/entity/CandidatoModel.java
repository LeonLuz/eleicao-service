package com.example.eleicao_service.feature.candidato.model.entity;

import com.example.eleicao_service.feature.cargo.model.CargoModel;
import com.example.eleicao_service.feature.partido.model.entity.PartidoModel;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "CANDIDATO",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_partido_cargo_candidato",
                columnNames = {"id_cargo", "id_partido", "id_eleitoral"}
        ))
public class CandidatoModel {

    @Id
    private Integer id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private UsuarioModel usuario;

    @Column(nullable = false, length = 255)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cargo", nullable = false)
    private CargoModel cargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_partido", nullable = false)
    private PartidoModel partido;

    @Column(name = "id_eleitoral", nullable = false)
    private Integer idEleitoral = 0;

    @Transient
    public String getNumeroCompleto() {
        if (partido == null || cargo == null || idEleitoral == null) {
            return null;
        }
        String legenda = String.valueOf(partido.getIdLegenda());
        if (cargo.getQtdDigitos() == 0) {
            return legenda;
        }
        String sufixo = String.format("%0" + cargo.getQtdDigitos() + "d", idEleitoral);
        return legenda + sufixo;
    }
}