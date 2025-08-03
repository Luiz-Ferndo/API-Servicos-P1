package com.prestacaoservicos.controller;

import com.prestacaoservicos.entity.Cliente;
import com.prestacaoservicos.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Clientes", description = "Operações relacionadas ao gerenciamento de clientes")
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(summary = "Listar todos os clientes", description = "Retorna uma lista com todos os clientes cadastrados.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso")
    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteService.listar();
    }

    @Operation(summary = "Buscar cliente por ID", description = "Retorna os dados de um cliente com base no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @GetMapping("/{id}")
    public Optional<Cliente> listarPorId(
            @Parameter(description = "ID do cliente", example = "1", required = true)
            @PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    @Operation(summary = "Criar novo cliente", description = "Cria um novo cliente no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public Cliente criarCliente(
            @Parameter(description = "Dados do cliente para criação", required = true)
            @RequestBody Cliente cliente) {
        return clienteService.salvar(cliente);
    }

    @Operation(summary = "Atualizar cliente existente", description = "Atualiza os dados de um cliente existente com base no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @PutMapping("/{id}")
    public Cliente atualizarCliente(
            @Parameter(description = "ID do cliente para atualização", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do cliente", required = true)
            @RequestBody Cliente cliente) {
        return clienteService.atualizar(id, cliente);
    }

    @Operation(summary = "Deletar cliente por ID", description = "Remove um cliente do sistema com base no ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    })
    @DeleteMapping("/{id}")
    public void deletar(
            @Parameter(description = "ID do cliente para deletar", example = "1", required = true)
            @PathVariable Long id) {
        clienteService.deletar(id);
    }
}
