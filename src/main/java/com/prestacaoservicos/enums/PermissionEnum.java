package com.prestacaoservicos.enums;

import com.prestacaoservicos.entity.Permission;

public enum PermissionEnum {
    MANAGE_USERS("Gerenciar usuários"),
    MANAGE_SERVICES("Gerenciar serviços"),
    VIEW_REPORTS("Visualizar relatórios"),
    BOOK_SERVICE("Agendar serviço"),
    VIEW_APPOINTMENTS("Consultar agendamentos"),
    CANCEL_APPOINTMENT("Cancelar agendamento"),
    CONFIRM_EXECUTION("Confirmar execução do serviço"),
    DEFINE_AVAILABILITY("Definir disponibilidade"),
    MAKE_PAYMENT("Efetuar pagamento"),
    VIEW_SERVICES("Consultar serviços");

    private final String description;

    PermissionEnum(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name();
    }

    public String getDescription() {
        return this.description;
    }

    public Permission toPermission() {
        return new Permission(getName(), getDescription());
    }
}
