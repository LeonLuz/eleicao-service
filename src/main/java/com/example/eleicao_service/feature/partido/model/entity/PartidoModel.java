package com.example.eleicao_service.feature.partido.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "partido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartidoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 20)
    private String sigla;

    @Column(nullable = false, unique = true)
    private Integer numero;
}