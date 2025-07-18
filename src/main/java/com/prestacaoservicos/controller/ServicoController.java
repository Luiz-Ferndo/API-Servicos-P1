package com.prestacaoservicos.controller;

import com.prestacaoservicos.entity.Servico;
import com.prestacaoservicos.service.ServicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/servicos")
public class ServicoController {

    private final ServicoService servicosService;

    public ServicoController(ServicoService servicosService) {
        this.servicosService = servicosService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os serviços")
    public List<Servico> listaServicos(){
        return servicosService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar serviço por ID")
    public Optional<Servico> listaServicosPorId(
            @Parameter(description = "ID do serviço que deseja buscar", example = "1", required = true)
            @PathVariable Long id){
        return servicosService.buscarPorId(id);
    }

    @PostMapping
    @Operation(summary = "Criar novo serviço")
    public Servico criarServico(
            @Parameter(description = "Dados do serviço a ser criado", required = true)
            @RequestBody Servico servico) {
        return servicosService.salvar(servico);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar serviço existente")
    public Servico atualizarServico(
            @Parameter(description = "ID do serviço a ser atualizado", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Novos dados do serviço", required = true)
            @RequestBody Servico servico) {
        return servicosService.atualizar(id, servico);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar serviço por ID")
    public void deletarServico(
            @Parameter(description = "ID do serviço a ser deletado", example = "1", required = true)
            @PathVariable Long id) {
        servicosService.deletar(id);
    }
}
