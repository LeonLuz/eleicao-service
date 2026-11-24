package com.example.eleicao_service.feature.eleitor.model.entity;

import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "eleitor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EleitorModel{

    @Id
    @Column(name = "id_usuario")
    private Long idUsuario;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @MapsId
    @JoinColumn(name = "id_usuario")
    private UsuarioModel usuario;

    @Column(name = "titulo_eleitor", nullable = false, unique = true, length = 12)
    private String tituloEleitor;

    @Column(nullable = false, length = 3)
    private String zona;

    @Column(nullable = false, length = 4)
    private String secao;
}