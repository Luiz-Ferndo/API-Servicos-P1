package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.DominioStatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório para operações CRUD relacionadas à entidade DominioStatusAgendamento.
 * Extende JpaRepository para fornecer métodos padrão de acesso a dados.
 */
public interface DominioStatusAgendamentoRepository extends JpaRepository<DominioStatusAgendamento, Integer> {

}