package com.prestacaoservicos.service;

import com.prestacaoservicos.entity.Servico;
import com.prestacaoservicos.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    public List<Servico> listar() {
        return servicoRepository.findAll();
    }

    public Optional<Servico> buscarPorId(Long id) {
        return servicoRepository.findById(id);
    }

    public Servico salvar(Servico Servico) {
        return servicoRepository.save(Servico);
    }

    public Servico atualizar(Long id, Servico servico) {
        return servicoRepository.findById(id).map(s -> {
            s.setCodigo(servico.getCodigo());
            s.setDescricao(servico.getDescricao());
            return servicoRepository.save(s);
        }).orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
    }

    public void deletar(Long id) {
        servicoRepository.deleteById(id);
    }


}
