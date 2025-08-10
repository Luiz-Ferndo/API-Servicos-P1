package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório para operações CRUD relacionadas à entidade User.
 * Extende JpaRepository para fornecer métodos padrão de acesso a dados.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Busca um usuário pelo seu email.
     *
     * @param email O email do usuário a ser buscado.
     * @return Um Optional contendo o usuário, se encontrado, ou vazio caso contrário.
     */
    Optional<User> findByEmail(String email);
}