package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório para operações CRUD relacionadas à entidade Cliente.
 * Extende JpaRepository para fornecer métodos padrão de acesso a dados.
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}