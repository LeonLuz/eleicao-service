package com.example.eleicao_service.feature.usuario.repository;

import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    Optional<UsuarioModel> findByUsername(String username);

    Optional<UsuarioModel> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByUsername(String username);
}