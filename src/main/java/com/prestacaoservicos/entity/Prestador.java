package com.prestacaoservicos.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "prestador")
public class Prestador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_prestador")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cd_servico", nullable = false)
    private Servico servico;

    public Prestador() {
    }

    public Prestador(Long id, Servico servico) {
        this.id = id;
        this.servico = servico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prestador prestador = (Prestador) o;
        return Objects.equals(id, prestador.id) &&
                Objects.equals(servico, prestador.servico);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, servico);
    }

    @Override
    public String toString() {
        return "Prestador{" +
                "id=" + id +
                ", servico='" + servico + '\'' +
                '}';
    }
}