package com.prestacaoservicos.enums;

public enum RoleNameEnum {
    ROLE_CUSTOMER,
    ROLE_ADMINISTRATOR;

    public String toUpperCase() {
        return this.name().toUpperCase();
    }
}
