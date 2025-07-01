package com.prestacaoservicos.controller;


import com.prestacaoservicos.entity.Servicos;
import com.prestacaoservicos.service.ServicosService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/servicos")
public class ServicosController {

    private ServicosService servicosService;


    public List<Servicos> listaServicos(){
        return servicosService.listar();
    }

    @GetMapping("/{id}")
    public Optional<Servicos> listaServicosPorId(Long id){
        return servicosService.buscarPorId(id);
    }

    @PostMapping
    public Servicos criarServico(Servicos servicos) {
        return servicosService.salvar(servicos);
    }

    @PutMapping("/{id}")
    public Servicos atualizarServico(@PathVariable Long id, @RequestBody Servicos servicos) {
        return servicosService.atualizar(id, servicos);
    }

    @DeleteMapping("/{id}")
    public void deletarServico(@PathVariable Long id) {
        servicosService.deletar(id);
    }
}
