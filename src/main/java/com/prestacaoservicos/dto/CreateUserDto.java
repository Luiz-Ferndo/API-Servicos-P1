package com.prestacaoservicos.dto;

import com.prestacaoservicos.enums.RoleNameEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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