package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações CRUD relacionadas à entidade Servico.
 * Extende JpaRepository para fornecer métodos padrão de acesso a dados.
 */
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    /**
     * Busca um serviço pelo nome, ignorando diferenças de maiúsculas e minúsculas.
     *
     * @param nome O nome do serviço a ser buscado.
     * @return Um Optional contendo o serviço encontrado, ou vazio se não houver correspondência.
     */
    Optional<Servico> findByNomeIgnoreCase(String nome);

    /**
     * Busca todos os serviços que estão ativos (st_ativo = true).
     *
     * @return Uma lista de serviços ativos.
     */
    List<Servico> findAllByAtivoTrue();
}
