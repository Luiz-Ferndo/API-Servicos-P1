package com.prestacaoservicos.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.Objects;

@Entity
@Table(name = "agendamento")
public class Agendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cd_agendamento")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cd_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cd_prestador", nullable = false)
    private Prestador prestador;

    @Column(name = "dt_agendamento", nullable = false)
    private LocalDateTime dataHora;
    @Column(name = "vl_agendamento", nullable = false)
    private BigDecimal valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cd_status", nullable = false)
    private DominioStatusAgendamento status;

    @Column(name = "ds_motivo_cancelamento", length = 255)
    private String motivoCancelamento;

    public Agendamento() {
    }

    public Agendamento(Long id, Cliente cliente, Prestador prestador, LocalDateTime dataHora, BigDecimal valor, DominioStatusAgendamento dominioStatusAgendamento, String motivoCancelamento) {
        this.id = id;
        this.cliente = cliente;
        this.prestador = prestador;
        this.dataHora = dataHora;
        this.valor = valor;
        this.status = dominioStatusAgendamento;
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public DominioStatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(DominioStatusAgendamento status) {
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