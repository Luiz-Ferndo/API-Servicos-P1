package com.prestacaoservicos.enums;

import com.prestacaoservicos.entity.Permission;

/**
 * Enumeração que define as permissões disponíveis no sistema.
 * <p>
 * Cada permissão representa uma ação específica que pode ser atribuída a usuários
 * para controle de acesso granular.
 */
public enum PermissionEnum {

    /** Permissão para gerenciar usuários do sistema. */
    MANAGE_USERS("Gerenciar usuários"),

    /** Permissão para gerenciar serviços oferecidos. */
    MANAGE_SERVICES("Gerenciar serviços"),

    /** Permissão para visualizar relatórios gerenciais. */
    VIEW_REPORTS("Visualizar relatórios"),

    /** Permissão para agendar um serviço. */
    BOOK_SERVICE("Agendar serviço"),

    /** Permissão para consultar agendamentos existentes. */
    VIEW_APPOINTMENTS("Consultar agendamentos"),

    /** Permissão para cancelar um agendamento. */
    CANCEL_APPOINTMENT("Cancelar agendamento"),

    /** Permissão para confirmar a execução de um serviço. */
    CONFIRM_EXECUTION("Confirmar execução do serviço"),

    /** Permissão para definir a disponibilidade de recursos ou serviços. */
    DEFINE_AVAILABILITY("Definir disponibilidade"),

    /** Permissão para efetuar pagamentos. */
    MAKE_PAYMENT("Efetuar pagamento"),

    /** Permissão para consultar os serviços disponíveis. */
    VIEW_SERVICES("Consultar serviços");

    /** Descrição textual da permissão, usada para exibição ou logs. */
    private final String description;

    /**
     * Construtor da enum PermissionEnum.
     *
     * @param description Descrição textual da permissão.
     */
    PermissionEnum(String description) {
        this.description = description;
    }

    /**
     * Obtém o nome da permissão, que corresponde ao nome da constante enum.
     *
     * @return Nome da permissão.
     */
    public String getName() {
        return this.name();
    }

    /**
     * Obtém a descrição textual da permissão.
     *
     * @return Descrição da permissão.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Converte esta enumeração para uma entidade {@link Permission}.
     * <p>
     * Cria um novo objeto Permission com nome e descrição correspondentes.
     *
     * @return Objeto {@link Permission} correspondente a esta enumeração.
     */
    public Permission toPermission() {
        return new Permission(getName(), getDescription());
    }
}