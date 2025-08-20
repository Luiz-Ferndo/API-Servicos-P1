package com.prestacaoservicos.service;

import com.prestacaoservicos.entity.Agendamento;
import com.prestacaoservicos.entity.Cliente;
import com.prestacaoservicos.entity.Prestador;
import com.prestacaoservicos.exception.RecursoNaoEncontradoException;
import com.prestacaoservicos.exception.RegraNegocioException;
import com.prestacaoservicos.repository.AgendamentoRepository;
import com.prestacaoservicos.repository.ClienteRepository;
import com.prestacaoservicos.repository.PrestadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço responsável por gerenciar a lógica de negócio para Agendamentos.
 * <p>
 * Inclui operações como criar, cancelar e listar agendamentos, aplicando
 * as regras de negócio necessárias.
 *
 * @version 1.0
 * @since 02/08/2025
 */
@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepo;
    private final ClienteRepository clienteRepo;
    private final PrestadorRepository prestadorRepo;
    private final PagamentoService pagamentoService;

    /**
     * Construtor para injeção de dependências via Spring.
     *
     * @param agendamentoRepo Repositório para operações com a entidade Agendamento.
     * @param clienteRepo     Repositório para operações com a entidade Cliente.
     * @param prestadorRepo   Repositório para operações com a entidade Prestador.
     * @param pagamentoService Serviço para processar operações de pagamento, como reembolsos.
     */
    public AgendamentoService(
            AgendamentoRepository agendamentoRepo,
            ClienteRepository clienteRepo,
            PrestadorRepository prestadorRepo,
            PagamentoService pagamentoService) {
        this.agendamentoRepo = agendamentoRepo;
        this.clienteRepo = clienteRepo;
        this.prestadorRepo = prestadorRepo;
        this.pagamentoService = pagamentoService;
    }

    /**
     * Cria um novo agendamento para um cliente com um prestador específico.
     * <p>
     * Regras de negócio aplicadas:
     * <ul>
     * <li>O agendamento deve ser feito com no mínimo 24 horas de antecedência.</li>
     * <li>O prestador não pode ter outro agendamento no mesmo horário.</li>
     * </ul>
     *
     * @param clienteId  O ID do cliente que está realizando o agendamento.
     * @param prestadorId O ID do prestador de serviço.
     * @param dataHora   A data e hora desejadas para o serviço.
     * @return O objeto {@link Agendamento} que foi criado e salvo.
     * @throws RegraNegocioException      Se alguma regra de negócio for violada.
     * @throws RecursoNaoEncontradoException Se o cliente ou o prestador não forem encontrados.
     */
    @Transactional
    public Agendamento agendar(Long clienteId, Long prestadorId, LocalDateTime dataHora) {
        if (dataHora.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new RegraNegocioException("Agendamentos devem ser feitos com 24h de antecedência.");
        }

        boolean ocupado = !agendamentoRepo.findByPrestadorIdAndDataHora(prestadorId, dataHora).isEmpty();

        if (ocupado) {
            throw new RegraNegocioException("Horário indisponível.");
        }

        Cliente cliente = clienteRepo.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));

        Prestador prestador = prestadorRepo.findById(prestadorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Prestador não encontrado."));

        Agendamento ag = new Agendamento();
        ag.setCliente(cliente);
        ag.setPrestador(prestador);
        ag.setDataHora(dataHora);
        ag.setStatus(null); // O status pode ser definido posteriormente, ex: "CONFIRMADO"
        // ag.setValor(prestador.getPreco());

        return agendamentoRepo.save(ag);
    }

    /**
     * Cancela um agendamento existente.
     * <p>
     * Regra de negócio: O cancelamento só é permitido com no mínimo 12 horas de antecedência.
     * Após o cancelamento, um reembolso é processado.
     *
     * @param id O ID do agendamento a ser cancelado.
     * @param motivo O motivo pelo qual o agendamento está sendo cancelado.
     * @throws RecursoNaoEncontradoException Se o agendamento não for encontrado.
     * @throws RegraNegocioException Se o cancelamento for tentado fora da janela permitida.
     */
    @Transactional
    public void cancelar(Long id, String motivo) {
        Agendamento ag = agendamentoRepo.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado."));

        if (ag.getDataHora().isBefore(LocalDateTime.now().plusHours(12))) {
            throw new RegraNegocioException("Cancelamento só permitido até 12h antes.");
        }

        ag.setStatus(null); // O status pode ser alterado para "CANCELADO"
        ag.setMotivoCancelamento(motivo);

        agendamentoRepo.save(ag);
        pagamentoService.reembolsar(id);
    }

    /**
     * Lista todos os agendamentos associados a um cliente específico.
     *
     * @param clienteId O ID do cliente.
     * @return Uma lista de {@link Agendamento} para o cliente especificado.
     */
    public List<Agendamento> listarPorCliente(Long clienteId) {
        return agendamentoRepo.findByClienteId(clienteId);
    }

    /**
     * Lista todos os agendamentos associados a um prestador de serviço específico.
     *
     * @param prestadorId O ID do prestador.
     * @return Uma lista de {@link Agendamento} para o prestador especificado.
     */
    public List<Agendamento> listarPorPrestador(Long prestadorId) {
        return agendamentoRepo.findByPrestadorId(prestadorId);
    }
}