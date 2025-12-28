package handler

import (
	"fmt"
	"net/http"
	"strconv"
	"time"

	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/dto"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/service"
	"github.com/gin-gonic/gin"
)

// UserHandler gerencia requisições de usuários
type UserHandler struct {
	userService *service.UserService
}

// NewUserHandler cria um novo handler de usuários
func NewUserHandler(userService *service.UserService) *UserHandler {
	return &UserHandler{
		userService: userService,
	}
}

// Create cria um novo usuário
// @Summary Criar usuário
// @Description Registra um novo usuário no sistema
// @Tags users
// @Accept json
// @Produce json
// @Param request body dto.CreateUserRequest true "Dados do usuário"
// @Success 201 {object} dto.UserResponse
// @Failure 400 {object} map[string]interface{}
// @Router /users [post]
func (h *UserHandler) Create(c *gin.Context) {
	var req dto.CreateUserRequest
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

	response, err := h.userService.Create(c.Request.Context(), &req)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"status":    http.StatusBadRequest,
			"message":   err.Error(),
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.JSON(http.StatusCreated, response)
}

// FindAll lista todos os usuários
// @Summary Listar usuários
// @Description Lista todos os usuários cadastrados
// @Tags users
// @Security BearerAuth
// @Produce json
// @Success 200 {array} dto.UserResponse
// @Failure 401 {object} map[string]interface{}
// @Router /users [get]
func (h *UserHandler) FindAll(c *gin.Context) {
	users, err := h.userService.FindAll(c.Request.Context())
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"status":    http.StatusInternalServerError,
			"message":   err.Error(),
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.JSON(http.StatusOK, users)
}

// FindByID busca um usuário por ID
// @Summary Buscar usuário por ID
// @Description Retorna os dados de um usuário específico
// @Tags users
// @Security BearerAuth
// @Produce json
// @Param id path int true "ID do usuário"
// @Success 200 {object} dto.UserResponse
// @Failure 404 {object} map[string]interface{}
// @Router /users/{id} [get]
func (h *UserHandler) FindByID(c *gin.Context) {
	id, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"status":    http.StatusBadRequest,
			"message":   "ID inválido",
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	user, err := h.userService.FindByID(c.Request.Context(), uint(id))
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{
			"status":    http.StatusNotFound,
			"message":   err.Error(),
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.JSON(http.StatusOK, user)
}

// FindByEmail busca um usuário por email
// @Summary Buscar usuário por email
// @Description Retorna os dados de um usuário com o email especificado
// @Tags users
// @Security BearerAuth
// @Produce json
// @Param email query string true "Email do usuário"
// @Success 200 {object} dto.UserResponse
// @Failure 404 {object} map[string]interface{}
// @Router /users/search [get]
func (h *UserHandler) FindByEmail(c *gin.Context) {
	email := c.Query("email")
	if email == "" {
		c.JSON(http.StatusBadRequest, gin.H{
			"status":    http.StatusBadRequest,
			"message":   "Email é obrigatório",
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	user, err := h.userService.FindByEmail(c.Request.Context(), email)
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{
			"status":    http.StatusNotFound,
			"message":   err.Error(),
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.JSON(http.StatusOK, user)
}

// Update atualiza um usuário
// @Summary Atualizar usuário
// @Description Atualiza os dados de um usuário
// @Tags users
// @Security BearerAuth
// @Accept json
// @Produce json
// @Param id path int true "ID do usuário"
// @Param request body dto.UpdateUserRequest true "Dados a serem atualizados"
// @Success 200 {object} dto.UserResponse
// @Failure 400 {object} map[string]interface{}
// @Router /users/{id} [put]
func (h *UserHandler) Update(c *gin.Context) {
	id, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"status":    http.StatusBadRequest,
			"message":   "ID inválido",
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	var req dto.UpdateUserRequest
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

	user, err := h.userService.Update(c.Request.Context(), uint(id), &req)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"status":    http.StatusBadRequest,
			"message":   err.Error(),
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.JSON(http.StatusOK, user)
}

// Delete deleta um usuário
// @Summary Deletar usuário
// @Description Remove um usuário do sistema
// @Tags users
// @Security BearerAuth
// @Param id path int true "ID do usuário"
// @Success 204
// @Failure 404 {object} map[string]interface{}
// @Router /users/{id} [delete]
func (h *UserHandler) Delete(c *gin.Context) {
	id, err := strconv.ParseUint(c.Param("id"), 10, 32)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"status":    http.StatusBadRequest,
			"message":   "ID inválido",
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	if err := h.userService.Delete(c.Request.Context(), uint(id)); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"status":    http.StatusInternalServerError,
			"message":   err.Error(),
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.Status(http.StatusNoContent)
}
