package com.prestacaoservicos.dto;

import com.prestacaoservicos.entity.Role;

import java.util.List;

public record RecoveryUserDto(
        Long id,
        String email,
        List<Role> roles
) {}