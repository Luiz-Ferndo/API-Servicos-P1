package repository

import (
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/entity"
	"gorm.io/gorm"
)

// UserRepository gerencia operações de usuários no banco
type UserRepository struct {
	db *gorm.DB
}

// NewUserRepository cria um novo repositório de usuários
func NewUserRepository(db *gorm.DB) *UserRepository {
	return &UserRepository{db: db}
}

// Create cria um novo usuário
func (r *UserRepository) Create(user *entity.User) error {
	return r.db.Create(user).Error
}

// FindByID busca um usuário por ID
func (r *UserRepository) FindByID(id uint) (*entity.User, error) {
	var user entity.User
	err := r.db.Preload("Roles.Permissions").Preload("Phones").Preload("ServicosOferecidos").First(&user, id).Error
	if err != nil {
		return nil, err
	}
	return &user, nil
}

// FindByEmail busca um usuário por email
func (r *UserRepository) FindByEmail(email string) (*entity.User, error) {
	var user entity.User
	err := r.db.Preload("Roles.Permissions").Preload("Phones").Where("ds_email = ?", email).First(&user).Error
	if err != nil {
		return nil, err
	}
	return &user, nil
}

// FindAll lista todos os usuários
func (r *UserRepository) FindAll() ([]entity.User, error) {
	var users []entity.User
	err := r.db.Preload("Roles").Preload("Phones").Find(&users).Error
	return users, err
}

// Update atualiza um usuário
func (r *UserRepository) Update(user *entity.User) error {
	return r.db.Save(user).Error
}

// Delete deleta um usuário
func (r *UserRepository) Delete(id uint) error {
	return r.db.Delete(&entity.User{}, id).Error
}

// AddServico adiciona um serviço ao usuário (prestador)
func (r *UserRepository) AddServico(userID uint, servicoID uint) error {
	var user entity.User
	if err := r.db.First(&user, userID).Error; err != nil {
		return err
	}

	var servico entity.Servico
	if err := r.db.First(&servico, servicoID).Error; err != nil {
		return err
	}

	return r.db.Model(&user).Association("ServicosOferecidos").Append(&servico)
}

// RemoveServico remove um serviço do usuário (prestador)
func (r *UserRepository) RemoveServico(userID uint, servicoID uint) error {
	var user entity.User
	if err := r.db.First(&user, userID).Error; err != nil {
		return err
	}

	var servico entity.Servico
	if err := r.db.First(&servico, servicoID).Error; err != nil {
		return err
	}

	return r.db.Model(&user).Association("ServicosOferecidos").Delete(&servico)
}
