package com.prestacaoservicos.controller;

import com.prestacaoservicos.dto.ServicoRequestDTO;
import com.prestacaoservicos.dto.ServicoResponseDTO;
import com.prestacaoservicos.entity.Servico;
import com.prestacaoservicos.security.userdetails.UserDetailsImpl;
import com.prestacaoservicos.service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador responsável pelas operações relacionadas a {@link Servico}.
 * <p>
 * Fornece endpoints REST para criação, atualização, listagem, busca e exclusão de serviços.
 * </p>
 *
 * @author Você
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/servicos")
@Tag(name = "Serviços", description = "Operações para gerenciamento de serviços")
public class ServicoController {

    private final ServicoService service;

    /**
     * Construtor que injeta o {@link ServicoService}.
     *
     * @param service serviço responsável pelas regras de negócio dos serviços.
     */
    public ServicoController(ServicoService service) {
        this.service = service;
    }

    /**
     * Cria um novo serviço no sistema.
     *
     * @param dto          objeto com os dados do serviço a ser criado.
     * @param usuarioLogado usuário autenticado que está criando o serviço.
     * @return {@link ResponseEntity} contendo o serviço criado e a URI de localização.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SERVICE_PROVIDER')")
    @Operation(summary = "Criar um novo serviço", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServicoResponseDTO> create(
            @Valid @RequestBody ServicoRequestDTO dto,
            @AuthenticationPrincipal UserDetailsImpl usuarioLogado) {

        Servico novoServico = service.create(dto, usuarioLogado);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novoServico.getId()).toUri();
        return ResponseEntity.created(location).body(ServicoResponseDTO.fromEntity(novoServico));
    }

    /**
     * Lista todos os serviços cadastrados no sistema.
     *
     * @return {@link ResponseEntity} contendo a lista de serviços disponíveis.
     */
    @GetMapping
    @Operation(summary = "Listar todos os serviços disponíveis")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SERVICE_PROVIDER', 'CUSTOMER')")
    public ResponseEntity<List<ServicoResponseDTO>> findAll() {
        List<Servico> servicos = service.findAll();
        List<ServicoResponseDTO> dtos = servicos.stream()
                .map(ServicoResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Busca um serviço pelo seu identificador único.
     *
     * @param id identificador do serviço.
     * @return {@link ResponseEntity} contendo o serviço encontrado.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar um serviço por ID")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SERVICE_PROVIDER', 'CUSTOMER')")
    public ResponseEntity<ServicoResponseDTO> findById(@PathVariable Long id) {
        Servico servico = service.findById(id);
        return ResponseEntity.ok(ServicoResponseDTO.fromEntity(servico));
    }

    /**
     * Atualiza os dados de um serviço existente.
     *
     * @param id  identificador do serviço a ser atualizado.
     * @param dto objeto com os novos dados do serviço.
     * @return {@link ResponseEntity} contendo o serviço atualizado.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SERVICE_PROVIDER')")
    @Operation(summary = "Atualizar um serviço existente", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ServicoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ServicoRequestDTO dto) {
        Servico servicoAtualizado = service.update(id, dto);
        return ResponseEntity.ok(ServicoResponseDTO.fromEntity(servicoAtualizado));
    }

    /**
     * Remove um serviço do sistema.
     *
     * @param id identificador do serviço a ser excluído.
     * @return {@link ResponseEntity} sem conteúdo indicando que a operação foi concluída com sucesso.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'SERVICE_PROVIDER')")
    @Operation(summary = "Deletar um serviço", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}