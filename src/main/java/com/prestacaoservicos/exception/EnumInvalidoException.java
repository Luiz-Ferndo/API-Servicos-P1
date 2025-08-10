package com.prestacaoservicos.exception;

import java.util.List;

public class EnumInvalidoException extends RuntimeException {
    private String campo;
    private Object valorRecebido;
    private List<String> valoresValidos;

    public EnumInvalidoException(String mensagem) {
        super(mensagem);
    }

    public EnumInvalidoException(String campo, Object valorRecebido, List<String> valoresValidos) {
        super(String.format(
                "Valor inválido '%s' para o campo '%s'. Valores válidos: %s",
                valorRecebido, campo, valoresValidos
        ));
        this.campo = campo;
        this.valorRecebido = valorRecebido;
        this.valoresValidos = valoresValidos;
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