package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações CRUD relacionadas à entidade Agendamento.
 * Extende JpaRepository para fornecer métodos padrão de acesso a dados.
 */
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    /**
     * Encontra agendamentos por ID do prestador e data/hora.
     *
     * @param prestadorId ID do prestador
     * @param dataHora Data e hora do agendamento
     * @return Lista de agendamentos correspondentes
     */
    List<Agendamento> findByPrestadorIdAndDataHora(Long prestadorId, LocalDateTime dataHora);

    /**
     * Encontra agendamentos por ID do cliente e data/hora.
     *
     * @param clienteId ID do cliente
     * @return Lista de agendamentos correspondentes
     */
    List<Agendamento> findByClienteId(Long clienteId);

    /**
     * Encontra agendamentos por ID do prestador.
     *
     * @param prestadorId ID do prestador
     * @return Lista de agendamentos correspondentes
     */
    List<Agendamento> findByPrestadorId(Long prestadorId);
}