package com.prestacaoservicos.exception;

public class PermissaoNaoEncontradaException extends RuntimeException {
    public PermissaoNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}