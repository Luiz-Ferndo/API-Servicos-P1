package com.prestacaoservicos.controller;

import com.prestacaoservicos.entity.Agendamento;
import com.prestacaoservicos.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Agendamentos", description = "Operações relacionadas aos agendamentos de serviços")
@RestController
@RequestMapping("/api/v1/agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @Operation(summary = "Agendar novo serviço", description = "Cria um novo agendamento entre cliente e prestador para uma data e hora específicas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento criado com sucesso",
                    content = @Content(schema = @Schema(implementation = Agendamento.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cliente ou prestador não encontrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Agendamento> agendar(
            @Parameter(description = "ID do cliente") @RequestParam Long clienteId,
            @Parameter(description = "ID do prestador de serviço") @RequestParam Long prestadorId,
            @Parameter(description = "Data e hora do agendamento no formato ISO (ex: 2025-08-03T14:30)") @RequestParam String dataHora) {

        Agendamento ag = service.agendar(clienteId, prestadorId, LocalDateTime.parse(dataHora));
        return ResponseEntity.ok(ag);
    }

    @Operation(summary = "Cancelar agendamento", description = "Cancela um agendamento existente fornecendo o motivo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content)
    })
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(
            @Parameter(description = "ID do agendamento a ser cancelado") @PathVariable Long id,
            @Parameter(description = "Motivo do cancelamento") @RequestParam String motivo) {

        service.cancelar(id, motivo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Listar agendamentos por cliente", description = "Retorna todos os agendamentos associados a um cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Agendamento.class)))
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Agendamento>> listarCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId) {

        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    @Operation(summary = "Listar agendamentos por prestador", description = "Retorna todos os agendamentos associados a um prestador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Agendamento.class)))
    })
    @GetMapping("/prestador/{prestadorId}")
    public ResponseEntity<List<Agendamento>> listarPrestador(
            @Parameter(description = "ID do prestador") @PathVariable Long prestadorId) {

        return ResponseEntity.ok(service.listarPorPrestador(prestadorId));
    }
}