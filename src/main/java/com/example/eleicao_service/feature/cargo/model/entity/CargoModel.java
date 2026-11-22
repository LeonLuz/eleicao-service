package com.example.eleicao_service.feature.cargo.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "CARGO")
public class CargoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(name = "qtd_digitos", nullable = false)
    private Integer qtdDigitos;

    public boolean sufixoValido(Integer idEleitoral) {
        if (idEleitoral == null || idEleitoral < 0) {
            return false;
        }
        if (this.qtdDigitos == 0) {
            return idEleitoral == 0;
        }
        int maximoPermitido = (int) Math.pow(10, this.qtdDigitos) - 1;
        return idEleitoral <= maximoPermitido;
    }
}