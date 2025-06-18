package com.prestacaoservicos.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import java.util.Objects;

@Entity
public class Agendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cliente cliente;

    @ManyToOne
    private Prestador prestador;

    private LocalDateTime dataHora;
    private Double valor;
    private String status;
    private String motivoCancelamento;

    public Agendamento() {
    }

    public Agendamento(Long id, Cliente cliente, Prestador prestador, LocalDateTime dataHora, Double valor, String status, String motivoCancelamento) {
        this.id = id;
        this.cliente = cliente;
        this.prestador = prestador;
        this.dataHora = dataHora;
        this.valor = valor;
        this.status = status;
        this.motivoCancelamento = motivoCancelamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Prestador getPrestador() {
        return prestador;
    }

    public void setPrestador(Prestador prestador) {
        this.prestador = prestador;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agendamento that = (Agendamento) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(cliente, that.cliente) &&
                Objects.equals(prestador, that.prestador) &&
                Objects.equals(dataHora, that.dataHora) &&
                Objects.equals(valor, that.valor) &&
                Objects.equals(status, that.status) &&
                Objects.equals(motivoCancelamento, that.motivoCancelamento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cliente, prestador, dataHora, valor, status, motivoCancelamento);
    }

    @Override
    public String toString() {
        return "Agendamento{" +
                "id=" + id +
                ", cliente=" + cliente +
                ", prestador=" + prestador +
                ", dataHora=" + dataHora +
                ", valor=" + valor +
                ", status='" + status + '\'' +
                ", motivoCancelamento='" + motivoCancelamento + '\'' +
                '}';
    }
}