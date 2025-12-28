package entity

import (
	"time"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/enum"
)

// Agendamento representa um agendamento de serviço
type Agendamento struct {
	ID                  uint                      `gorm:"primaryKey;column:cd_agendamento" json:"id"`
	ClienteID           uint                      `gorm:"not null;column:cd_cliente_user" json:"clienteId"`
	PrestadorID         uint                      `gorm:"not null;column:cd_prestador_user" json:"prestadorId"`
	ServicoID           uint                      `gorm:"not null;column:cd_servico" json:"servicoId"`
	DataHora            time.Time                 `gorm:"not null;column:dt_agendamento" json:"dataHora"`
	Valor               float64                   `gorm:"not null;column:vl_agendamento;type:decimal(10,2)" json:"valor"`
	Status              enum.StatusAgendamento    `gorm:"not null;column:ds_status;type:varchar(20)" json:"status"`
	MotivoCancelamento  *string                   `gorm:"column:ds_motivo_cancelamento;type:varchar(255)" json:"motivoCancelamento,omitempty"`
	Cliente             *User                     `gorm:"foreignKey:ClienteID;references:ID" json:"cliente,omitempty"`
	Prestador           *User                     `gorm:"foreignKey:PrestadorID;references:ID" json:"prestador,omitempty"`
	Servico             *Servico                  `gorm:"foreignKey:ServicoID;references:ID" json:"servico,omitempty"`
}

// TableName especifica o nome da tabela
func (Agendamento) TableName() string {
	return "agendamento"
}
