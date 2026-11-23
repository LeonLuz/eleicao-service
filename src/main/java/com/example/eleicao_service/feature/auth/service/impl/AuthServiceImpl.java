package com.example.eleicao_service.feature.auth.service.impl;

import com.example.eleicao_service.core.security.jwt.service.JwtService;
import com.example.eleicao_service.feature.auth.dto.LoginRequest;
import com.example.eleicao_service.feature.auth.dto.LoginResponse;
import com.example.eleicao_service.feature.auth.service.AuthService;
import com.example.eleicao_service.feature.usuario.model.entity.UsuarioModel;
import com.example.eleicao_service.feature.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.senha())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UsuarioModel usuario = usuarioRepository.findByUsername(request.username()).orElseThrow();
        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(token, "Bearer", usuario.getPerfil().name(), usuario.getNome());
    }
}