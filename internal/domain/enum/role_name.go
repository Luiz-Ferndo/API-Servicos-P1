package enum

import (
	"database/sql/driver"
	"fmt"
	"strings"
)

// RoleName representa os papéis (roles) disponíveis no sistema
type RoleName string

const (
	RoleCustomer        RoleName = "ROLE_CUSTOMER"
	RoleAdministrator   RoleName = "ROLE_ADMINISTRATOR"
	RoleServiceProvider RoleName = "ROLE_SERVICE_PROVIDER"
)

// String retorna a representação em string do RoleName
func (r RoleName) String() string {
	return string(r)
}

// IsValid verifica se o RoleName é válido
func (r RoleName) IsValid() bool {
	switch r {
	case RoleCustomer, RoleAdministrator, RoleServiceProvider:
		return true
	}
	return false
}

// FromString converte uma string para RoleName
func RoleNameFromString(s string) (RoleName, error) {
	s = strings.TrimSpace(strings.ToUpper(s))
	role := RoleName(s)
	if !role.IsValid() {
		return "", fmt.Errorf("role inválida: %s", s)
	}
	return role, nil
}

// Value implementa driver.Valuer para salvar no banco
func (r RoleName) Value() (driver.Value, error) {
	return string(r), nil
}

// Scan implementa sql.Scanner para ler do banco
func (r *RoleName) Scan(value interface{}) error {
	if value == nil {
		return nil
	}
	
	str, ok := value.(string)
	if !ok {
		return fmt.Errorf("failed to scan RoleName: %v", value)
	}
	
	role := RoleName(str)
	if !role.IsValid() {
		return fmt.Errorf("invalid RoleName: %s", str)
	}
	
	*r = role
	return nil
}
