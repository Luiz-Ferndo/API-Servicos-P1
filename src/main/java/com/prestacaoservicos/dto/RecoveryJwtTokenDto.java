package com.prestacaoservicos.dto;

/**
 * DTO para resposta contendo um token JWT.
 * <p>
 * Utilizado para retornar tokens JWT em respostas da API.
 *
 * @param token Token JWT gerado para autenticação e autorização.
 */
public record RecoveryJwtTokenDto(
        String token
) {}