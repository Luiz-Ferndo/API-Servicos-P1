package middleware

import (
	"fmt"
	"net/http"
	"strings"
	"time"

	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/service"
	"github.com/gin-gonic/gin"
)

// AuthMiddleware middleware para autenticação JWT
type AuthMiddleware struct {
	authService *service.AuthService
}

// NewAuthMiddleware cria um novo middleware de autenticação
func NewAuthMiddleware(authService *service.AuthService) *AuthMiddleware {
	return &AuthMiddleware{
		authService: authService,
	}
}

// Authenticate middleware que valida o token JWT
func (m *AuthMiddleware) Authenticate() gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")
		if authHeader == "" {
			c.JSON(http.StatusUnauthorized, gin.H{
				"status":    http.StatusUnauthorized,
				"message":   "Token de autenticação não fornecido",
				"path":      c.Request.URL.Path,
				"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
			})
			c.Abort()
			return
		}

		// Extrair token do header "Bearer <token>"
		parts := strings.Split(authHeader, " ")
		if len(parts) != 2 || parts[0] != "Bearer" {
			c.JSON(http.StatusUnauthorized, gin.H{
				"status":    http.StatusUnauthorized,
				"message":   "Formato de token inválido",
				"path":      c.Request.URL.Path,
				"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
			})
			c.Abort()
			return
		}

		token := parts[1]

		// Validar token
		claims, err := m.authService.ValidateToken(c.Request.Context(), token)
		if err != nil {
			c.JSON(http.StatusUnauthorized, gin.H{
				"status":    http.StatusUnauthorized,
				"message":   "Token inválido ou expirado",
				"path":      c.Request.URL.Path,
				"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
			})
			c.Abort()
			return
		}

		// Armazenar claims no contexto
		c.Set("user_id", claims.UserID)
		c.Set("user_email", claims.Email)
		c.Set("user_roles", claims.Roles)

		c.Next()
	}
}

// RequireRole middleware que verifica se o usuário tem uma role específica
func (m *AuthMiddleware) RequireRole(requiredRole string) gin.HandlerFunc {
	return func(c *gin.Context) {
		roles, exists := c.Get("user_roles")
		if !exists {
			c.JSON(http.StatusForbidden, gin.H{
				"status":    http.StatusForbidden,
				"message":   "Acesso negado",
				"path":      c.Request.URL.Path,
				"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
			})
			c.Abort()
			return
		}

		userRoles, ok := roles.([]string)
		if !ok {
			c.JSON(http.StatusForbidden, gin.H{
				"status":    http.StatusForbidden,
				"message":   "Acesso negado",
				"path":      c.Request.URL.Path,
				"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
			})
			c.Abort()
			return
		}

		hasRole := false
		for _, role := range userRoles {
			if role == requiredRole {
				hasRole = true
				break
			}
		}

		if !hasRole {
			c.JSON(http.StatusForbidden, gin.H{
				"status":    http.StatusForbidden,
				"message":   "Você não tem permissão para acessar este recurso",
				"path":      c.Request.URL.Path,
				"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
			})
			c.Abort()
			return
		}

		c.Next()
	}
}
