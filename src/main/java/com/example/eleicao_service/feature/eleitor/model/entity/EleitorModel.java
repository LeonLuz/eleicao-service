package com.example.eleicao_service.feature.eleitor.model.entity;

import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ELEITOR")
@Getter
@Setter
public class EleitorModel {
    @Id
    private Integer id;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, unique = true, length = 12)
    private String titulo;

    @Column(nullable = false)
    private String nome;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_usuario")
    private UsuarioModel usuario;
}