package repository

import (
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/entity"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/enum"
	"gorm.io/gorm"
)

// RoleRepository gerencia operações de roles no banco
type RoleRepository struct {
	db *gorm.DB
}

// NewRoleRepository cria um novo repositório de roles
func NewRoleRepository(db *gorm.DB) *RoleRepository {
	return &RoleRepository{db: db}
}

// FindByName busca uma role por nome
func (r *RoleRepository) FindByName(name enum.RoleName) (*entity.Role, error) {
	var role entity.Role
	err := r.db.Preload("Permissions").Where("nm_role = ?", name).First(&role).Error
	if err != nil {
		return nil, err
	}
	return &role, nil
}

// FindAll lista todas as roles
func (r *RoleRepository) FindAll() ([]entity.Role, error) {
	var roles []entity.Role
	err := r.db.Preload("Permissions").Find(&roles).Error
	return roles, err
}
