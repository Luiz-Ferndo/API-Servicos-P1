package enum

import (
	"database/sql/driver"
	"fmt"
)

// Permission representa as permissões disponíveis no sistema
type Permission string

const (
	ManageUsers       Permission = "MANAGE_USERS"
	ManageServices    Permission = "MANAGE_SERVICES"
	ViewReports       Permission = "VIEW_REPORTS"
	BookService       Permission = "BOOK_SERVICE"
	ViewAppointments  Permission = "VIEW_APPOINTMENTS"
	CancelAppointment Permission = "CANCEL_APPOINTMENT"
	ConfirmExecution  Permission = "CONFIRM_EXECUTION"
	DefineAvailability Permission = "DEFINE_AVAILABILITY"
	MakePayment       Permission = "MAKE_PAYMENT"
	ViewServices      Permission = "VIEW_SERVICES"
)

// String retorna a representação em string da Permission
func (p Permission) String() string {
	return string(p)
}

// GetDescription retorna a descrição da permissão em português
func (p Permission) GetDescription() string {
	descriptions := map[Permission]string{
		ManageUsers:        "Gerenciar usuários",
		ManageServices:     "Gerenciar serviços",
		ViewReports:        "Visualizar relatórios",
		BookService:        "Agendar serviço",
		ViewAppointments:   "Consultar agendamentos",
		CancelAppointment:  "Cancelar agendamento",
		ConfirmExecution:   "Confirmar execução do serviço",
		DefineAvailability: "Definir disponibilidade",
		MakePayment:        "Efetuar pagamento",
		ViewServices:       "Consultar serviços",
	}
	return descriptions[p]
}

// Value implementa driver.Valuer para salvar no banco
func (p Permission) Value() (driver.Value, error) {
	return string(p), nil
}

// Scan implementa sql.Scanner para ler do banco
func (p *Permission) Scan(value interface{}) error {
	if value == nil {
		return nil
	}
	
	str, ok := value.(string)
	if !ok {
		return fmt.Errorf("failed to scan Permission: %v", value)
	}
	
	*p = Permission(str)
	return nil
}
