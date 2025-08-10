package com.prestacaoservicos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.prestacaoservicos.exception.EnumInvalidoException;

import java.util.Arrays;

/**
 * Enumeração que define os nomes dos papéis (roles) disponíveis no sistema.
 * <p>
 * Utilizada para controle de acesso baseado em papéis, indicando diferentes perfis
 * de usuários como cliente, administrador e prestador de serviço.
 */
public enum RoleNameEnum {

    /** Papel do cliente do sistema. */
    ROLE_CUSTOMER,

    /** Papel do administrador do sistema. */
    ROLE_ADMINISTRATOR,

    /** Papel do prestador de serviço no sistema. */
    ROLE_SERVICE_PROVIDER;

    /**
     * Método estático para desserializar uma string em um valor válido de {@code RoleNameEnum}.
     * <p>
     * Ignora diferenças de maiúsculas/minúsculas e espaços em branco.
     * Lança {@link EnumInvalidoException} se o valor for nulo, vazio ou não corresponder
     * a nenhum papel válido.
     *
     * @param valor String com o nome da role a ser convertida.
     * @return Enum {@link RoleNameEnum} correspondente.
     * @throws EnumInvalidoException Se o valor for inválido ou nulo.
     */
    @JsonCreator
    public static RoleNameEnum fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new EnumInvalidoException("Role não pode ser nula ou vazia.");
        }

        return Arrays.stream(RoleNameEnum.values())
                .filter(r -> r.name().equalsIgnoreCase(valor.trim()))
                .findFirst()
                .orElseThrow(() -> new EnumInvalidoException("Role inválida: " + valor));
    }
}