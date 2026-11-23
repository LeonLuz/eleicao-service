package com.example.eleicao_service.feature.votacao.model.entity;


import com.example.eleicao_service.feature.cargo.model.CargoModel;
import com.example.eleicao_service.feature.eleicao.model.entity.EleicaoModel;
import com.example.eleicao_service.feature.eleitor.model.entity.EleitorModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "controle_voto", uniqueConstraints = @UniqueConstraint(
        name = "uk_controle_voto", columnNames = {"id_eleicao", "id_eleitor", "id_cargo"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ControleVotoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_eleicao", nullable = false)
    private EleicaoModel eleicao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_eleitor", nullable = false)
    private EleitorModel eleitor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cargo", nullable = false)
    private CargoModel cargo;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
}