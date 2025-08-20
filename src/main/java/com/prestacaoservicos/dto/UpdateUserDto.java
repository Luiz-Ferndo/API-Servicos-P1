package com.prestacaoservicos.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO para atualização de dados do usuário.
 * <p>
 * Contém os campos que podem ser atualizados para um usuário existente,
 * com validações para garantir a integridade dos dados.
 * @param name  Nome do usuário. Pode conter até 100 caracteres.
 * @param email Endereço de email do usuário. Deve ser um email válido e não pode ser vazio.
 * @param password Senha do usuário. Deve conter pelo menos 6 caracteres e não pode ser vazia.
 * @param phones Lista de telefones associados ao usuário. Pode ser nula, mas se fornecida, deve conter objetos válidos.
 */
public record UpdateUserDto(
        @Nullable
        @Size(max = 100, message = "O nome pode ter no máximo 100 caracteres")
        String name,

        @Nullable
        @Email(message = "Email inválido")
        String email,

        @Nullable
        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        String password,

        @Nullable
        List<PhoneDto> phones
) {}