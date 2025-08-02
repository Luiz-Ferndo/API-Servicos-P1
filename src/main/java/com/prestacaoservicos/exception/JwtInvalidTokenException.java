package com.prestacaoservicos.exception;

/**
 * Exceção lançada quando um token JWT fornecido é considerado inválido,
 * seja por expiração, assinatura incorreta ou formato inválido.
 */
public class JwtInvalidTokenException extends RuntimeException {
    public JwtInvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtInvalidTokenException(String message) {
        super(message);
    }
}