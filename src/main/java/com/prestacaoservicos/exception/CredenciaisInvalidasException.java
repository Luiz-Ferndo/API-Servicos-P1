package com.prestacaoservicos.exception;

/**
 * Exceção personalizada para indicar que as credenciais fornecidas são inválidas.
 * Extende RuntimeException para indicar que é uma exceção não verificada.
 */
public class CredenciaisInvalidasException extends RuntimeException {
    /**
     * Construtor que recebe uma mensagem de erro.
     *
     * @param mensagem A mensagem de erro a ser exibida.
     */
    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}