package repository

import (
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/entity"
	"gorm.io/gorm"
)

// ServicoRepository gerencia operações de serviços no banco
type ServicoRepository struct {
	db *gorm.DB
}

// NewServicoRepository cria um novo repositório de serviços
func NewServicoRepository(db *gorm.DB) *ServicoRepository {
	return &ServicoRepository{db: db}
}

// Create cria um novo serviço
func (r *ServicoRepository) Create(servico *entity.Servico) error {
	return r.db.Create(servico).Error
}

// FindByID busca um serviço por ID
func (r *ServicoRepository) FindByID(id uint) (*entity.Servico, error) {
	var servico entity.Servico
	err := r.db.First(&servico, id).Error
	if err != nil {
		return nil, err
	}
	return &servico, nil
}

// FindAll lista todos os serviços
func (r *ServicoRepository) FindAll() ([]entity.Servico, error) {
	var servicos []entity.Servico
	err := r.db.Where("st_ativo = ?", true).Find(&servicos).Error
	return servicos, err
}

// FindAllIncludingInactive lista todos os serviços incluindo inativos
func (r *ServicoRepository) FindAllIncludingInactive() ([]entity.Servico, error) {
	var servicos []entity.Servico
	err := r.db.Find(&servicos).Error
	return servicos, err
}

// Update atualiza um serviço
func (r *ServicoRepository) Update(servico *entity.Servico) error {
	return r.db.Save(servico).Error
}

// Delete deleta um serviço (soft delete marcando como inativo)
func (r *ServicoRepository) Delete(id uint) error {
	return r.db.Model(&entity.Servico{}).Where("cd_servico = ?", id).Update("st_ativo", false).Error
}

// HardDelete deleta permanentemente um serviço
func (r *ServicoRepository) HardDelete(id uint) error {
	return r.db.Delete(&entity.Servico{}, id).Error
}

// FindByPrestador busca serviços oferecidos por um prestador
func (r *ServicoRepository) FindByPrestador(prestadorID uint) ([]entity.Servico, error) {
	var servicos []entity.Servico
	err := r.db.Joins("JOIN prestador_servicos ON prestador_servicos.cd_servico = servico.cd_servico").
		Where("prestador_servicos.cd_user = ? AND servico.st_ativo = ?", prestadorID, true).
		Find(&servicos).Error
	return servicos, err
}
