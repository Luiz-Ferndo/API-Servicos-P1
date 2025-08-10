package com.prestacaoservicos.dto;

import com.prestacaoservicos.enums.RoleNameEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para criação de um novo usuário.
 * <p>
 * Contém os dados necessários para cadastrar um usuário no sistema,
 * incluindo validações para garantir a integridade dos dados.
 *
 * @param email    Endereço de email do usuário. Deve ser um email válido e não pode ser vazio.
 * @param password Senha do usuário. Deve conter pelo menos 6 caracteres e não pode ser vazia.
 * @param role     Papel (role) do usuário no sistema. Não pode ser nulo.
 */
public record CreateUserDto(
        @Email(message = "Email inválido")
        @NotBlank(message = "Email não pode ser vazio")
        String email,

        @NotBlank(message = "Senha não pode ser vazia")
        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        String password,

        @NotNull(message = "Role deve ser informada")
        RoleNameEnum role
) {}