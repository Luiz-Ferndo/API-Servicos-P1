package com.prestacaoservicos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para atualização de dados do usuário.
 * <p>
 * Contém os campos que podem ser atualizados para um usuário existente,
 * com validações para garantir a integridade dos dados.
 *
 * @param email Endereço de email do usuário. Deve ser um email válido e não pode ser vazio.
 * @param name  Nome do usuário. Pode conter até 100 caracteres.
 */
public record UpdateUserDto(
        @Email(message = "Email inválido")
        @NotBlank(message = "Email não pode ser vazio")
        String email,

        @Size(max = 100, message = "O nome pode ter no máximo 100 caracteres")
        String name
) {}