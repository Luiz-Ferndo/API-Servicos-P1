package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório para operações CRUD relacionadas à entidade Servico.
 * Extende JpaRepository para fornecer métodos padrão de acesso a dados.
 */
public interface ServicoRepository extends JpaRepository<Servico, Long> {
}
