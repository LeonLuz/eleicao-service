package com.example.eleicao_service.feature.chefe_secao.controller;

import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoDTO;
import com.example.eleicao_service.feature.chefe_secao.model.dto.ChefeSecaoInputDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chefes de Seção", description = "Gerenciamento dos chefes de seção das eleições")
public interface ChefeSecaoController {

    @Operation(summary = "Listar chefes de seção", description = "Retorna todos os chefes de seção cadastrados em ordem alfabética por nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de chefes de seção retornada com sucesso")
    })
    @GetMapping
    ResponseEntity<List<ChefeSecaoDTO>> findAll();

    @Operation(summary = "Buscar chefe de seção por ID", description = "Retorna os dados de um chefe de seção específico pelo ID do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chefe de seção encontrado"),
            @ApiResponse(responseCode = "404", description = "Chefe de seção não encontrado")
    })
    @GetMapping("/{id}")
    ResponseEntity<ChefeSecaoDTO> findById(
            @Parameter(description = "ID do usuário/chefe de seção", example = "1")
            @PathVariable("id") Integer id
    );

    @Operation(summary = "Cadastrar chefe de seção", description = "Atribui o perfil de chefe de seção a um usuário existente que não seja candidato")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Chefe de seção cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Usuário inativo, já é candidato ou já cadastrado como chefe de seção")
    })
    @PostMapping
    ResponseEntity<ChefeSecaoDTO> insert(@RequestBody @Valid ChefeSecaoInputDTO chefeSecaoInputDTO);

    @Operation(summary = "Atualizar chefe de seção", description = "Atualiza os dados de um chefe de seção existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chefe de seção atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Chefe de seção não encontrado")
    })
    @PutMapping("/{id}")
    ResponseEntity<ChefeSecaoDTO> update(
            @RequestBody @Valid ChefeSecaoInputDTO chefeSecaoInputDTO,
            @Parameter(description = "ID do chefe de seção", example = "1")
            @PathVariable("id") Integer id
    );

    @Operation(summary = "Remover chefe de seção", description = "Remove o perfil de chefe de seção e revoga a role CHEFE_SECAO do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Chefe de seção removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Chefe de seção não encontrado")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<ChefeSecaoDTO> delete(
            @Parameter(description = "ID do chefe de seção", example = "1")
            @PathVariable("id") Integer id
    );
}