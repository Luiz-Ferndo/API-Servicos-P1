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

// ServicoHandler gerencia requisições de serviços
type ServicoHandler struct {
	servicoService *service.ServicoService
}

// NewServicoHandler cria um novo handler de serviços
func NewServicoHandler(servicoService *service.ServicoService) *ServicoHandler {
	return &ServicoHandler{
		servicoService: servicoService,
	}
}

// Create cria um novo serviço
// @Summary Criar serviço
// @Description Cadastra um novo serviço (apenas SERVICE_PROVIDER)
// @Tags servicos
// @Security BearerAuth
// @Accept json
// @Produce json
// @Param request body dto.CreateServicoRequest true "Dados do serviço"
// @Success 201 {object} dto.ServicoResponse
// @Failure 400 {object} map[string]interface{}
// @Router /servicos [post]
func (h *ServicoHandler) Create(c *gin.Context) {
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

	var req dto.CreateServicoRequest
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

	response, err := h.servicoService.Create(c.Request.Context(), &req, userID.(uint))
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

// FindAll lista todos os serviços
// @Summary Listar serviços
// @Description Lista todos os serviços disponíveis
// @Tags servicos
// @Security BearerAuth
// @Produce json
// @Success 200 {array} dto.ServicoResponse
// @Failure 401 {object} map[string]interface{}
// @Router /servicos [get]
func (h *ServicoHandler) FindAll(c *gin.Context) {
	servicos, err := h.servicoService.FindAll(c.Request.Context())
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"status":    http.StatusInternalServerError,
			"message":   err.Error(),
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.JSON(http.StatusOK, servicos)
}

// FindByID busca um serviço por ID
// @Summary Buscar serviço por ID
// @Description Retorna os dados de um serviço específico
// @Tags servicos
// @Security BearerAuth
// @Produce json
// @Param id path int true "ID do serviço"
// @Success 200 {object} dto.ServicoResponse
// @Failure 404 {object} map[string]interface{}
// @Router /servicos/{id} [get]
func (h *ServicoHandler) FindByID(c *gin.Context) {
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

	servico, err := h.servicoService.FindByID(c.Request.Context(), uint(id))
	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{
			"status":    http.StatusNotFound,
			"message":   err.Error(),
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.JSON(http.StatusOK, servico)
}

// Delete deleta um serviço
// @Summary Deletar serviço
// @Description Remove um serviço do sistema (apenas SERVICE_PROVIDER)
// @Tags servicos
// @Security BearerAuth
// @Param id path int true "ID do serviço"
// @Success 204
// @Failure 404 {object} map[string]interface{}
// @Router /servicos/{id} [delete]
func (h *ServicoHandler) Delete(c *gin.Context) {
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

	if err := h.servicoService.Delete(c.Request.Context(), uint(id), userID.(uint)); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"status":    http.StatusBadRequest,
			"message":   err.Error(),
			"path":      c.Request.URL.Path,
			"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
		})
		return
	}

	c.Status(http.StatusNoContent)
}
