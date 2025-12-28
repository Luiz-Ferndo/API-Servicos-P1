package repository

import (
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/entity"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/enum"
	"gorm.io/gorm"
)

// AgendamentoRepository gerencia operações de agendamentos no banco
type AgendamentoRepository struct {
	db *gorm.DB
}

// NewAgendamentoRepository cria um novo repositório de agendamentos
func NewAgendamentoRepository(db *gorm.DB) *AgendamentoRepository {
	return &AgendamentoRepository{db: db}
}

// Create cria um novo agendamento
func (r *AgendamentoRepository) Create(agendamento *entity.Agendamento) error {
	return r.db.Create(agendamento).Error
}

// FindByID busca um agendamento por ID
func (r *AgendamentoRepository) FindByID(id uint) (*entity.Agendamento, error) {
	var agendamento entity.Agendamento
	err := r.db.Preload("Cliente").Preload("Prestador").Preload("Servico").First(&agendamento, id).Error
	if err != nil {
		return nil, err
	}
	return &agendamento, nil
}

// FindAll lista todos os agendamentos
func (r *AgendamentoRepository) FindAll() ([]entity.Agendamento, error) {
	var agendamentos []entity.Agendamento
	err := r.db.Preload("Cliente").Preload("Prestador").Preload("Servico").Find(&agendamentos).Error
	return agendamentos, err
}

// FindByCliente busca agendamentos de um cliente
func (r *AgendamentoRepository) FindByCliente(clienteID uint) ([]entity.Agendamento, error) {
	var agendamentos []entity.Agendamento
	err := r.db.Preload("Cliente").Preload("Prestador").Preload("Servico").
		Where("cd_cliente_user = ?", clienteID).Find(&agendamentos).Error
	return agendamentos, err
}

// FindByPrestador busca agendamentos de um prestador
func (r *AgendamentoRepository) FindByPrestador(prestadorID uint) ([]entity.Agendamento, error) {
	var agendamentos []entity.Agendamento
	err := r.db.Preload("Cliente").Preload("Prestador").Preload("Servico").
		Where("cd_prestador_user = ?", prestadorID).Find(&agendamentos).Error
	return agendamentos, err
}

// Update atualiza um agendamento
func (r *AgendamentoRepository) Update(agendamento *entity.Agendamento) error {
	return r.db.Save(agendamento).Error
}

// UpdateStatus atualiza o status de um agendamento
func (r *AgendamentoRepository) UpdateStatus(id uint, status enum.StatusAgendamento, motivo *string) error {
	updates := map[string]interface{}{
		"ds_status": status,
	}
	if motivo != nil {
		updates["ds_motivo_cancelamento"] = *motivo
	}
	return r.db.Model(&entity.Agendamento{}).Where("cd_agendamento = ?", id).Updates(updates).Error
}

// Delete deleta um agendamento
func (r *AgendamentoRepository) Delete(id uint) error {
	return r.db.Delete(&entity.Agendamento{}, id).Error
}

// CheckConflict verifica se já existe um agendamento conflitante
func (r *AgendamentoRepository) CheckConflict(prestadorID uint, dataHora string) (bool, error) {
	var count int64
	err := r.db.Model(&entity.Agendamento{}).
		Where("cd_prestador_user = ? AND dt_agendamento = ? AND ds_status != ?", 
			prestadorID, dataHora, enum.StatusCancelado).
		Count(&count).Error
	return count > 0, err
}
