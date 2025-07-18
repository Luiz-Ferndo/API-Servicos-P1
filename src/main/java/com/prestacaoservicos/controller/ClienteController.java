package com.prestacaoservicos.controller;

import com.prestacaoservicos.entity.Cliente;
import com.prestacaoservicos.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os clientes")
    public List<Cliente> listarClientes() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar cliente por ID")
    public Optional<Cliente> listarPorId(
            @Parameter(description = "ID do cliente", example = "1", required = true)
            @PathVariable Long id) {
        return clienteService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Criar novo cliente")
    public Cliente criarCliente(
            @Parameter(description = "Dados do cliente para criação", required = true)
            @RequestBody Cliente cliente) {
        return clienteService.salvar(cliente);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente existente")
    public Cliente atualizarCliente(
            @Parameter(description = "ID do cliente para atualização", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do cliente", required = true)
            @RequestBody Cliente cliente) {
        return clienteService.atualizar(id, cliente);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar cliente por ID")
    public void deletar(
            @Parameter(description = "ID do cliente para deletar", example = "1", required = true)
            @PathVariable Long id) {
        clienteService.deletar(id);
    }
}
