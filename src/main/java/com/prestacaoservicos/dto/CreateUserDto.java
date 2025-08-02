package com.prestacaoservicos.dto;

import com.prestacaoservicos.enums.RoleNameEnum;

public record CreateUserDto(
        String email,
        String password,
        RoleNameEnum role
) {}