package service

import (
	"context"
	"fmt"

	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/entity"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/enum"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/dto"
	pkgredis "github.com/Luiz-Ferndo/API-Servicos-P1/internal/pkg/redis"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/repository"
	"gorm.io/gorm"
)

// AgendamentoService gerencia operações de agendamentos
type AgendamentoService struct {
	agendamentoRepo *repository.AgendamentoRepository
	servicoRepo     *repository.ServicoRepository
	userRepo        *repository.UserRepository
	redisClient     *pkgredis.Client
}

// NewAgendamentoService cria um novo serviço de agendamentos
func NewAgendamentoService(
	agendamentoRepo *repository.AgendamentoRepository,
	servicoRepo *repository.ServicoRepository,
	userRepo *repository.UserRepository,
	redisClient *pkgredis.Client,
) *AgendamentoService {
	return &AgendamentoService{
		agendamentoRepo: agendamentoRepo,
		servicoRepo:     servicoRepo,
		userRepo:        userRepo,
		redisClient:     redisClient,
	}
}

// Create cria um novo agendamento
func (s *AgendamentoService) Create(ctx context.Context, req *dto.CreateAgendamentoRequest, clienteID uint) (*dto.AgendamentoResponse, error) {
	// Verificar se o prestador existe
	prestador, err := s.userRepo.FindByID(req.PrestadorID)
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, fmt.Errorf("prestador não encontrado")
		}
		return nil, fmt.Errorf("erro ao buscar prestador: %w", err)
	}

	// Verificar se o serviço existe
	servico, err := s.servicoRepo.FindByID(req.ServicoID)
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, fmt.Errorf("serviço não encontrado")
		}
		return nil, fmt.Errorf("erro ao buscar serviço: %w", err)
	}

	// Verificar se o prestador oferece o serviço
	prestadorServicos, err := s.servicoRepo.FindByPrestador(prestador.ID)
	if err != nil {
		return nil, fmt.Errorf("erro ao verificar serviços do prestador: %w", err)
	}

	servicoEncontrado := false
	for _, ps := range prestadorServicos {
		if ps.ID == servico.ID {
			servicoEncontrado = true
			break
		}
	}

	if !servicoEncontrado {
		return nil, fmt.Errorf("prestador não oferece este serviço")
	}

	// Verificar conflito de horário
	conflict, err := s.agendamentoRepo.CheckConflict(prestador.ID, req.DataHora.Format("2006-01-02 15:04:05"))
	if err != nil {
		return nil, fmt.Errorf("erro ao verificar conflito de horário: %w", err)
	}
	if conflict {
		return nil, fmt.Errorf("prestador já possui agendamento neste horário")
	}

	// Criar agendamento
	agendamento := &entity.Agendamento{
		ClienteID:   clienteID,
		PrestadorID: req.PrestadorID,
		ServicoID:   req.ServicoID,
		DataHora:    req.DataHora,
		Valor:       servico.Valor,
		Status:      enum.StatusAgendado,
	}

	if err := s.agendamentoRepo.Create(agendamento); err != nil {
		return nil, fmt.Errorf("erro ao criar agendamento: %w", err)
	}

	// Buscar agendamento completo com relacionamentos
	agendamentoCompleto, err := s.agendamentoRepo.FindByID(agendamento.ID)
	if err != nil {
		return nil, fmt.Errorf("erro ao buscar agendamento criado: %w", err)
	}

	// Invalidar cache relevante
	_ = s.redisClient.InvalidatePattern(ctx, "agendamentos:*")

	return s.toAgendamentoResponse(agendamentoCompleto), nil
}

// FindByID busca um agendamento por ID
func (s *AgendamentoService) FindByID(ctx context.Context, id uint) (*dto.AgendamentoResponse, error) {
	agendamento, err := s.agendamentoRepo.FindByID(id)
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, fmt.Errorf("agendamento não encontrado")
		}
		return nil, fmt.Errorf("erro ao buscar agendamento: %w", err)
	}

	return s.toAgendamentoResponse(agendamento), nil
}

// FindAll lista todos os agendamentos
func (s *AgendamentoService) FindAll(ctx context.Context) ([]dto.AgendamentoResponse, error) {
	agendamentos, err := s.agendamentoRepo.FindAll()
	if err != nil {
		return nil, fmt.Errorf("erro ao listar agendamentos: %w", err)
	}

	responses := make([]dto.AgendamentoResponse, len(agendamentos))
	for i, agendamento := range agendamentos {
		responses[i] = *s.toAgendamentoResponse(&agendamento)
	}

	return responses, nil
}

