package com.prestacaoservicos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.prestacaoservicos.exception.EnumInvalidoException;

import java.util.Arrays;

/**
 * Enumeração que representa os possíveis status de um agendamento.
 * <p>
 * Cada status possui um código numérico único e uma descrição textual.
 */
public enum StatusAgendamentoEnum {

    /** Status: Agendado (código 1). */
    AGENDADO(1, "Agendado"),

    /** Status: Confirmado (código 2). */
    CONFIRMADO(2, "Confirmado"),

    /** Status: Cancelado (código 3). */
    CANCELADO(3, "Cancelado"),

    /** Status: Finalizado (código 4). */
    FINALIZADO(4, "Finalizado"),

    /** Status: Não Compareceu (código 5). */
    NAO_COMPARECEU(5, "Não Compareceu");

    /** Código numérico do status. */
    private final Integer codigo;

    /** Descrição textual do status. */
    private final String descricao;

    /**
     * Construtor da enum StatusAgendamentoEnum.
     *
     * @param codigo    Código numérico do status.
     * @param descricao Descrição textual do status.
     */
    StatusAgendamentoEnum(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    /**
     * Obtém o código numérico do status.
     *
     * @return Código do status.
     */
    public Integer getCodigo() {
        return codigo;
    }

    /**
     * Obtém a descrição textual do status.
     * <p>
     * Esta anotação {@link JsonValue} indica que este valor será usado
     * ao serializar o enum para JSON.
     *
     * @return Descrição do status.
     */
    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    /**
     * Converte um código numérico para o enum correspondente.
     * <p>
     * Lança {@link EnumInvalidoException} se o código for nulo ou inválido.
     *
     * @param codigo Código do status.
     * @return Enum correspondente ao código.
     * @throws EnumInvalidoException Se o código for nulo ou inválido.
     */
    public static StatusAgendamentoEnum fromCodigo(Integer codigo) {
        if (codigo == null) {
            throw new EnumInvalidoException(
                    "Código do StatusAgendamentoEnum não pode ser nulo."
            );
        }

        return Arrays.stream(values())
                .filter(status -> status.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new EnumInvalidoException(
                        "Código inválido para StatusAgendamentoEnum: " + codigo
                ));
    }

    /**
     * Converte uma descrição textual para o enum correspondente.
     * <p>
     * Lança {@link EnumInvalidoException} se a descrição for nula, vazia ou inválida.
     * Esta anotação {@link JsonCreator} permite a desserialização correta a partir de JSON.
     *
     * @param descricao Descrição do status.
     * @return Enum correspondente à descrição.
     * @throws EnumInvalidoException Se a descrição for nula, vazia ou inválida.
     */
    @JsonCreator
    public static StatusAgendamentoEnum fromDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new EnumInvalidoException(
                    "Descrição do StatusAgendamentoEnum não pode ser nula ou vazia."
            );
        }

        return Arrays.stream(values())
                .filter(status -> status.getDescricao().equalsIgnoreCase(descricao.trim()))
                .findFirst()
                .orElseThrow(() -> new EnumInvalidoException(
                        "Descrição inválida para StatusAgendamentoEnum: '" + descricao + "'"
                ));
    }
}