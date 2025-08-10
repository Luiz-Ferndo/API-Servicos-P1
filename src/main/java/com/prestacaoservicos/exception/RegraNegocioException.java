package com.prestacaoservicos.exception;

/**
 * Exceção personalizada para regras de negócio.
 * Extende RuntimeException para indicar que é uma exceção não verificada.
 */
public class RegraNegocioException extends RuntimeException {
    /**
     * Construtor que recebe uma mensagem de erro.
     *
     * @param mensagem A mensagem de erro a ser exibida.
     */
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}