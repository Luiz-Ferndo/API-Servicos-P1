package com.prestacaoservicos.dto;

import com.prestacaoservicos.enums.StatusAgendamentoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizacaoStatusDTO(
        @NotNull(message = "O status do agendamento não pode ser nulo.")
        StatusAgendamentoEnum status,

        @NotBlank(message = "O motivo da atualização do status não pode ser vazio.")
        String motivo
) {}