package com.prestacaoservicos.exception;

/**
 * Exceção personalizada para indicar que o acesso foi negado.
 * Extende RuntimeException para indicar que é uma exceção não verificada.
 */
public class AcessoNegadoException extends RuntimeException {
    /**
     * Construtor que recebe uma mensagem de erro.
     *
     * @param mensagem A mensagem de erro a ser exibida.
     */
    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
