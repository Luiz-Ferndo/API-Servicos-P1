package com.prestacaoservicos.dto;

import java.time.LocalDateTime;

public record AgendamentoRequestDTO(
        Long prestadorId,
        Long servicoId,
        LocalDateTime dataHora
) {}