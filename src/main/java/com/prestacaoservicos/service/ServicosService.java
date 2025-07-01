package com.prestacaoservicos.service;

import com.prestacaoservicos.entity.Servicos;
import com.prestacaoservicos.repository.ServicosRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicosService {

    private ServicosRepository servicosRepository;

    public ServicosService(ServicosRepository servicosRepository) {
        this.servicosRepository = servicosRepository;
    }

    public List<Servicos> listar() {
        return servicosRepository.findAll();
    }

    public Optional<Servicos> buscarPorId(Long id) {
        return servicosRepository.findById(id);
    }

    public Servicos salvar(Servicos servicos) {
        return servicosRepository.save(servicos);
    }

    public Servicos atualizar(Long id, Servicos novo) {
        return servicosRepository.findById(id).map(s -> {
            s.setNome(novo.getNome());
            s.setDescricao(novo.getDescricao());
            return servicosRepository.save(s);
        }).orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
    }
}
