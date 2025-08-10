package com.prestacaoservicos.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.prestacaoservicos.exception.EnumInvalidoException;

import java.util.Arrays;

public enum RoleNameEnum {
    ROLE_CUSTOMER,
    ROLE_ADMINISTRATOR,
    ROLE_SERVICE_PROVIDER;

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