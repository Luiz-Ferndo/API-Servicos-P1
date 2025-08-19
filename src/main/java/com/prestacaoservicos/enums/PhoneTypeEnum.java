package com.prestacaoservicos.enums;

/**
 * Enumeração que define os tipos de telefone aceitos no sistema.
 * <p>
 * Utilizada para padronizar os números de contato dos usuários.
 */
public enum PhoneTypeEnum {

    /** Telefone celular. */
    MOBILE("Telefone celular"),

    /** Telefone fixo (residencial ou comercial). */
    LANDLINE("Telefone fixo"),

    /** Número utilizado para contato via WhatsApp. */
    WHATSAPP("WhatsApp"),

    /** Outro tipo de telefone não especificado. */
    OTHER("Outro");

    /** Descrição textual do tipo de telefone. */
    private final String description;

    /**
     * Construtor da enum PhoneTypeEnum.
     *
     * @param description Descrição textual do tipo de telefone.
     */
    PhoneTypeEnum(String description) {
        this.description = description;
    }

    /**
     * Obtém o nome do tipo de telefone, que corresponde ao nome da constante enum.
     *
     * @return Nome do tipo de telefone.
     */
    public String getName() {
        return this.name();
    }

    /**
     * Obtém a descrição textual do tipo de telefone.
     *
     * @return Descrição do tipo de telefone.
     */
    public String getDescription() {
        return this.description;
    }
}