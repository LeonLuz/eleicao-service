package com.example.eleicao_service.core.config;

import com.example.eleicao_service.feature.usuario.model.Perfil;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class AdminConfig implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String[] args) {
        if (!usuarioRepository.existsByUsername(adminUsername)) {
            UsuarioModel chefe = UsuarioModel.builder()
                    .nome("Admin")
                    .cpf("00000000000")
                    .username(adminUsername)
                    .senha(passwordEncoder.encode(adminPassword))
                    .perfis(new HashSet<>(Set.of(Perfil.CHEFE_SECAO)))
                    .ativo(true)
                    .build();

            usuarioRepository.save(chefe);
        }
    }
}