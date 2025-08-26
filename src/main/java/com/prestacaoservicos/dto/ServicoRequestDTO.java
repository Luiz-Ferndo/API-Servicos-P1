package com.prestacaoservicos.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;

public record ServicoRequestDTO(
        @NotBlank(message = "O nome não pode ser vazio.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String nome,

        @NotNull(message = "O valor não pode ser nulo.")
        @Positive(message = "O valor deve ser positivo.")
        BigDecimal valor,

        @NotBlank(message = "A descrição não pode ser vazia.")
        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
        String descricao
) {}