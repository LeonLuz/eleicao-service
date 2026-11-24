package com.example.eleicao_service.feature.candidato.model.entity;


import com.example.eleicao_service.feature.cargo.model.CargoModel;
import com.example.eleicao_service.feature.eleicao.model.entity.EleicaoModel;
import com.example.eleicao_service.feature.partido.model.entity.PartidoModel;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "candidato", uniqueConstraints = @UniqueConstraint(
        name = "uk_candidato_numero_eleicao", columnNames = {"numero_eleitoral", "id_cargo", "id_eleicao"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CandidatoModel {

    @Id
    @Column(name = "id_usuario")
    private Long idUsuario;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @MapsId
    @JoinColumn(name = "id_usuario")
    private UsuarioModel usuario;

    @Column(name = "numero_eleitoral", nullable = false)
    private Integer numeroEleitoral;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_partido", nullable = false)
    private PartidoModel partido;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cargo", nullable = false)
    private CargoModel cargo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_eleicao", nullable = false)
    private EleicaoModel eleicao;
}