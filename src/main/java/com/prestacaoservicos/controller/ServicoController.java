package com.prestacaoservicos.controller;


import com.prestacaoservicos.entity.Servico;
import com.prestacaoservicos.service.ServicoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private ServicoService servicosService;


    public List<Servico> listaServicos(){
        return servicosService.listar();
    }

    @GetMapping("/{id}")
    public Optional<Servico> listaServicosPorId(Long id){
        return servicosService.buscarPorId(id);
    }

    @PostMapping
    public Servico criarServico(Servico servico) {
        return servicosService.salvar(servico);
    }

    @PutMapping("/{id}")
    public Servico atualizarServico(@PathVariable Long id, @RequestBody Servico servico) {
        return servicosService.atualizar(id, servico);
    }

    @DeleteMapping("/{id}")
    public void deletarServico(@PathVariable Long id) {
        servicosService.deletar(id);
    }
}
