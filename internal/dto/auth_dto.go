package dto

import "time"

// LoginRequest representa a requisição de login
type LoginRequest struct {
	Email    string `json:"email" binding:"required,email"`
	Password string `json:"password" binding:"required,min=6"`
}

// LoginResponse representa a resposta do login
type LoginResponse struct {
	Token string `json:"token"`
	Type  string `json:"type"`
}

// JWTClaims representa as claims do token JWT
type JWTClaims struct {
	UserID uint     `json:"user_id"`
	Email  string   `json:"email"`
	Roles  []string `json:"roles"`
	Exp    int64    `json:"exp"`
}

// RefreshTokenRequest representa a requisição de refresh token
type RefreshTokenRequest struct {
	Token string `json:"token" binding:"required"`
}

// TokenCacheData representa os dados do token armazenados no Redis
type TokenCacheData struct {
	UserID    uint      `json:"user_id"`
	Email     string    `json:"email"`
	Roles     []string  `json:"roles"`
	ExpiresAt time.Time `json:"expires_at"`
}
