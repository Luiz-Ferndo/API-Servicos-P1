package com.prestacaoservicos.dto;

import com.prestacaoservicos.entity.Servico;

import java.math.BigDecimal;

public record ServicoResponseDTO(
        Long id,
        String nome,
        BigDecimal valor,
        String descricao
) {
    /**
     * Construtor para criar um ServicoResponseDTO a partir de uma entidade Servico.
     *
     * @param servico A entidade Servico da qual os dados serão extraídos.
     */
    public static ServicoResponseDTO fromEntity(Servico servico) {
        return new ServicoResponseDTO(
                servico.getId(),
                servico.getNome(),
                servico.getValor(),
                servico.getDescricao()
        );
    }
}