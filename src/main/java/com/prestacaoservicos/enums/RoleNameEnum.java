package com.prestacaoservicos.enums;

public enum RoleNameEnum {
    ROLE_CUSTOMER,
    ROLE_ADMINISTRATOR,
    ROLE_SERVICE_PROVIDER;

    public String toUpperCase() {
        return this.name().toUpperCase();
    }
}
