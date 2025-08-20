package com.prestacaoservicos.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "servico")
public class Servico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_servico")
    private Long codigo;

    @Column(name = "nm_servico", nullable = false, length = 100)
    private String nome;

    @Column(name = "vl_servico", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "ds_servico", nullable = false, length = 255)
    private String descricao;

    public Servico() {}

    public Servico(Long codigo, String nome, BigDecimal valor, String descricao) {
        this.codigo = codigo;
        this.nome = nome;
        this.valor = valor;
        this.descricao = descricao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servico servico = (Servico) o;
        return Objects.equals(codigo, servico.codigo) &&
                Objects.equals(nome, servico.nome) &&
                Objects.equals(valor, servico.valor) &&
                Objects.equals(descricao, servico.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Servico{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", valor=" + valor +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}