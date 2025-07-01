package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.Servicos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicosRepository extends JpaRepository<Servicos, Long> {
}
