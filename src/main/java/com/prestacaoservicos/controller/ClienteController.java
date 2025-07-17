package com.prestacaoservicos.controller;


import com.prestacaoservicos.entity.Cliente;
import com.prestacaoservicos.repository.ClienteRepository;
import com.prestacaoservicos.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private ClienteService clienteService;

    public List<Cliente> listarClientes() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public Optional<Cliente> listarPorId(Long id) {
        return clienteService.buscarPorId(id);
    }

    @PostMapping
    public Cliente criarCliente(@RequestBody Cliente cliente) {
        return clienteService.salvar(cliente);
    }

    @PutMapping("/{id}")
    public Cliente atualizarCliente(@PathVariable Long id, @RequestBody Cliente cliente) {
        return clienteService.atualizar(id, cliente);
    }

    @DeleteMapping("{id}")
    public void deletar(@PathVariable Long id) {
        clienteService.deletar(id);
    }
}
