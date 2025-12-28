package enum

import (
	"database/sql/driver"
	"fmt"
	"strings"
)

// PhoneType representa os tipos de telefone disponíveis
type PhoneType string

const (
	PhoneTypeMobile PhoneType = "MOBILE"
	PhoneTypeHome   PhoneType = "HOME"
	PhoneTypeWork   PhoneType = "WORK"
)

// String retorna a representação em string
func (p PhoneType) String() string {
	return string(p)
}

// IsValid verifica se o tipo de telefone é válido
func (p PhoneType) IsValid() bool {
	switch p {
	case PhoneTypeMobile, PhoneTypeHome, PhoneTypeWork:
		return true
	}
	return false
}

// FromString converte uma string para PhoneType
func PhoneTypeFromString(s string) (PhoneType, error) {
	s = strings.TrimSpace(strings.ToUpper(s))
	phoneType := PhoneType(s)
	if !phoneType.IsValid() {
		return "", fmt.Errorf("tipo de telefone inválido: %s", s)
	}
	return phoneType, nil
}

// Value implementa driver.Valuer para salvar no banco
func (p PhoneType) Value() (driver.Value, error) {
	return string(p), nil
}

// Scan implementa sql.Scanner para ler do banco
func (p *PhoneType) Scan(value interface{}) error {
	if value == nil {
		return nil
	}
	
	str, ok := value.(string)
	if !ok {
		return fmt.Errorf("failed to scan PhoneType: %v", value)
	}
	
	phoneType := PhoneType(str)
	if !phoneType.IsValid() {
		return fmt.Errorf("invalid PhoneType: %s", str)
	}
	
	*p = phoneType
	return nil
}
