package com. prestacaoservicos.dto;

import com.prestacaoservicos.entity. Servico;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public record ServicoResponseDTO(
        Long id,
        String nome,
        BigDecimal valor,
        String descricao,
        List<PrestadorInfo> prestadores
) {
    /**
     * DTO interno para representar informações básicas do prestador
     */
    public record PrestadorInfo(
            Long id,
            String nome,
            String email
    ) {}

    /**
     * Construtor para criar um ServicoResponseDTO a partir de uma entidade Servico. 
     *
     * @param servico A entidade Servico da qual os dados serão extraídos. 
     */
    public static ServicoResponseDTO fromEntity(Servico servico) {
        List<PrestadorInfo> prestadoresInfo = servico.getPrestadores().stream()
                .map(user -> new PrestadorInfo(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                ))
                .collect(Collectors.toList());

        return new ServicoResponseDTO(
                servico.getId(),
                servico.getNome(),
                servico.getValor(),
                servico.getDescricao(),
                prestadoresInfo
        );
    }
}