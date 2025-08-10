package com.prestacaoservicos.exception;

/**
 * Exceção lançada quando ocorre um erro interno durante a geração de um token JWT.
 * Geralmente indica um problema de configuração ou da biblioteca JWT.
 */
public class JwtGenerationException extends RuntimeException {
    /**
     * Construtor que recebe uma mensagem de erro e uma causa.
     *
     * @param message A mensagem de erro a ser exibida.
     * @param cause   A causa da exceção, geralmente outra exceção que causou esta.
     */
    public JwtGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}