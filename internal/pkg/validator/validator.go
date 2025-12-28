package validator

import (
	"github.com/go-playground/validator/v10"
)

// Validator wrapper para o validador
type Validator struct {
	validate *validator.Validate
}

// NewValidator cria um novo validador
func NewValidator() *Validator {
	validate := validator.New()
	return &Validator{
		validate: validate,
	}
}

// ValidateStruct valida uma struct
func (v *Validator) ValidateStruct(s interface{}) error {
	return v.validate.Struct(s)
}

// GetValidator retorna o validador subjacente
func (v *Validator) GetValidator() *validator.Validate {
	return v.validate
}
