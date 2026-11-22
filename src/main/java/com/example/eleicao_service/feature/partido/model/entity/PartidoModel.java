package com.example.eleicao_service.feature.partido.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "PARTIDO")
public class PartidoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_legenda", nullable = false, unique = true)
    private Integer idLegenda;

    @Column(nullable = false, unique = true, length = 255)
    private String nome;
}