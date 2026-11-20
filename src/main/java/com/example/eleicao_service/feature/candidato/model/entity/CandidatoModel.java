package com.example.eleicao_service.feature.candidato.model.entity;

import com.example.eleicao_service.feature.candidato.model.Cargo;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "CANDIDATO",
        uniqueConstraints = @UniqueConstraint(name = "uk_candidato_numero_cargo", columnNames = {"numero", "cargo"}))
public class CandidatoModel {
    @Id
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false, length = 50)
    private String partido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Cargo cargo;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private UsuarioModel usuario;
}
