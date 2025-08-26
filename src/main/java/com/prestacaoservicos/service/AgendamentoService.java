package com.prestacaoservicos.service;

import com.prestacaoservicos.entity.Agendamento;
import com.prestacaoservicos.entity.Servico;
import com.prestacaoservicos.entity.User;
import com.prestacaoservicos.enums.RoleNameEnum;
import com.prestacaoservicos.enums.StatusAgendamentoEnum;
import com.prestacaoservicos.exception.AcessoNegadoException;
import com.prestacaoservicos.exception.RecursoNaoEncontradoException;
import com.prestacaoservicos.exception.RegraNegocioException;
import com.prestacaoservicos.repository.AgendamentoRepository;
import com.prestacaoservicos.repository.ServicoRepository;
import com.prestacaoservicos.repository.UserRepository;
import com.prestacaoservicos.security.userdetails.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serviço responsável por gerenciar as regras de negócio relacionadas a {@link Agendamento}.
 * <p>
 * Permite operações de criação, cancelamento, listagem e atualização de status
 * de agendamentos entre clientes e prestadores de serviços.
 */
@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepo;
    private final UserRepository userRepo;
    private final ServicoRepository servicoRepo;
    private final PagamentoService pagamentoService;

    /**
     * Construtor que injeta os repositórios e serviços necessários.
     *
     * @param agendamentoRepo   Repositório de agendamentos
     * @param userRepo          Repositório de usuários
     * @param servicoRepo       Repositório de serviços
     * @param pagamentoService  Serviço responsável por operações de pagamento
     */
    public AgendamentoService(
            AgendamentoRepository agendamentoRepo,
            UserRepository userRepo,
            ServicoRepository servicoRepo,
            PagamentoService pagamentoService) {
        this.agendamentoRepo = agendamentoRepo;
        this.userRepo = userRepo;
        this.servicoRepo = servicoRepo;
        this.pagamentoService = pagamentoService;
    }

    /**
     * Cria um novo agendamento entre cliente e prestador para um serviço.
     *
     * <p><b>Regras de negócio:</b></p>
     * <ul>
     *   <li>Deve ser feito com pelo menos 24h de antecedência.</li>
     *   <li>Prestador não pode ter outro agendamento no mesmo horário.</li>
     *   <li>O cliente deve ter o papel {@code ROLE_CUSTOMER}.</li>
     *   <li>O prestador deve ter o papel {@code ROLE_SERVICE_PROVIDER}.</li>
     *   <li>O prestador deve oferecer o serviço solicitado.</li>
     * </ul>
     *
     * @param clienteId   ID do cliente que solicita o agendamento
     * @param prestadorId ID do prestador de serviços
     * @param servicoId   ID do serviço a ser prestado
     * @param dataHora    Data e hora desejadas
     * @return O agendamento criado
     * @throws RecursoNaoEncontradoException se cliente, prestador ou serviço não forem encontrados
     * @throws RegraNegocioException se alguma regra de negócio for violada
     */
    @Transactional
    public Agendamento agendar(Long clienteId, Long prestadorId, Long servicoId, LocalDateTime dataHora) {
        if (dataHora.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new RegraNegocioException("Agendamentos devem ser feitos com 24h de antecedência.");
        }

        if (agendamentoRepo.existsByPrestadorIdAndDataHora(prestadorId, dataHora)) {
            throw new RegraNegocioException("Horário indisponível para este prestador.");
        }

        User cliente = userRepo.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado. ID: " + clienteId));

        User prestador = userRepo.findById(prestadorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Prestador não encontrado. ID: " + prestadorId));

        Servico servico = servicoRepo.findById(servicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado. ID: " + servicoId));

        validarPapeis(cliente, prestador);

        if (!userRepo.prestadorOfereceServico(prestador.getId(), servico.getId())) {
            throw new RegraNegocioException("O prestador selecionado não oferece este serviço.");
        }

        Agendamento ag = new Agendamento();
        ag.setCliente(cliente);
        ag.setPrestador(prestador);
        ag.setServico(servico);
        ag.setDataHora(dataHora);
        ag.setStatus(StatusAgendamentoEnum.AGENDADO);
        ag.setValor(servico.getValor());

        return agendamentoRepo.save(ag);
    }

    /**
     * Cancela um agendamento existente.
     *
     * <p><b>Regra de negócio:</b> O cancelamento só pode ser feito até 12 horas antes do horário agendado.</p>
     *
     * @param id     ID do agendamento
     * @param motivo Motivo informado pelo usuário para o cancelamento
     * @throws RecursoNaoEncontradoException se o agendamento não for encontrado
     * @throws RegraNegocioException se o cancelamento ocorrer fora do prazo permitido
     */
    @Transactional
    public void cancelar(Long id, String motivo) {
        Agendamento ag = agendamentoRepo.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado."));

        if (ag.getDataHora().isBefore(LocalDateTime.now().plusHours(12))) {
            throw new RegraNegocioException("Cancelamento só permitido até 12h antes do horário agendado.");
        }

        ag.setStatus(StatusAgendamentoEnum.CANCELADO);
        ag.setMotivoCancelamento(motivo);

        agendamentoRepo.save(ag);
        pagamentoService.reembolsar(id);
    }

    /**
     * Lista todos os agendamentos de um cliente.
     *
     * @param clienteId ID do cliente
     * @return Lista de agendamentos do cliente
     */
    public List<Agendamento> listarPorCliente(Long clienteId) {
        return agendamentoRepo.findByClienteId(clienteId);
    }

    /**
     * Lista todos os agendamentos de forma paginada.
     *
     * @param pageable Objeto de paginação
     * @return Página de agendamentos
     */
    public Page<Agendamento> listarTodosPaginado(Pageable pageable) {
        return agendamentoRepo.findAll(pageable);
    }

    /**
     * Lista todos os agendamentos de um prestador.
     *
     * @param prestadorId ID do prestador
     * @return Lista de agendamentos do prestador
     */
    public List<Agendamento> listarPorPrestador(Long prestadorId) {
        return agendamentoRepo.findByPrestadorId(prestadorId);
    }

    /**
     * Lista os agendamentos do usuário logado, de acordo com seu papel (cliente ou prestador).
     *
     * @param userDetails Detalhes do usuário autenticado
     * @param pageable    Objeto de paginação
     * @return Página de agendamentos do usuário
     */
    public Page<Agendamento> listarAgendamentosDoUsuario(UserDetailsImpl userDetails, Pageable pageable) {
        Long usuarioId = userDetails.getId();

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (roles.contains(RoleNameEnum.ROLE_SERVICE_PROVIDER.name())) {
            return agendamentoRepo.findByPrestador_Id(usuarioId, pageable);
        } else if (roles.contains(RoleNameEnum.ROLE_CUSTOMER.name())) {
            return agendamentoRepo.findByCliente_Id(usuarioId, pageable);
        }

        return Page.empty(pageable);
    }

    /**
     * Valida os papéis (roles) do cliente e do prestador em um agendamento.
     *
     * @param cliente   Usuário cliente
     * @param prestador Usuário prestador
     * @throws RegraNegocioException se o cliente ou prestador não tiverem os papéis esperados
     */
    private void validarPapeis(User cliente, User prestador) {
        boolean clienteValido = cliente.getRoles().stream()
                .anyMatch(role -> role.getName().equals(RoleNameEnum.ROLE_CUSTOMER));
        if (!clienteValido) {
            throw new RegraNegocioException("Usuário não possui permissão de cliente.");
        }

        boolean prestadorValido = prestador.getRoles().stream()
                .anyMatch(role -> role.getName().equals(RoleNameEnum.ROLE_SERVICE_PROVIDER));
        if (!prestadorValido) {
            throw new RegraNegocioException("Usuário não possui permissão de prestador.");
        }
    }

    /**
     * Atualiza o status de um agendamento existente, validando permissões e regras.
     *
     * <p><b>Regras de negócio:</b></p>
     * <ul>
     *   <li>Apenas cliente, prestador ou administrador podem alterar o agendamento.</li>
     *   <li>Para cancelamento, é obrigatório informar o motivo.</li>
     *   <li>Clientes só podem cancelar até 12 horas antes do agendamento.</li>
     * </ul>
     *
     * @param id            ID do agendamento
     * @param status        Novo status do agendamento
     * @param motivo        Motivo do cancelamento (quando aplicável)
     * @param usuarioLogado Usuário autenticado que solicita a alteração
     * @return O agendamento atualizado
     * @throws RecursoNaoEncontradoException se o agendamento não for encontrado
     * @throws AcessoNegadoException se o usuário não tiver permissão
     * @throws RegraNegocioException se alguma regra de negócio for violada
     */
    @Transactional
    public Agendamento atualizarStatus(Long id, StatusAgendamentoEnum status, String motivo, UserDetailsImpl usuarioLogado) {
        Agendamento agendamento = agendamentoRepo.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado."));

        Long usuarioLogadoId = usuarioLogado.getId();
        Set<String> roles = usuarioLogado.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        boolean isOwner = agendamento.getCliente().getId().equals(usuarioLogadoId) ||
                agendamento.getPrestador().getId().equals(usuarioLogadoId);

        boolean isAdminOrProvider = roles.contains(RoleNameEnum.ROLE_ADMINISTRATOR.name()) ||
                roles.contains(RoleNameEnum.ROLE_SERVICE_PROVIDER.name());

        if (!isOwner && !isAdminOrProvider) {
            throw new AcessoNegadoException("Você não tem permissão para modificar este agendamento.");
        }

        if (status == StatusAgendamentoEnum.CANCELADO) {
            if (motivo == null || motivo.isBlank()) {
                throw new RegraNegocioException("O motivo é obrigatório para o cancelamento.");
            }
            if (roles.contains(RoleNameEnum.ROLE_CUSTOMER.name()) &&
                    agendamento.getDataHora().isBefore(LocalDateTime.now().plusHours(12))) {
                throw new RegraNegocioException("Cancelamento só permitido até 12h antes do horário agendado.");
            }
            agendamento.setMotivoCancelamento(motivo);
            pagamentoService.reembolsar(id);
        }

        agendamento.setStatus(status);
        return agendamentoRepo.save(agendamento);
    }
}
