package com.example.eleicao_service.feature.votacao.model.entity;

import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import com.example.eleicao_service.feature.cargo.model.CargoModel;
import com.example.eleicao_service.feature.eleicao.model.entity.EleicaoModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "voto")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VotoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_eleicao", nullable = false)
    private EleicaoModel eleicao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cargo", nullable = false)
    private CargoModel cargo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_candidato", nullable = false)
    private CandidatoModel candidato;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
}