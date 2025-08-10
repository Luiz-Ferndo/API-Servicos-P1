package com.prestacaoservicos.exception;

/**
 * Exceção personalizada para indicar que uma permissão não foi encontrada.
 * Extende RuntimeException para indicar que é uma exceção não verificada.
 */
public class PermissaoNaoEncontradaException extends RuntimeException {
    /**
     * Construtor que recebe uma mensagem de erro.
     *
     * @param mensagem A mensagem de erro a ser exibida.
     */
    public PermissaoNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}