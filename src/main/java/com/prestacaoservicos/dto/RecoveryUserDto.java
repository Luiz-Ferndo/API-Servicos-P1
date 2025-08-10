package com.prestacaoservicos.dto;

import java.util.List;

/**
 * DTO para recuperação de dados do usuário.
 * <p>
 * Contém as informações básicas retornadas ao buscar usuários,
 * incluindo id, email e lista de roles atribuídas.
 *
 * @param id    Identificador único do usuário.
 * @param email Endereço de email do usuário.
 * @param roles Lista de roles (perfis) atribuídos ao usuário.
 */
public record RecoveryUserDto(
        Long id,
        String email,
        List<String> roles
) {}