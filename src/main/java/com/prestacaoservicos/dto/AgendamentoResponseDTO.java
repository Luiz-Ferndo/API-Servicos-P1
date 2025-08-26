package com.prestacaoservicos.dto;

import com.prestacaoservicos.entity.Agendamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AgendamentoResponseDTO(
        Long id,
        Long clienteId,
        String clienteNome,
        Long prestadorId,
        String prestadorNome,
        Long servicoId,
        String servicoNome,
        LocalDateTime dataHora,
        BigDecimal valor,
        String status,
        String motivo
) {
    /**
     * Construtor para criar uma instância de AgendamentoResponseDTO.
     *
     * @param agendamento A entidade a ser convertida.
     * @return Uma nova instância de AgendamentoResponseDTO.
     */
    public static AgendamentoResponseDTO fromEntity(Agendamento agendamento) {
        return new AgendamentoResponseDTO(
                agendamento.getId(),
                agendamento.getCliente().getId(),
                agendamento.getCliente().getName(),
                agendamento.getPrestador().getId(),
                agendamento.getPrestador().getName(),
                agendamento.getServico().getId(),
                agendamento.getServico().getNome(),
                agendamento.getDataHora(),
                agendamento.getValor(),
                agendamento.getStatus().name(),
                agendamento.getMotivoCancelamento()
        );
    }
}