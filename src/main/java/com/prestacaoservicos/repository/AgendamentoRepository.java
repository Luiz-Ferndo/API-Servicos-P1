package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.Agendamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Repositório para operações CRUD relacionadas à entidade Agendamento.
 * Extende JpaRepository para fornecer métodos padrão de acesso a dados.
 */
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
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

    /**
     * Encontra agendamentos por ID do cliente e paginação.
     *
     * @param clienteId ID do cliente
     * @param pageable  Objeto de paginação
     * @return Página de agendamentos correspondentes
     */
    Page<Agendamento> findByCliente_Id(Long clienteId, Pageable pageable);

    /**
     * Encontra agendamentos por ID do prestador.
     *
     * @param prestadorId ID do prestador
     * @return Lista de agendamentos correspondentes
     */
    Page<Agendamento> findByPrestador_Id(Long prestadorId, Pageable pageable);

    /**
     * Verifica se já existe um agendamento para o prestador em uma data e hora específicas.
     *
     * @param prestadorId ID do prestador
     * @param dataHora    Data e hora do agendamento
     * @return true se existir, false caso contrário
     */
    boolean existsByPrestadorIdAndDataHora(Long prestadorId, LocalDateTime dataHora);

    /**
     * Encontra agendamentos por ID do prestador e intervalo de data/hora.
     *
     * @param prestadorId ID do prestador
     * @param inicio      Data/hora de início do intervalo
     * @param fim         Data/hora de fim do intervalo
     * @return Lista de agendamentos correspondentes
     */
    List<Agendamento> findByPrestadorIdAndDataHoraBetween(Long prestadorId, LocalDateTime inicio, LocalDateTime fim);
}