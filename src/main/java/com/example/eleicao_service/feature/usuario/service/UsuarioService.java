package com.example.eleicao_service.feature.usuario.service;

import com.example.eleicao_service.feature.usuario.model.dto.CriarChefeSecaoRequest;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioResponse;
import com.example.eleicao_service.feature.usuario.model.dto.UsuarioUpdateSelfRequest;

public interface UsuarioService {
    UsuarioResponse criarChefeSecao(CriarChefeSecaoRequest request);

    UsuarioResponse atualizarMeusDados(String usernameLogado, UsuarioUpdateSelfRequest request);

    UsuarioResponse buscarMeuPerfil(String usernameLogado);
}