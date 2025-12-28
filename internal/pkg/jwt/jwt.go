package jwt

import (
	"fmt"
	"time"

	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/dto"
	"github.com/golang-jwt/jwt/v5"
)

// Manager gerencia tokens JWT
type Manager struct {
	secret            string
	expirationHours   int
}

// NewManager cria um novo gerenciador de JWT
func NewManager(secret string, expirationHours int) *Manager {
	return &Manager{
		secret:          secret,
		expirationHours: expirationHours,
	}
}

// GenerateToken gera um novo token JWT
func (m *Manager) GenerateToken(userID uint, email string, roles []string) (string, error) {
	expiresAt := time.Now().Add(time.Duration(m.expirationHours) * time.Hour)

	claims := jwt.MapClaims{
		"user_id": userID,
		"email":   email,
		"roles":   roles,
		"exp":     expiresAt.Unix(),
		"iat":     time.Now().Unix(),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	tokenString, err := token.SignedString([]byte(m.secret))
	if err != nil {
		return "", fmt.Errorf("failed to generate token: %w", err)
	}

	return tokenString, nil
}

// ValidateToken valida e extrai as claims de um token JWT
func (m *Manager) ValidateToken(tokenString string) (*dto.JWTClaims, error) {
	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
		}
		return []byte(m.secret), nil
	})

	if err != nil {
		return nil, fmt.Errorf("failed to parse token: %w", err)
	}

	if !token.Valid {
		return nil, fmt.Errorf("invalid token")
	}

	claims, ok := token.Claims.(jwt.MapClaims)
	if !ok {
		return nil, fmt.Errorf("invalid token claims")
	}

	// Extrair dados das claims
	userID, ok := claims["user_id"].(float64)
	if !ok {
		return nil, fmt.Errorf("invalid user_id in token")
	}

	email, ok := claims["email"].(string)
	if !ok {
		return nil, fmt.Errorf("invalid email in token")
	}

	rolesInterface, ok := claims["roles"].([]interface{})
	if !ok {
		return nil, fmt.Errorf("invalid roles in token")
	}

	roles := make([]string, len(rolesInterface))
	for i, r := range rolesInterface {
		roles[i], ok = r.(string)
		if !ok {
			return nil, fmt.Errorf("invalid role format in token")
		}
	}

	exp, ok := claims["exp"].(float64)
	if !ok {
		return nil, fmt.Errorf("invalid exp in token")
	}

	return &dto.JWTClaims{
		UserID: uint(userID),
		Email:  email,
		Roles:  roles,
		Exp:    int64(exp),
	}, nil
}

// GetExpirationTime retorna o tempo de expiração configurado
func (m *Manager) GetExpirationTime() time.Duration {
	return time.Duration(m.expirationHours) * time.Hour
}
