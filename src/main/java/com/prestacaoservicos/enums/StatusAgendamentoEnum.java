package com.prestacaoservicos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.prestacaoservicos.exception.EnumInvalidoException;

import java.util.Arrays;

public enum StatusAgendamentoEnum {

    AGENDADO(1, "Agendado"),
    CONFIRMADO(2, "Confirmado"),
    CANCELADO(3, "Cancelado"),
    FINALIZADO(4, "Finalizado"),
    NAO_COMPARECEU(5, "Não Compareceu");

    private final Integer codigo;
    private final String descricao;

    StatusAgendamentoEnum(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    public static StatusAgendamentoEnum fromCodigo(Integer codigo) {
        if (codigo == null) {
            throw new EnumInvalidoException("Código de status não pode ser nulo.");
        }

        return Arrays.stream(values())
                .filter(status -> status.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new EnumInvalidoException("Código de status inválido: " + codigo));
    }

    @JsonCreator
    public static StatusAgendamentoEnum fromDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new EnumInvalidoException("Descrição de status não pode ser vazia.");
        }

        return Arrays.stream(values())
                .filter(status -> status.getDescricao().equalsIgnoreCase(descricao.trim()))
                .findFirst()
                .orElseThrow(() -> new EnumInvalidoException("Descrição de status inválida: " + descricao));
    }
}