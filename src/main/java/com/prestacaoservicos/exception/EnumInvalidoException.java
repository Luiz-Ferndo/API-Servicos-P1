package com.prestacaoservicos.exception;

import java.util.List;

/**
 * Exceção personalizada para indicar que um valor de enumeração é inválido.
 * Extende RuntimeException para indicar que é uma exceção não verificada.
 */
public class EnumInvalidoException extends RuntimeException {
    private String campo;
    private Object valorRecebido;
    private List<String> valoresValidos;

    /**
     * Construtor que recebe uma mensagem de erro.
     *
     * @param mensagem A mensagem de erro a ser exibida.
     */
    public EnumInvalidoException(String mensagem) {
        super(mensagem);
    }

    public String getCampo() {
        return campo;
    }

    public Object getValorRecebido() {
        return valorRecebido;
    }

    public List<String> getValoresValidos() {
        return valoresValidos;
    }
}