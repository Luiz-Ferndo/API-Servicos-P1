package com.prestacaoservicos.dto;

import com.prestacaoservicos.enums.PhoneTypeEnum;

/**
 * DTO para representar um telefone associado a um usuário.
 * <p>
 * @param phone Número de telefone do usuário. Deve ser uma string representando o número.
 * @param type Tipo do telefone, representado por um enum {@link PhoneTypeEnum}.
 */
public record PhoneDto(
        String phone,
        PhoneTypeEnum type
) {}
