package com.prestacaoservicos.exception;

/**
 * Exceção lançada quando ocorre um erro interno durante a geração de um token JWT.
 * Geralmente indica um problema de configuração ou da biblioteca JWT.
 */
public class JwtGenerationException extends RuntimeException {
    public JwtGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}