package com.example.eleicao_service.feature.eleicao.model.entity;

import com.example.eleicao_service.feature.eleicao.model.StatusEleicao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "eleicao", uniqueConstraints = @UniqueConstraint(name = "uk_eleicao_data_turno", columnNames = {"data_eleicao", "turno"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EleicaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Column(name = "data_eleicao", nullable = false)
    private LocalDate dataEleicao;

    @Column(nullable = false)
    private Integer turno;

    @Column(name = "horario_abertura", nullable = false)
    private LocalTime horarioAbertura;

    @Column(name = "horario_encerramento", nullable = false)
    private LocalTime horarioEncerramento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEleicao status = StatusEleicao.AGENDADA;
}