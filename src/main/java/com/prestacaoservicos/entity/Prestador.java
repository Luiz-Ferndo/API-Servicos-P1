package com.prestacaoservicos.entity;

import jakarta.persistence.*;

@Entity
public class Prestador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String servico;
    private Double preco;

    public Prestador() {
    }

    public Prestador(Long id, String nome, String servico, Double preco) {
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

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
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
        return java.util.Objects.equals(id, prestador.id) &&
                java.util.Objects.equals(nome, prestador.nome) &&
                java.util.Objects.equals(servico, prestador.servico) &&
                java.util.Objects.equals(preco, prestador.preco);
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