package com.prestacaoservicos.service;

import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    public void processar(String metodo) {
        System.out.println("Pagamento via: " + metodo);
    }

    public void reembolsar(Long agendamentoId) {
        System.out.println("Reembolsando agendamento: " + agendamentoId);
    }
}