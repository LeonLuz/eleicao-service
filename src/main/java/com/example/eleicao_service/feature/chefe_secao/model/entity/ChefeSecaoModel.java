package com.example.eleicao_service.feature.chefe_secao.model.entity;

import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="CHEFE_SECAO")
public class ChefeSecaoModel {
    @Id
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @OneToOne
    @MapsId
    @JoinColumn(name="id_usuario")
    private UsuarioModel usuario;
}