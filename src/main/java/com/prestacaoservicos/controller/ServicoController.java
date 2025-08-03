package com.prestacaoservicos.controller;

import com.prestacaoservicos.entity.Servico;
import com.prestacaoservicos.service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Serviços", description = "Operações para gerenciamento de serviços oferecidos")
@RestController
@RequestMapping("/api/v1/servicos")
public class ServicoController {

    private final ServicoService servicosService;

    public ServicoController(ServicoService servicosService) {
        this.servicosService = servicosService;
    }

    @Operation(summary = "Listar todos os serviços", description = "Retorna uma lista de todos os serviços cadastrados.")
    @ApiResponse(responseCode = "200", description = "Serviços listados com sucesso")
    @GetMapping
    public List<Servico> listaServicos() {
        return servicosService.listar();
    }

    @Operation(summary = "Buscar serviço por ID", description = "Retorna um serviço específico com base no ID fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço encontrado"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @GetMapping("/{id}")
    public Optional<Servico> listaServicosPorId(
            @Parameter(description = "ID do serviço que deseja buscar", example = "1", required = true)
            @PathVariable Long id) {
        return servicosService.buscarPorId(id);
    }

    @Operation(summary = "Criar novo serviço", description = "Cria um novo serviço no sistema com os dados fornecidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public Servico criarServico(
            @Parameter(description = "Dados do serviço a ser criado", required = true)
            @RequestBody Servico servico) {
        return servicosService.salvar(servico);
    }

    @Operation(summary = "Atualizar serviço existente", description = "Atualiza um serviço existente com base no ID e nos novos dados fornecidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @PutMapping("/{id}")
    public Servico atualizarServico(
            @Parameter(description = "ID do serviço a ser atualizado", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do serviço", required = true)
            @RequestBody Servico servico) {
        return servicosService.atualizar(id, servico);
    }

    @Operation(summary = "Deletar serviço por ID", description = "Remove um serviço do sistema com base no ID informado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Serviço deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @DeleteMapping("/{id}")
    public void deletarServico(
            @Parameter(description = "ID do serviço a ser deletado", example = "1", required = true)
            @PathVariable Long id) {
        servicosService.deletar(id);
    }
}
