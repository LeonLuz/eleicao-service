package com.example.eleicao_service.feature.partido.service;


import com.example.eleicao_service.feature.partido.model.dto.PartidoRequest;
import com.example.eleicao_service.feature.partido.model.dto.PartidoResponse;

import java.util.List;

public interface PartidoService {
    PartidoResponse cadastrar(PartidoRequest request);
    List<PartidoResponse> listarTodos();
}