package com.prestacaoservicos.service;

import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    /**
     * Processa o pagamento utilizando o método especificado.
     *
     * @param metodo O método de pagamento a ser utilizado (ex: cartão de crédito, boleto, etc.).
     */
    public void processar(String metodo) {
        System.out.println("Pagamento via: " + metodo);
    }

    /**
     * Reembolsa um agendamento específico.
     *
     * @param agendamentoId O ID do agendamento a ser reembolsado.
     */
    public void reembolsar(Long agendamentoId) {
        System.out.println("Reembolsando agendamento: " + agendamentoId);
    }
}