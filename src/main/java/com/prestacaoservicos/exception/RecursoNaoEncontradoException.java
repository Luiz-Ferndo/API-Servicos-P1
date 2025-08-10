package com.prestacaoservicos.exception;

/**
 * Exceção personalizada para indicar que um recurso não foi encontrado.
 * Extende RuntimeException para indicar que é uma exceção não verificada.
 */
public class RecursoNaoEncontradoException extends RuntimeException {
    /**
     * Construtor que recebe uma mensagem de erro.
     *
     * @param mensagem A mensagem de erro a ser exibida.
     */
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}