package com.prestacaoservicos.controller;

import com.prestacaoservicos.dto.AgendamentoRequestDTO;
import com.prestacaoservicos.dto.AgendamentoResponseDTO;
import com.prestacaoservicos.dto.AtualizacaoStatusDTO;
import com.prestacaoservicos.entity.Agendamento;
import com.prestacaoservicos.security.userdetails.UserDetailsImpl;
import com.prestacaoservicos.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador responsável pelas operações relacionadas a {@link Agendamento}.
 * <p>
 * Este controller expõe endpoints REST para criação, atualização e consulta de agendamentos.
 * </p>
 *
 * @author Você
 * @since 1.0
 */
@Tag(name = "Agendamentos", description = "Operações relacionadas aos agendamentos de serviços")
@RestController
@RequestMapping("/api/v1/agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;

    /**
     * Construtor que injeta o {@link AgendamentoService}.
     *
     * @param service serviço responsável pelas regras de negócio dos agendamentos.
     */
    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    /**
     * Cria um novo agendamento para o cliente autenticado.
     *
     * @param userDetails detalhes do usuário autenticado.
     * @param dto         dados do agendamento a ser criado.
     * @return {@link ResponseEntity} contendo o agendamento criado e o cabeçalho Location.
     */
    @Operation(summary = "Agendar novo serviço", description = "Cria um novo agendamento para o cliente autenticado.")
    @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso")
    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> agendar(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody AgendamentoRequestDTO dto) {

        Agendamento ag = service.agendar(
                userDetails.getId(),
                dto.prestadorId(),
                dto.servicoId(),
                dto.dataHora()
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(ag.getId()).toUri();

        return ResponseEntity.created(location).body(AgendamentoResponseDTO.fromEntity(ag));
    }

    /**
     * Atualiza o status de um agendamento existente.
     *
     * @param id            identificador do agendamento.
     * @param dto           dados para atualização do status.
     * @param usuarioLogado usuário autenticado que realiza a operação.
     * @return {@link ResponseEntity} contendo o agendamento atualizado.
     */
    @Operation(summary = "Atualizar status de um agendamento", description = "Modifica o status de um agendamento (ex: para CANCELADO).")
    @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso")
    @PutMapping("/{id}/status")
    public ResponseEntity<AgendamentoResponseDTO> atualizarStatus(
            @Parameter(description = "ID do agendamento") @PathVariable Long id,
            @Valid @RequestBody AtualizacaoStatusDTO dto,
            @AuthenticationPrincipal UserDetailsImpl usuarioLogado) {

        Agendamento agendamentoAtualizado = service.atualizarStatus(id, dto.status(), dto.motivo(), usuarioLogado);

        return ResponseEntity.ok(AgendamentoResponseDTO.fromEntity(agendamentoAtualizado));
    }

    /**
     * Lista os agendamentos de um cliente específico.
     *
     * @param clienteId identificador do cliente.
     * @return {@link ResponseEntity} contendo a lista de agendamentos do cliente.
     */
    @Operation(summary = "Listar agendamentos por cliente")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarPorCliente(
            @Parameter(description = "ID do cliente") @PathVariable Long clienteId) {

        List<Agendamento> agendamentos = service.listarPorCliente(clienteId);
        List<AgendamentoResponseDTO> dtos = agendamentos.stream()
                .map(AgendamentoResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Lista os agendamentos de um prestador específico.
     *
     * @param prestadorId identificador do prestador.
     * @return {@link ResponseEntity} contendo a lista de agendamentos do prestador.
     */
    @Operation(summary = "Listar agendamentos por prestador")
    @GetMapping("/prestador/{prestadorId}")
    public ResponseEntity<List<AgendamentoResponseDTO>> listarPorPrestador(
            @Parameter(description = "ID do prestador") @PathVariable Long prestadorId) {

        List<Agendamento> agendamentos = service.listarPorPrestador(prestadorId);
        List<AgendamentoResponseDTO> dtos = agendamentos.stream()
                .map(AgendamentoResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Lista agendamentos com possibilidade de filtragem e paginação.
     * <p>
     * - Se informado {@code clienteId}, retorna apenas os agendamentos do cliente.
     * - Se informado {@code prestadorId}, retorna apenas os agendamentos do prestador.
     * - Caso contrário, retorna todos os agendamentos paginados (apenas para administradores e prestadores).
     * </p>
     *
     * @param clienteId   identificador do cliente (opcional).
     * @param prestadorId identificador do prestador (opcional).
     * @param pageable    informações de paginação.
     * @return {@link ResponseEntity} contendo a lista de agendamentos (filtrada ou paginada).
     */
    @Operation(summary = "Listar agendamentos", description = "Retorna uma lista de agendamentos. Administradores podem listar todos de forma paginada.")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SERVICE_PROVIDER')")
    @GetMapping
    public ResponseEntity<?> listarAgendamentos(
            @Parameter(description = "ID do cliente para filtrar os resultados") @RequestParam(required = false) Long clienteId,
            @Parameter(description = "ID do prestador para filtrar os resultados") @RequestParam(required = false) Long prestadorId,
            Pageable pageable) {

        if (clienteId != null) {
            List<Agendamento> agendamentos = service.listarPorCliente(clienteId);
            List<AgendamentoResponseDTO> dtos = agendamentos.stream()
                    .map(AgendamentoResponseDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        }

        if (prestadorId != null) {
            List<Agendamento> agendamentos = service.listarPorPrestador(prestadorId);
            List<AgendamentoResponseDTO> dtos = agendamentos.stream()
                    .map(AgendamentoResponseDTO::fromEntity)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        }

        Page<Agendamento> agendamentosPaginados = service.listarTodosPaginado(pageable);

        Page<AgendamentoResponseDTO> dtosPaginados = agendamentosPaginados
                .map(AgendamentoResponseDTO::fromEntity);

        return ResponseEntity.ok(dtosPaginados);
    }
}