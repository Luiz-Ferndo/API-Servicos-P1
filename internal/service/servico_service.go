package service

import (
	"context"
	"fmt"
	"time"

	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/entity"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/dto"
	pkgredis "github.com/Luiz-Ferndo/API-Servicos-P1/internal/pkg/redis"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/repository"
	"gorm.io/gorm"
)

// ServicoService gerencia operações de serviços
type ServicoService struct {
	servicoRepo *repository.ServicoRepository
	userRepo    *repository.UserRepository
	redisClient *pkgredis.Client
}

// NewServicoService cria um novo serviço de serviços
func NewServicoService(servicoRepo *repository.ServicoRepository, userRepo *repository.UserRepository, redisClient *pkgredis.Client) *ServicoService {
	return &ServicoService{
		servicoRepo: servicoRepo,
		userRepo:    userRepo,
		redisClient: redisClient,
	}
}

// Create cria um novo serviço e associa ao prestador
func (s *ServicoService) Create(ctx context.Context, req *dto.CreateServicoRequest, prestadorID uint) (*dto.ServicoResponse, error) {
	// Verificar se o usuário existe e é um prestador
	user, err := s.userRepo.FindByID(prestadorID)
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, fmt.Errorf("usuário não encontrado")
		}
		return nil, fmt.Errorf("erro ao buscar usuário: %w", err)
	}

	// Verificar se tem a role de prestador
	isPrestador := false
	for _, role := range user.Roles {
		if role.Name.String() == "ROLE_SERVICE_PROVIDER" {
			isPrestador = true
			break
		}
	}
	if !isPrestador {
		return nil, fmt.Errorf("usuário não é um prestador de serviço")
	}

	// Criar serviço
	servico := &entity.Servico{
		Nome:      req.Nome,
		Valor:     req.Valor,
		Descricao: req.Descricao,
		Ativo:     true,
	}

	if err := s.servicoRepo.Create(servico); err != nil {
		return nil, fmt.Errorf("erro ao criar serviço: %w", err)
	}

	// Associar serviço ao prestador
	if err := s.userRepo.AddServico(prestadorID, servico.ID); err != nil {
		return nil, fmt.Errorf("erro ao associar serviço ao prestador: %w", err)
	}

	// Invalidar cache de serviços
	_ = s.redisClient.DeleteCache(ctx, "servicos:all")

	return s.toServicoResponse(servico), nil
}

// FindByID busca um serviço por ID
func (s *ServicoService) FindByID(ctx context.Context, id uint) (*dto.ServicoResponse, error) {
	// Tentar buscar do cache
	cacheKey := fmt.Sprintf("servico:%d", id)
	var cachedServico dto.ServicoResponse
	if err := s.redisClient.GetCache(ctx, cacheKey, &cachedServico); err == nil && cachedServico.ID > 0 {
		return &cachedServico, nil
	}

	servico, err := s.servicoRepo.FindByID(id)
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, fmt.Errorf("serviço não encontrado")
		}
		return nil, fmt.Errorf("erro ao buscar serviço: %w", err)
	}

	response := s.toServicoResponse(servico)

	// Cachear o resultado
	_ = s.redisClient.SetCache(ctx, cacheKey, response, 600*time.Second) // 10 minutos

	return response, nil
}

// FindAll lista todos os serviços ativos
func (s *ServicoService) FindAll(ctx context.Context) ([]dto.ServicoResponse, error) {
	// Tentar buscar do cache
	cacheKey := "servicos:all"
	var cachedServicos []dto.ServicoResponse
	if err := s.redisClient.GetCache(ctx, cacheKey, &cachedServicos); err == nil && len(cachedServicos) > 0 {
		return cachedServicos, nil
	}

	servicos, err := s.servicoRepo.FindAll()
	if err != nil {
		return nil, fmt.Errorf("erro ao listar serviços: %w", err)
	}

	responses := make([]dto.ServicoResponse, len(servicos))
	for i, servico := range servicos {
		responses[i] = *s.toServicoResponse(&servico)
	}

	// Cachear o resultado
	_ = s.redisClient.SetCache(ctx, cacheKey, responses, 300*time.Second) // 5 minutos

	return responses, nil
}

// Update atualiza um serviço
func (s *ServicoService) Update(ctx context.Context, id uint, req *dto.UpdateServicoRequest) (*dto.ServicoResponse, error) {
	servico, err := s.servicoRepo.FindByID(id)
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, fmt.Errorf("serviço não encontrado")
		}
		return nil, fmt.Errorf("erro ao buscar serviço: %w", err)
	}

	// Atualizar campos se fornecidos
	if req.Nome != "" {
		servico.Nome = req.Nome
	}
	if req.Valor > 0 {
		servico.Valor = req.Valor
	}
	if req.Descricao != "" {
		servico.Descricao = req.Descricao
	}
	if req.Ativo != nil {
		servico.Ativo = *req.Ativo
	}

	if err := s.servicoRepo.Update(servico); err != nil {
		return nil, fmt.Errorf("erro ao atualizar serviço: %w", err)
	}

	// Invalidar cache
	_ = s.redisClient.DeleteCache(ctx, fmt.Sprintf("servico:%d", id))
	_ = s.redisClient.DeleteCache(ctx, "servicos:all")

	return s.toServicoResponse(servico), nil
}

// Delete deleta um serviço (soft delete)
func (s *ServicoService) Delete(ctx context.Context, id uint, prestadorID uint) error {
	// Verificar se o serviço existe
	servico, err := s.servicoRepo.FindByID(id)
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return fmt.Errorf("serviço não encontrado")
		}
		return fmt.Errorf("erro ao buscar serviço: %w", err)
	}

	// Verificar se o prestador está associado ao serviço
	prestadorServicos, err := s.servicoRepo.FindByPrestador(prestadorID)
	if err != nil {
		return fmt.Errorf("erro ao verificar serviços do prestador: %w", err)
	}

	found := false
	for _, ps := range prestadorServicos {
		if ps.ID == servico.ID {
			found = true
			break
		}
	}

	if !found {
		return fmt.Errorf("serviço não pertence ao prestador")
	}

	// Soft delete
	if err := s.servicoRepo.Delete(id); err != nil {
		return fmt.Errorf("erro ao deletar serviço: %w", err)
	}

	// Invalidar cache
	_ = s.redisClient.DeleteCache(ctx, fmt.Sprintf("servico:%d", id))
	_ = s.redisClient.DeleteCache(ctx, "servicos:all")

	return nil
}

// toServicoResponse converte uma entidade Servico para ServicoResponse
func (s *ServicoService) toServicoResponse(servico *entity.Servico) *dto.ServicoResponse {
	return &dto.ServicoResponse{
		ID:        servico.ID,
		Nome:      servico.Nome,
		Valor:     servico.Valor,
		Descricao: servico.Descricao,
		Ativo:     servico.Ativo,
	}
}
