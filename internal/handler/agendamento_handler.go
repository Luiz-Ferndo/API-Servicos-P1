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

// AgendamentoHandler gerencia requisições de agendamentos
type AgendamentoHandler struct {
	agendamentoService *service.AgendamentoService
}

// NewAgendamentoHandler cria um novo handler de agendamentos
func NewAgendamentoHandler(agendamentoService *service.AgendamentoService) *AgendamentoHandler {
	return &AgendamentoHandler{
		agendamentoService: agendamentoService,
	}
}

// Create cria um novo agendamento
// @Summary Criar agendamento
// @Description Cria um novo agendamento (CUSTOMER)
// @Tags agendamentos
// @Security BearerAuth
// @Accept json
// @Produce json
// @Param request body dto.CreateAgendamentoRequest true "Dados do agendamento"
// @Success 201 {object} dto.AgendamentoResponse
// @Failure 400 {object} map[string]interface{}
// @Router /agendamentos [post]
func (h *AgendamentoHandler) Create(c *gin.Context) {
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

	var req dto.CreateAgendamentoRequest
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

	response, err := h.agendamentoService.Create(c.Request.Context(), &req, userID.(uint))
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

// FindAll lista todos os agendamentos
// @Summary Listar todos agendamentos
// @Description Lista todos os agendamentos (admin)
// @Tags agendamentos
// @Security BearerAuth
// @Produce json
// @Success 200 {array} dto.AgendamentoResponse
// @Failure 401 {object} map[string]interface{}
// @Router /agendamentos [get]
func (h *AgendamentoHandler) FindAll(c *gin.Context) {
	agendamentos, err := h.agendamentoService.FindAll(c.Request.Context())
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"status":    http.StatusInternalServerError,
			"message":   err.Error(),
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.JSON(http.StatusOK, agendamentos)
}

// FindByCliente busca agendamentos de um cliente
// @Summary Buscar agendamentos por cliente
// @Description Retorna todos os agendamentos de um cliente
// @Tags agendamentos
// @Security BearerAuth
// @Produce json
// @Param id path int true "ID do cliente"
// @Success 200 {array} dto.AgendamentoResponse
// @Failure 404 {object} map[string]interface{}
// @Router /agendamentos/cliente/{id} [get]
func (h *AgendamentoHandler) FindByCliente(c *gin.Context) {
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

	agendamentos, err := h.agendamentoService.FindByCliente(c.Request.Context(), uint(id))
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"status":    http.StatusInternalServerError,
			"message":   err.Error(),
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.JSON(http.StatusOK, agendamentos)
}

// FindByPrestador busca agendamentos de um prestador
// @Summary Buscar agendamentos por prestador
// @Description Retorna todos os agendamentos de um prestador
// @Tags agendamentos
// @Security BearerAuth
// @Produce json
// @Param id path int true "ID do prestador"
// @Success 200 {array} dto.AgendamentoResponse
// @Failure 404 {object} map[string]interface{}
// @Router /agendamentos/prestador/{id} [get]
func (h *AgendamentoHandler) FindByPrestador(c *gin.Context) {
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

	agendamentos, err := h.agendamentoService.FindByPrestador(c.Request.Context(), uint(id))
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"status":    http.StatusInternalServerError,
			"message":   err.Error(),
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.JSON(http.StatusOK, agendamentos)
}

// UpdateStatus atualiza o status de um agendamento
// @Summary Atualizar status de agendamento
// @Description Atualiza o status de um agendamento
// @Tags agendamentos
// @Security BearerAuth
// @Accept json
// @Produce json
// @Param id path int true "ID do agendamento"
// @Param request body dto.UpdateStatusRequest true "Novo status"
// @Success 200 {object} dto.AgendamentoResponse
// @Failure 400 {object} map[string]interface{}
// @Router /agendamentos/{id}/status [put]
func (h *AgendamentoHandler) UpdateStatus(c *gin.Context) {
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

	var req dto.UpdateStatusRequest
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

	response, err := h.agendamentoService.UpdateStatus(c.Request.Context(), uint(id), &req)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"status":    http.StatusBadRequest,
			"message":   err.Error(),
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.JSON(http.StatusOK, response)
}
