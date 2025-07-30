package com.prestacaoservicos.service;

import com.prestacaoservicos.entity.Servico;
import com.prestacaoservicos.exception.RecursoNaoEncontradoException;
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

    public Servico salvar(Servico servico) {
        return servicoRepository.save(servico);
    }

    public Servico atualizar(Long id, Servico servico) {
        return servicoRepository.findById(id).map(s -> {
            s.setCodigo(servico.getCodigo());
            s.setDescricao(servico.getDescricao());
            return servicoRepository.save(s);
        }).orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado"));
    }

    public void deletar(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Serviço não encontrado");
        }
        servicoRepository.deleteById(id);
    }
}