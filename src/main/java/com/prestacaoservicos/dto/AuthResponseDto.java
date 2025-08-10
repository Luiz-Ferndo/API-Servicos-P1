package com.prestacaoservicos.dto;

import java.util.List;

public record AuthResponseDto(
        String token,
        String tokenType,
        long expiresIn,
        UserInfo user
) {

    public static record UserInfo(
            Long id,
            String email,
            List<String> roles
    ) {}
}
