package com.prestacaoservicos.entity;

import com.prestacaoservicos.enums.StatusAgendamentoEnum;
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
    @JoinColumn(name = "cd_cliente_user", nullable = false)
    private User cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cd_prestador_user", nullable = false)
    private User prestador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cd_servico", nullable = false)
    private Servico servico;

    @Column(name = "dt_agendamento", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "vl_agendamento", nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "ds_status", nullable = false, length = 20)
    private StatusAgendamentoEnum status;

    @Column(name = "ds_motivo_cancelamento", length = 255)
    private String motivoCancelamento;

    public Agendamento() {}

    public Agendamento(Long id, User cliente, User prestador, Servico servico, LocalDateTime dataHora, BigDecimal valor, StatusAgendamentoEnum status, String motivoCancelamento) {
        this.id = id;
        this.cliente = cliente;
        this.prestador = prestador;
        this.servico = servico;
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

    public User getCliente() {
        return cliente;
    }

    public void setCliente(User cliente) {
        this.cliente = cliente;
    }

    public User getPrestador() {
        return prestador;
    }

    public void setPrestador(User prestador) {
        this.prestador = prestador;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
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

    public StatusAgendamentoEnum getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamentoEnum status) {
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
        if (o == null || getClass() != o.getClass()) return false;
        Agendamento that = (Agendamento) o;
        return Objects.equals(id, that.id) && Objects.equals(cliente, that.cliente) && Objects.equals(prestador, that.prestador) && Objects.equals(servico, that.servico) && Objects.equals(dataHora, that.dataHora) && Objects.equals(valor, that.valor) && status == that.status && Objects.equals(motivoCancelamento, that.motivoCancelamento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cliente, prestador, servico, dataHora, valor, status, motivoCancelamento);
    }

    @Override
    public String toString() {
        return "Agendamento{" +
                "id=" + id +
                ", cliente=" + cliente +
                ", prestador=" + prestador +
                ", servico=" + servico +
                ", dataHora=" + dataHora +
                ", valor=" + valor +
                ", status=" + status +
                ", motivoCancelamento='" + motivoCancelamento + '\'' +
                '}';
    }
}