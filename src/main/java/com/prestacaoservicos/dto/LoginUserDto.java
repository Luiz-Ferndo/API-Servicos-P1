package com.prestacaoservicos.dto;

/**
 * DTO para requisição de login de usuário.
 * <p>
 * Contém os dados necessários para autenticação, como email e senha.
 *
 * @param email    Email do usuário que deseja se autenticar.
 * @param password Senha do usuário para autenticação.
 */
public record LoginUserDto(
        String email,
        String password
) {}