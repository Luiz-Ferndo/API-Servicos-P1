package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.Role;
import com.prestacaoservicos.enums.RoleNameEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositório para operações CRUD relacionadas à entidade Role.
 * Extende JpaRepository para fornecer métodos padrão de acesso a dados.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Busca uma Role pelo seu nome.
     *
     * @param name O nome da Role a ser buscada.
     * @return Um Optional contendo a Role, se encontrada, ou vazio caso contrário.
     */
    Optional<Role> findByName(RoleNameEnum name);
}