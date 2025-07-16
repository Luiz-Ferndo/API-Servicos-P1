package com.prestacaoservicos.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "prestador")
public class Prestador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_prestador")
    private Long id;

    @Column(name = "nm_prestador", nullable = false, length = 255)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cd_servico", nullable = false)
    private Servico servico;

    @Column(name = "vl_servico", nullable = false)
    private Double preco;

    public Prestador() {
    }

    public Prestador(Long id, String nome, Servico servico, Double preco) {
        this.id = id;
        this.nome = nome;
        this.servico = servico;
        this.preco = preco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prestador prestador = (Prestador) o;
        return Objects.equals(id, prestador.id) &&
                Objects.equals(nome, prestador.nome) &&
                Objects.equals(servico, prestador.servico) &&
                Objects.equals(preco, prestador.preco);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, nome, servico, preco);
    }

    @Override
    public String toString() {
        return "Prestador{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", servico='" + servico + '\'' +
                ", preco=" + preco +
                '}';
    }
}