// FindByCliente busca agendamentos de um cliente
func (s *AgendamentoService) FindByCliente(ctx context.Context, clienteID uint) ([]dto.AgendamentoResponse, error) {
	agendamentos, err := s.agendamentoRepo.FindByCliente(clienteID)
	if err != nil {
		return nil, fmt.Errorf("erro ao buscar agendamentos do cliente: %w", err)
	}

	responses := make([]dto.AgendamentoResponse, len(agendamentos))
	for i, agendamento := range agendamentos {
		responses[i] = *s.toAgendamentoResponse(&agendamento)
	}

	return responses, nil
}

// FindByPrestador busca agendamentos de um prestador
func (s *AgendamentoService) FindByPrestador(ctx context.Context, prestadorID uint) ([]dto.AgendamentoResponse, error) {
	agendamentos, err := s.agendamentoRepo.FindByPrestador(prestadorID)
	if err != nil {
		return nil, fmt.Errorf("erro ao buscar agendamentos do prestador: %w", err)
	}

	responses := make([]dto.AgendamentoResponse, len(agendamentos))
	for i, agendamento := range agendamentos {
		responses[i] = *s.toAgendamentoResponse(&agendamento)
	}

	return responses, nil
}

// UpdateStatus atualiza o status de um agendamento
func (s *AgendamentoService) UpdateStatus(ctx context.Context, id uint, req *dto.UpdateStatusRequest) (*dto.AgendamentoResponse, error) {
	// Verificar se o agendamento existe
	agendamento, err := s.agendamentoRepo.FindByID(id)
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, fmt.Errorf("agendamento não encontrado")
		}
		return nil, fmt.Errorf("erro ao buscar agendamento: %w", err)
	}

	// Validar novo status
	novoStatus, err := enum.StatusAgendamentoFromString(req.Status)
	if err != nil {
		return nil, fmt.Errorf("status inválido: %w", err)
	}

	// Validar transição de status
	if agendamento.Status == enum.StatusFinalizado {
		return nil, fmt.Errorf("não é possível alterar status de agendamento finalizado")
	}

	// Se cancelando, motivo é obrigatório
	if novoStatus == enum.StatusCancelado && (req.Motivo == nil || *req.Motivo == "") {
		return nil, fmt.Errorf("motivo de cancelamento é obrigatório")
	}

	// Atualizar status
	if err := s.agendamentoRepo.UpdateStatus(id, novoStatus, req.Motivo); err != nil {
		return nil, fmt.Errorf("erro ao atualizar status: %w", err)
	}

	// Buscar agendamento atualizado
	agendamentoAtualizado, err := s.agendamentoRepo.FindByID(id)
	if err != nil {
		return nil, fmt.Errorf("erro ao buscar agendamento atualizado: %w", err)
	}

	// Invalidar cache relevante
	_ = s.redisClient.InvalidatePattern(ctx, "agendamentos:*")

	return s.toAgendamentoResponse(agendamentoAtualizado), nil
}

// toAgendamentoResponse converte uma entidade Agendamento para AgendamentoResponse
func (s *AgendamentoService) toAgendamentoResponse(agendamento *entity.Agendamento) *dto.AgendamentoResponse {
	response := &dto.AgendamentoResponse{
		ID:                 agendamento.ID,
		ClienteID:          agendamento.ClienteID,
		PrestadorID:        agendamento.PrestadorID,
		ServicoID:          agendamento.ServicoID,
		DataHora:           agendamento.DataHora,
		Valor:              agendamento.Valor,
		Status:             agendamento.Status.GetDescricao(),
		MotivoCancelamento: agendamento.MotivoCancelamento,
	}

	if agendamento.Cliente != nil {
		response.Cliente = &dto.UserSimpleResponse{
			ID:    agendamento.Cliente.ID,
			Name:  agendamento.Cliente.Name,
			Email: agendamento.Cliente.Email,
		}
	}

	if agendamento.Prestador != nil {
		response.Prestador = &dto.UserSimpleResponse{
			ID:    agendamento.Prestador.ID,
			Name:  agendamento.Prestador.Name,
			Email: agendamento.Prestador.Email,
		}
	}

	if agendamento.Servico != nil {
		response.Servico = &dto.ServicoResponse{
			ID:        agendamento.Servico.ID,
			Nome:      agendamento.Servico.Nome,
			Valor:     agendamento.Servico.Valor,
			Descricao: agendamento.Servico.Descricao,
			Ativo:     agendamento.Servico.Ativo,
		}
	}

	return response
}
