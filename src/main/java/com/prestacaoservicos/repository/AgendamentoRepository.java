package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByPrestadorIdAndDataHora(Long prestadorId, LocalDateTime dataHora);

    List<Agendamento> findByClienteId(Long clienteId);

    List<Agendamento> findByPrestadorId(Long prestadorId);
}