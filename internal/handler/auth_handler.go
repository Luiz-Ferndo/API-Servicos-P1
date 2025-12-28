package handler

import (
	"fmt"
	"net/http"
	"time"

	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/dto"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/service"
	"github.com/gin-gonic/gin"
)

// AuthHandler gerencia requisições de autenticação
type AuthHandler struct {
	authService *service.AuthService
}

// NewAuthHandler cria um novo handler de autenticação
func NewAuthHandler(authService *service.AuthService) *AuthHandler {
	return &AuthHandler{
		authService: authService,
	}
}

// Login processa a requisição de login
// @Summary Login de usuário
// @Description Autentica um usuário e retorna um token JWT
// @Tags auth
// @Accept json
// @Produce json
// @Param request body dto.LoginRequest true "Credenciais de login"
// @Success 200 {object} dto.LoginResponse
// @Failure 400 {object} map[string]interface{}
// @Failure 401 {object} map[string]interface{}
// @Router /auth/login [post]
func (h *AuthHandler) Login(c *gin.Context) {
	var req dto.LoginRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"status":    http.StatusBadRequest,
			"message":   "Dados inválidos",
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
			"errors":    []string{err.Error()},
		})
		return
	}

	response, err := h.authService.Login(c.Request.Context(), &req)
	if err != nil {
		c.JSON(http.StatusUnauthorized, gin.H{
			"status":    http.StatusUnauthorized,
			"message":   err.Error(),
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.JSON(http.StatusOK, response)
}

// Logout revoga o token do usuário
// @Summary Logout de usuário
// @Description Revoga o token JWT do usuário
// @Tags auth
// @Security BearerAuth
// @Success 200 {object} map[string]interface{}
// @Failure 401 {object} map[string]interface{}
// @Router /auth/logout [post]
func (h *AuthHandler) Logout(c *gin.Context) {
	userID, exists := c.Get("user_id")
	if !exists {
		c.JSON(http.StatusUnauthorized, gin.H{
			"status":    http.StatusUnauthorized,
			"message":   "Não autenticado",
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	if err := h.authService.Logout(c.Request.Context(), userID.(uint)); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"status":    http.StatusInternalServerError,
			"message":   "Erro ao fazer logout",
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"message": "Logout realizado com sucesso",
	})
}
