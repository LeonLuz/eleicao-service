package com.example.eleicao_service.feature.usuario.repository;

import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Integer> {

    boolean existsByUsername(String username);
}
