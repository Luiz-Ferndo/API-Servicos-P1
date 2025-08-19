package com.prestacaoservicos.dto;

import java.util.List;

/**
 * DTO de resposta para autenticação, retornado após login bem-sucedido.
 * <p>
 * Contém o token JWT gerado, tipo do token, tempo de expiração em segundos
 * e informações básicas do usuário autenticado.
 *
 * @param token      Token JWT para uso nas requisições autenticadas.
 * @param tokenType  Tipo do token (exemplo: "Bearer").
 * @param expiresIn  Tempo de expiração do token em segundos.
 * @param user       Informações do usuário autenticado.
 */
public record AuthResponseDto(
        String token,
        String tokenType,
        long expiresIn,
        UserInfo user
) {

    /**
     * Informações básicas do usuário incluídas na resposta de autenticação.
     *
     * @param id    Identificador único do usuário.
     * @param email Email do usuário.
     * @param roles Lista de roles (perfis) atribuídos ao usuário.
     * @param permissions Lista de permissões concedidas ao usuário.
     */
    public static record UserInfo(
            Long id,
            String name,
            String email,
            List<String> roles,
            List<String> permissions
    ) {}
}