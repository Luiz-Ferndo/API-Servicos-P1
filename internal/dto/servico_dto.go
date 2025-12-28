package dto

// CreateServicoRequest representa a requisição de criação de serviço
type CreateServicoRequest struct {
	Nome      string  `json:"nome" binding:"required,min=3,max=100"`
	Valor     float64 `json:"valor" binding:"required,gt=0"`
	Descricao string  `json:"descricao" binding:"required,min=3,max=255"`
}

// UpdateServicoRequest representa a requisição de atualização de serviço
type UpdateServicoRequest struct {
	Nome      string  `json:"nome" binding:"omitempty,min=3,max=100"`
	Valor     float64 `json:"valor" binding:"omitempty,gt=0"`
	Descricao string  `json:"descricao" binding:"omitempty,min=3,max=255"`
	Ativo     *bool   `json:"ativo" binding:"omitempty"`
}

// ServicoResponse representa a resposta de serviço
type ServicoResponse struct {
	ID        uint    `json:"id"`
	Nome      string  `json:"nome"`
	Valor     float64 `json:"valor"`
	Descricao string  `json:"descricao"`
	Ativo     bool    `json:"ativo"`
}
