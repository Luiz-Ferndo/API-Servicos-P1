package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.UserPhone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositório para operações CRUD relacionadas à entidade UserPhone
 * Extende JpaRepository para fornecer métodos padrão de acesso a dados.
 */
public interface UserPhoneRepository extends JpaRepository<UserPhone, Long> {
}