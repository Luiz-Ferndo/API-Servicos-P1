package dto

// CreateUserRequest representa a requisição de criação de usuário
type CreateUserRequest struct {
	Name     string   `json:"name" binding:"required,min=3,max=255"`
	Email    string   `json:"email" binding:"required,email,max=255"`
	Password string   `json:"password" binding:"required,min=6,max=255"`
	Role     string   `json:"role" binding:"required"`
	Phones   []PhoneDTO `json:"phones,omitempty"`
}

// UpdateUserRequest representa a requisição de atualização de usuário
type UpdateUserRequest struct {
	Name   string     `json:"name" binding:"omitempty,min=3,max=255"`
	Email  string     `json:"email" binding:"omitempty,email,max=255"`
	Phones []PhoneDTO `json:"phones,omitempty"`
}

// PhoneDTO representa um telefone
type PhoneDTO struct {
	Number string `json:"number" binding:"required,min=8,max=20"`
	Type   string `json:"type" binding:"required"`
}

// UserResponse representa a resposta de usuário
type UserResponse struct {
	ID     uint         `json:"id"`
	Name   string       `json:"name"`
	Email  string       `json:"email"`
	Roles  []string     `json:"roles,omitempty"`
	Phones []PhoneDTO   `json:"phones,omitempty"`
}
