package dto

import "time"

// CreateAgendamentoRequest representa a requisição de criação de agendamento
type CreateAgendamentoRequest struct {
	PrestadorID uint      `json:"prestadorId" binding:"required"`
	ServicoID   uint      `json:"servicoId" binding:"required"`
	DataHora    time.Time `json:"dataHora" binding:"required"`
}

// UpdateStatusRequest representa a requisição de atualização de status
type UpdateStatusRequest struct {
	Status string  `json:"status" binding:"required"`
	Motivo *string `json:"motivo,omitempty"`
}

// AgendamentoResponse representa a resposta de agendamento
type AgendamentoResponse struct {
	ID                 uint                `json:"id"`
	ClienteID          uint                `json:"clienteId"`
	PrestadorID        uint                `json:"prestadorId"`
	ServicoID          uint                `json:"servicoId"`
	DataHora           time.Time           `json:"dataHora"`
	Valor              float64             `json:"valor"`
	Status             string              `json:"status"`
	MotivoCancelamento *string             `json:"motivoCancelamento,omitempty"`
	Cliente            *UserSimpleResponse `json:"cliente,omitempty"`
	Prestador          *UserSimpleResponse `json:"prestador,omitempty"`
	Servico            *ServicoResponse    `json:"servico,omitempty"`
}

// UserSimpleResponse representa uma resposta simplificada de usuário
type UserSimpleResponse struct {
	ID    uint   `json:"id"`
	Name  string `json:"name"`
	Email string `json:"email"`
}
