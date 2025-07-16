package com.prestacaoservicos.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "dominio_status_agendamento")
public class DominioStatusAgendamento {
    @Id
    @Column(name = "cd_status")
    private Integer codigo;

    @Column(name = "ds_status", nullable = false, length = 50)
    private String descricao;

    public DominioStatusAgendamento() {
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DominioStatusAgendamento)) return false;
        DominioStatusAgendamento dominioStatusAgendamento = (DominioStatusAgendamento) o;
        return Objects.equals(codigo, dominioStatusAgendamento.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "DominioStatusAgendamento{" +
                "codigo=" + codigo +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}