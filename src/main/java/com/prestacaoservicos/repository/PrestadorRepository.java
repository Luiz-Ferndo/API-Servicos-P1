package com.prestacaoservicos.repository;

import com.prestacaoservicos.entity.Prestador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestadorRepository extends JpaRepository<Prestador, Long> {
}