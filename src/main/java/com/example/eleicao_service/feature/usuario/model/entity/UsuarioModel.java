package com.example.eleicao_service.feature.usuario.model.entity;

import com.example.eleicao_service.feature.usuario.model.Perfil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, unique = true, length = 150)
    private String username;

    @Column(nullable = false)
    private String senha;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "perfis", columnDefinition = "json", nullable = false)
    @Builder.Default
    private Set<Perfil> perfis = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    public void adicionarPerfil(Perfil perfil) {
        if (this.perfis == null) {
            this.perfis = new HashSet<>();
        }
        this.perfis.add(perfil);
    }

    public boolean possuiPerfil(Perfil perfil) {
        return this.perfis != null && this.perfis.contains(perfil);
    }
}