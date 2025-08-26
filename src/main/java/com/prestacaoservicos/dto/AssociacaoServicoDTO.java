package com.prestacaoservicos.dto;

import jakarta.validation.constraints.NotNull;

public record AssociacaoServicoDTO(
        @NotNull
        Long servicoId
) {}