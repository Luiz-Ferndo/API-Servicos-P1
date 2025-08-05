package com.prestacaoservicos.dto;

import java.util.List;

public record RecoveryUserDto(
        Long id,
        String email,
        List<String> roles
) {}