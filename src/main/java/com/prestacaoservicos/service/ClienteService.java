package com.prestacaoservicos.service;

import com.prestacaoservicos.entity.Cliente;
import com.prestacaoservicos.exception.RecursoNaoEncontradoException;
import com.prestacaoservicos.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Long id, Cliente cliente) {
        return clienteRepository.findById(id).map(c -> {
            c.setNome(cliente.getNome());
            c.setEmail(cliente.getEmail());
            return clienteRepository.save(c);
        }).orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
    }

    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
    }
}