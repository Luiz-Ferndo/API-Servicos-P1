package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    /**
     * Busca um usuário pelo seu email, incluindo suas roles e permissões.
     *
     * @param email O email do usuário a ser buscado.
     * @return Um Optional contendo o usuário com suas roles e permissões, se encontrado.
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.email = :email")
    Optional<User> findByEmailWithRolesAndPermissions(@Param("email") String email);

    /**
     * Verifica se um usuário é um prestador de serviços.
     *
     * @param userId O ID do usuário a ser verificado.
     * @return Verdadeiro se o usuário for um prestador de serviços, falso caso contrário.
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END " +
            "FROM User u JOIN u.servicosOferecidos s " +
            "WHERE u.id = :userId AND s.id = :servicoId")
    boolean prestadorOfereceServico(@Param("userId") Long userId, @Param("servicoId") Long servicoId);

    /**
     * Busca um usuário pelo seu ID, incluindo suas roles e permissões.
     *
     * @param id O ID do usuário a ser buscado.
     * @return Um Optional contendo o usuário com suas roles e permissões, se encontrado.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions WHERE u.id = :id")
    Optional<User> findByIdWithRolesAndPermissions(@Param("id") Long id);

    /**
     * Busca todos os usuários, incluindo suas roles e permissões.
     *
     * @return Uma lista de usuários com suas roles e permissões.
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles r LEFT JOIN FETCH r.permissions")
    List<User> findAllWithRolesAndPermissions();
}