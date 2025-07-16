package com.prestacaoservicos.service;

import com.prestacaoservicos.entity.Servico;
import com.prestacaoservicos.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicoService {

    private final ServicoRepository ServicoRepository;

    public ServicoService(ServicoRepository ServicoRepository) {
        this.ServicoRepository = ServicoRepository;
    }

    public List<Servico> listar() {
        return ServicoRepository.findAll();
    }

    public Optional<Servico> buscarPorId(Long id) {
        return ServicoRepository.findById(id);
    }

    public Servico salvar(Servico Servico) {
        return ServicoRepository.save(Servico);
    }

    public Servico atualizar(Long id, Servico servico) {
        return ServicoRepository.findById(id).map(s -> {
            s.setCodigo(servico.getCodigo());
            s.setDescricao(servico.getDescricao());
            return ServicoRepository.save(s);
        }).orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
    }

    public void deletar(Long id) {
        ServicoRepository.deleteById(id);
    }


}
