package com.prestacaoservicos.exception;

/**
 * Exceção lançada quando um token JWT fornecido é considerado inválido,
 * seja por expiração, assinatura incorreta ou formato inválido.
 */
public class JwtInvalidTokenException extends RuntimeException {
    /**
     * Construtor que recebe uma mensagem de erro e uma causa.
     *
     * @param message A mensagem de erro a ser exibida.
     * @param cause   A causa da exceção, geralmente outra exceção que causou esta.
     */
    public JwtInvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor que recebe apenas uma mensagem de erro.
     *
     * @param message A mensagem de erro a ser exibida.
     */
    public JwtInvalidTokenException(String message) {
        super(message);
    }
}