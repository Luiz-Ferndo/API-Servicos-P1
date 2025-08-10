package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.Prestador;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório para operações CRUD relacionadas à entidade Prestador.
 * Extende JpaRepository para fornecer métodos padrão de acesso a dados.
 */
public interface PrestadorRepository extends JpaRepository<Prestador, Long> {
}