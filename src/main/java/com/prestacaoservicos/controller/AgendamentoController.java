package com.prestacaoservicos.controller;

import com.prestacaoservicos.entity.Agendamento;
import com.prestacaoservicos.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Agendar novo serviço")
    public ResponseEntity<Agendamento> agendar(@RequestParam Long clienteId, @RequestParam Long prestadorId, @RequestParam String dataHora) {
        Agendamento ag = service.agendar(clienteId, prestadorId, LocalDateTime.parse(dataHora));
        return ResponseEntity.ok(ag);
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar agendamento")
    public ResponseEntity<Void> cancelar(@PathVariable Long id, @RequestParam String motivo) {
        service.cancelar(id, motivo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Listar agendamentos por cliente")
    public ResponseEntity<List<Agendamento>> listarCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    @GetMapping("/prestador/{prestadorId}")
    @Operation(summary = "Listar agendamentos por prestador")
    public ResponseEntity<List<Agendamento>> listarPrestador(@PathVariable Long prestadorId) {
        return ResponseEntity.ok(service.listarPorPrestador(prestadorId));
    }
}
