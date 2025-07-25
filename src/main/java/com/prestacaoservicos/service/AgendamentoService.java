package com.prestacaoservicos.service;

import com.prestacaoservicos.entity.Agendamento;
import com.prestacaoservicos.entity.Cliente;
import com.prestacaoservicos.entity.Prestador;
import com.prestacaoservicos.repository.AgendamentoRepository;
import com.prestacaoservicos.repository.ClienteRepository;
import com.prestacaoservicos.repository.PrestadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepo;
    private final ClienteRepository clienteRepo;
    private final PrestadorRepository prestadorRepo;
    private final PagamentoService pagamentoService;

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

    public Agendamento agendar(Long clienteId, Long prestadorId, LocalDateTime dataHora) {
        if (dataHora.isBefore(LocalDateTime.now().plusHours(24)))
            throw new RuntimeException("Agendamentos devem ser feitos com 24h de antecedência.");

        boolean ocupado = !agendamentoRepo.findByPrestadorIdAndDataHora(prestadorId, dataHora).isEmpty();
        if (ocupado) throw new RuntimeException("Horário indisponível.");

        Cliente cliente = clienteRepo.findById(clienteId).orElseThrow();
        Prestador prestador = prestadorRepo.findById(prestadorId).orElseThrow();

        Agendamento ag = new Agendamento();
        ag.setCliente(cliente);
        ag.setPrestador(prestador);
        ag.setDataHora(dataHora);
        ag.setStatus(null);
        ag.setValor(prestador.getPreco());

        return agendamentoRepo.save(ag);
    }

    public void cancelar(Long id, String motivo) {
        Agendamento ag = agendamentoRepo.findById(id).orElseThrow();

        if (ag.getDataHora().isBefore(LocalDateTime.now().plusHours(12)))
            throw new RuntimeException("Cancelamento só permitido até 12h antes.");

        ag.setStatus(null);
        ag.setMotivoCancelamento(motivo);

        agendamentoRepo.save(ag);
        pagamentoService.reembolsar(id);
    }

    public List<Agendamento> listarPorCliente(Long clienteId) {
        return agendamentoRepo.findByClienteId(clienteId);
    }

    public List<Agendamento> listarPorPrestador(Long prestadorId) {
        return agendamentoRepo.findByPrestadorId(prestadorId);
    }
}
