package com.prestacaoservicos.dto;

import com.prestacaoservicos.enums.StatusAgendamentoEnum;
import jakarta.validation.constraints.NotBlank;

public record AtualizacaoStatusDTO(
        @NotBlank(message = "O status não pode ser vazio.")
        StatusAgendamentoEnum status,

        String motivo
) {}