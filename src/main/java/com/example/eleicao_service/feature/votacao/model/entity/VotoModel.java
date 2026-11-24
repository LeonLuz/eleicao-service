package com.example.eleicao_service.feature.votacao.model.entity;

import com.example.eleicao_service.feature.candidato.model.entity.CandidatoModel;
import com.example.eleicao_service.feature.cargo.model.CargoModel;
import com.example.eleicao_service.feature.eleicao.model.entity.EleicaoModel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "voto")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VotoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_eleicao", nullable = false)
    private EleicaoModel eleicao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cargo", nullable = false)
    private CargoModel cargo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_candidato", nullable = false)
    private CandidatoModel candidato;
}