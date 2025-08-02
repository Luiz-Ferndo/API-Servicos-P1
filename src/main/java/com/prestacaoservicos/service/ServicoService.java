package com.prestacaoservicos.service;

import com.prestacaoservicos.entity.Servico;
import com.prestacaoservicos.exception.RecursoNaoEncontradoException;
import com.prestacaoservicos.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Serviço que provê a lógica de negócio para as operações CRUD (Criar, Ler, Atualizar, Deletar)
 * relacionadas à entidade {@link Servico}.
 *
 * @version 1.0
 * @since 02/08/2025
 */
@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    /**
     * Construtor para injeção de dependência do repositório de serviços.
     *
     * @param servicoRepository O repositório para acesso aos dados da entidade Servico.
     */
    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    /**
     * Retorna uma lista com todos os serviços cadastrados no sistema.
     *
     * @return Uma lista de {@link Servico}.
     */
    public List<Servico> listar() {
        return servicoRepository.findAll();
    }

    /**
     * Busca um serviço específico pelo seu identificador único (ID).
     *
     * @param id O ID do serviço a ser buscado.
     * @return Um {@link Optional} contendo o serviço, se encontrado, ou vazio caso contrário.
     */
    public Optional<Servico> buscarPorId(Long id) {
        return servicoRepository.findById(id);
    }

    /**
     * Persiste um novo serviço no banco de dados.
     *
     * @param servico O objeto {@link Servico} a ser salvo.
     * @return A entidade {@link Servico} salva, com seu ID preenchido.
     */
    public Servico salvar(Servico servico) {
        return servicoRepository.save(servico);
    }

    /**
     * Atualiza os dados de um serviço existente a partir de seu ID.
     *
     * @param id      O ID do serviço a ser atualizado.
     * @param servico O objeto {@link Servico} com as novas informações.
     * @return O serviço com os dados atualizados.
     * @throws RecursoNaoEncontradoException se nenhum serviço for encontrado com o ID fornecido.
     */
    public Servico atualizar(Long id, Servico servico) {
        return servicoRepository.findById(id).map(s -> {
            s.setCodigo(servico.getCodigo());
            s.setDescricao(servico.getDescricao());
            return servicoRepository.save(s);
        }).orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado"));
    }

    /**
     * Exclui um serviço do banco de dados com base em seu ID.
     *
     * @param id O ID do serviço a ser deletado.
     * @throws RecursoNaoEncontradoException se nenhum serviço for encontrado com o ID fornecido.
     */
    public void deletar(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Serviço não encontrado");
        }
        servicoRepository.deleteById(id);
    }
}