package com.example.eleicao_service.feature.usuario.model.entity;

import com.example.eleicao_service.feature.chefe_secao.model.entity.ChefeSecaoModel;
import com.example.eleicao_service.feature.eleitor.model.entity.EleitorModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USUARIO")
@Getter
@Setter
public class UsuarioModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false)
    private String password;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> roles = new ArrayList<>();

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private ChefeSecaoModel chefeSecao;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private EleitorModel eleitor;

    public void setChefeSecao(ChefeSecaoModel chefeSecao) {
        this.chefeSecao = chefeSecao;
        if (chefeSecao != null) chefeSecao.setUsuario(this);
    }

    public void setEleitor(EleitorModel eleitor) {
        this.eleitor = eleitor;
        if (eleitor != null) eleitor.setUsuario(this);
    }
}