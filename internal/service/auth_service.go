package service

import (
	"context"
	"fmt"
	"time"

	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/dto"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/pkg/jwt"
	pkgredis "github.com/Luiz-Ferndo/API-Servicos-P1/internal/pkg/redis"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/repository"
	"golang.org/x/crypto/bcrypt"
)

// AuthService gerencia a autenticação de usuários
type AuthService struct {
	userRepo    *repository.UserRepository
	jwtManager  *jwt.Manager
	redisClient *pkgredis.Client
}

// NewAuthService cria um novo serviço de autenticação
func NewAuthService(userRepo *repository.UserRepository, jwtManager *jwt.Manager, redisClient *pkgredis.Client) *AuthService {
	return &AuthService{
		userRepo:    userRepo,
		jwtManager:  jwtManager,
		redisClient: redisClient,
	}
}

// Login autentica um usuário e retorna um token JWT
func (s *AuthService) Login(ctx context.Context, req *dto.LoginRequest) (*dto.LoginResponse, error) {
	// Buscar usuário por email
	user, err := s.userRepo.FindByEmail(req.Email)
	if err != nil {
		return nil, fmt.Errorf("credenciais inválidas")
	}

	// Verificar senha
	if err := bcrypt.CompareHashAndPassword([]byte(user.Password), []byte(req.Password)); err != nil {
		return nil, fmt.Errorf("credenciais inválidas")
	}

	// Extrair roles
	roles := make([]string, len(user.Roles))
	for i, role := range user.Roles {
		roles[i] = role.Name.String()
	}

	// Gerar token JWT
	token, err := s.jwtManager.GenerateToken(user.ID, user.Email, roles)
	if err != nil {
		return nil, fmt.Errorf("erro ao gerar token: %w", err)
	}

	// Armazenar token no Redis
	expiresAt := time.Now().Add(s.jwtManager.GetExpirationTime())
	tokenData := &dto.TokenCacheData{
		UserID:    user.ID,
		Email:     user.Email,
		Roles:     roles,
		ExpiresAt: expiresAt,
	}

	if err := s.redisClient.SetToken(ctx, user.ID, tokenData, s.jwtManager.GetExpirationTime()); err != nil {
		// Log do erro, mas não falhar o login
		fmt.Printf("Warning: failed to cache token in Redis: %v\n", err)
	}

	return &dto.LoginResponse{
		Token: token,
		Type:  "Bearer",
	}, nil
}

// ValidateToken valida um token JWT
func (s *AuthService) ValidateToken(ctx context.Context, tokenString string) (*dto.JWTClaims, error) {
	// Validar token
	claims, err := s.jwtManager.ValidateToken(tokenString)
	if err != nil {
		return nil, fmt.Errorf("token inválido: %w", err)
	}

	// Verificar se o token está no Redis
	cachedToken, err := s.redisClient.GetToken(ctx, claims.UserID)
	if err != nil {
		// Log do erro, mas não falhar a validação se o Redis estiver indisponível
		fmt.Printf("Warning: failed to get token from Redis: %v\n", err)
	}

	// Se o token não está no Redis, pode ter sido revogado
	if cachedToken == nil {
		return nil, fmt.Errorf("token revogado ou expirado")
	}

	return claims, nil
}

// Logout revoga um token JWT
func (s *AuthService) Logout(ctx context.Context, userID uint) error {
	if err := s.redisClient.DeleteToken(ctx, userID); err != nil {
		return fmt.Errorf("erro ao revogar token: %w", err)
	}
	return nil
}
