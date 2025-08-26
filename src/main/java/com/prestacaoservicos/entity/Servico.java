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
    private Long id;

    @Column(name = "nm_servico", nullable = false, length = 100)
    private String nome;

    @Column(name = "vl_servico", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "ds_servico", nullable = false, length = 255)
    private String descricao;

    @Column(name = "st_ativo", nullable = false)
    private boolean ativo = true;

    public Servico() {}

    public Servico(Long id, String nome, BigDecimal valor, String descricao, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.valor = valor;
        this.descricao = descricao;
        this.ativo = ativo;
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

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servico servico = (Servico) o;
        return Objects.equals(id, servico.id) &&
                Objects.equals(nome, servico.nome) &&
                Objects.equals(valor, servico.valor) &&
                Objects.equals(descricao, servico.descricao) &&
                Objects.equals(ativo, servico.ativo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Servico{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", valor=" + valor +
                ", descricao='" + descricao + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}