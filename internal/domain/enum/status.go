package enum

import (
	"database/sql/driver"
	"fmt"
	"strings"
)

// StatusAgendamento representa os possíveis status de um agendamento
type StatusAgendamento string

const (
	StatusAgendado      StatusAgendamento = "AGENDADO"
	StatusConfirmado    StatusAgendamento = "CONFIRMADO"
	StatusCancelado     StatusAgendamento = "CANCELADO"
	StatusFinalizado    StatusAgendamento = "FINALIZADO"
	StatusNaoCompareceu StatusAgendamento = "NAO_COMPARECEU"
)

// String retorna a representação em string
func (s StatusAgendamento) String() string {
	return string(s)
}

// GetDescricao retorna a descrição do status em português
func (s StatusAgendamento) GetDescricao() string {
	descriptions := map[StatusAgendamento]string{
		StatusAgendado:      "Agendado",
		StatusConfirmado:    "Confirmado",
		StatusCancelado:     "Cancelado",
		StatusFinalizado:    "Finalizado",
		StatusNaoCompareceu: "Não Compareceu",
	}
	return descriptions[s]
}

// IsValid verifica se o status é válido
func (s StatusAgendamento) IsValid() bool {
	switch s {
	case StatusAgendado, StatusConfirmado, StatusCancelado, StatusFinalizado, StatusNaoCompareceu:
		return true
	}
	return false
}

// FromString converte uma string para StatusAgendamento
func StatusAgendamentoFromString(s string) (StatusAgendamento, error) {
	s = strings.TrimSpace(strings.ToUpper(s))
	status := StatusAgendamento(s)
	if !status.IsValid() {
		return "", fmt.Errorf("status inválido: %s", s)
	}
	return status, nil
}

// Value implementa driver.Valuer para salvar no banco
func (s StatusAgendamento) Value() (driver.Value, error) {
	return string(s), nil
}

// Scan implementa sql.Scanner para ler do banco
func (s *StatusAgendamento) Scan(value interface{}) error {
	if value == nil {
		return nil
	}
	
	str, ok := value.(string)
	if !ok {
		return fmt.Errorf("failed to scan StatusAgendamento: %v", value)
	}
	
	status := StatusAgendamento(str)
	if !status.IsValid() {
		return fmt.Errorf("invalid StatusAgendamento: %s", str)
	}
	
	*s = status
	return nil
}
