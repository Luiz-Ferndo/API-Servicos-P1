package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório para operações CRUD relacionadas à entidade Permission.
 * Extende JpaRepository para fornecer métodos padrão de acesso a dados.
 */
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    /**
     * Busca uma Permission pelo seu nome.
     *
     * @param name O nome da Permission a ser buscada.
     * @return Um Optional contendo a Permission, se encontrada, ou vazio caso contrário.
     */
    Optional<Permission> findByName(String name);
}