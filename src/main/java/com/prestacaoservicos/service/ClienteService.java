package com.prestacaoservicos.service;

import com.prestacaoservicos.entity.Cliente;
import com.prestacaoservicos.exception.RecursoNaoEncontradoException;
import com.prestacaoservicos.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Serviço que encapsula a lógica de negócio para operações CRUD (Criar, Ler, Atualizar, Deletar)
 * relacionadas à entidade {@link Cliente}.
 *
 * @version 1.0
 * @since 02/08/2025
 */
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    /**
     * Construtor para injeção de dependência do repositório de clientes.
     *
     * @param clienteRepository O repositório para acesso aos dados da entidade Cliente.
     */
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Retorna uma lista com todos os clientes cadastrados.
     *
     * @return Uma lista de {@link Cliente}. A lista pode estar vazia.
     */
    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    /**
     * Busca um cliente específico pelo seu ID.
     *
     * @param id O ID do cliente a ser buscado.
     * @return Um {@link Optional} contendo o cliente, se encontrado, ou vazio caso contrário.
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    /**
     * Salva um novo cliente ou atualiza um existente no banco de dados.
     *
     * @param cliente O objeto {@link Cliente} a ser salvo.
     * @return O cliente salvo, com o ID preenchido pelo banco de dados.
     */
    @Transactional
    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    /**
     * Atualiza os dados de um cliente existente com base em seu ID.
     *
     * @param id      O ID do cliente a ser atualizado.
     * @param cliente O objeto {@link Cliente} com as novas informações.
     * @return O cliente com os dados atualizados.
     * @throws RecursoNaoEncontradoException se nenhum cliente for encontrado com o ID fornecido.
     */
    @Transactional
    public Cliente atualizar(Long id, Cliente cliente) {
        return clienteRepository.findById(id).map(c -> {
            c.setNome(cliente.getNome());
            c.setEmail(cliente.getEmail());
            return clienteRepository.save(c);
        }).orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
    }

    /**
     * Deleta um cliente do banco de dados com base em seu ID.
     *
     * @param id O ID do cliente a ser deletado.
     * @throws RecursoNaoEncontradoException se nenhum cliente for encontrado com o ID fornecido.
     */
    @Transactional
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
    }
}