package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
}
