package service

import (
	"context"
	"fmt"

	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/entity"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/enum"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/dto"
	pkgredis "github.com/Luiz-Ferndo/API-Servicos-P1/internal/pkg/redis"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/repository"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/gorm"
)

// UserService gerencia operações de usuários
type UserService struct {
	userRepo    *repository.UserRepository
	roleRepo    *repository.RoleRepository
	redisClient *pkgredis.Client
}

// NewUserService cria um novo serviço de usuários
func NewUserService(userRepo *repository.UserRepository, roleRepo *repository.RoleRepository, redisClient *pkgredis.Client) *UserService {
	return &UserService{
		userRepo:    userRepo,
		roleRepo:    roleRepo,
		redisClient: redisClient,
	}
}

// Create cria um novo usuário
func (s *UserService) Create(ctx context.Context, req *dto.CreateUserRequest) (*dto.UserResponse, error) {
	// Verificar se o email já existe
	existingUser, err := s.userRepo.FindByEmail(req.Email)
	if err == nil && existingUser != nil {
		return nil, fmt.Errorf("email já cadastrado")
	}

	// Hash da senha
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(req.Password), bcrypt.DefaultCost)
	if err != nil {
		return nil, fmt.Errorf("erro ao processar senha: %w", err)
	}

	// Buscar role
	roleName, err := enum.RoleNameFromString(req.Role)
	if err != nil {
		return nil, fmt.Errorf("role inválida: %w", err)
	}

	role, err := s.roleRepo.FindByName(roleName)
	if err != nil {
		return nil, fmt.Errorf("role não encontrada: %w", err)
	}

	// Criar usuário
	user := &entity.User{
		Name:     req.Name,
		Email:    req.Email,
		Password: string(hashedPassword),
		Roles:    []*entity.Role{role},
	}

	// Adicionar telefones se fornecidos
	if len(req.Phones) > 0 {
		for _, phoneDTO := range req.Phones {
			phoneType, err := enum.PhoneTypeFromString(phoneDTO.Type)
			if err != nil {
				return nil, fmt.Errorf("tipo de telefone inválido: %w", err)
			}
			user.Phones = append(user.Phones, entity.UserPhone{
				Number: phoneDTO.Number,
				Type:   phoneType,
			})
		}
	}

	if err := s.userRepo.Create(user); err != nil {
		return nil, fmt.Errorf("erro ao criar usuário: %w", err)
	}

	return s.toUserResponse(user), nil
}

// FindByID busca um usuário por ID
func (s *UserService) FindByID(ctx context.Context, id uint) (*dto.UserResponse, error) {
	// Tentar buscar do cache
	cacheKey := fmt.Sprintf("user:%d", id)
	var cachedUser dto.UserResponse
	if err := s.redisClient.GetCache(ctx, cacheKey, &cachedUser); err == nil && cachedUser.ID > 0 {
		return &cachedUser, nil
	}

	user, err := s.userRepo.FindByID(id)
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, fmt.Errorf("usuário não encontrado")
		}
		return nil, fmt.Errorf("erro ao buscar usuário: %w", err)
	}

	response := s.toUserResponse(user)

	// Cachear o resultado
	_ = s.redisClient.SetCache(ctx, cacheKey, response, 300) // 5 minutos

	return response, nil
}

// FindByEmail busca um usuário por email
func (s *UserService) FindByEmail(ctx context.Context, email string) (*dto.UserResponse, error) {
	user, err := s.userRepo.FindByEmail(email)
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, fmt.Errorf("usuário não encontrado")
		}
		return nil, fmt.Errorf("erro ao buscar usuário: %w", err)
	}

	return s.toUserResponse(user), nil
}

// FindAll lista todos os usuários
func (s *UserService) FindAll(ctx context.Context) ([]dto.UserResponse, error) {
	users, err := s.userRepo.FindAll()
	if err != nil {
		return nil, fmt.Errorf("erro ao listar usuários: %w", err)
	}

	responses := make([]dto.UserResponse, len(users))
	for i, user := range users {
		responses[i] = *s.toUserResponse(&user)
	}

	return responses, nil
}

// Update atualiza um usuário
func (s *UserService) Update(ctx context.Context, id uint, req *dto.UpdateUserRequest) (*dto.UserResponse, error) {
	user, err := s.userRepo.FindByID(id)
	if err != nil {
		if err == gorm.ErrRecordNotFound {
			return nil, fmt.Errorf("usuário não encontrado")
		}
		return nil, fmt.Errorf("erro ao buscar usuário: %w", err)
	}

	// Atualizar campos se fornecidos
	if req.Name != "" {
		user.Name = req.Name
	}

	if req.Email != "" && req.Email != user.Email {
		// Verificar se o novo email já existe
		existingUser, err := s.userRepo.FindByEmail(req.Email)
		if err == nil && existingUser != nil && existingUser.ID != id {
			return nil, fmt.Errorf("email já cadastrado")
		}
		user.Email = req.Email
	}

	// Atualizar telefones se fornecidos
	if len(req.Phones) > 0 {
		user.Phones = []entity.UserPhone{}
		for _, phoneDTO := range req.Phones {
			phoneType, err := enum.PhoneTypeFromString(phoneDTO.Type)
			if err != nil {
				return nil, fmt.Errorf("tipo de telefone inválido: %w", err)
			}
			user.Phones = append(user.Phones, entity.UserPhone{
				UserID: user.ID,
				Number: phoneDTO.Number,
				Type:   phoneType,
			})
		}
	}

	if err := s.userRepo.Update(user); err != nil {
		return nil, fmt.Errorf("erro ao atualizar usuário: %w", err)
	}

	// Invalidar cache
	cacheKey := fmt.Sprintf("user:%d", id)
	_ = s.redisClient.DeleteCache(ctx, cacheKey)

	return s.toUserResponse(user), nil
}

// Delete deleta um usuário
func (s *UserService) Delete(ctx context.Context, id uint) error {
	if err := s.userRepo.Delete(id); err != nil {
		return fmt.Errorf("erro ao deletar usuário: %w", err)
	}

	// Invalidar cache
	cacheKey := fmt.Sprintf("user:%d", id)
	_ = s.redisClient.DeleteCache(ctx, cacheKey)

	return nil
}

// toUserResponse converte uma entidade User para UserResponse
func (s *UserService) toUserResponse(user *entity.User) *dto.UserResponse {
	response := &dto.UserResponse{
		ID:    user.ID,
		Name:  user.Name,
		Email: user.Email,
	}

	if len(user.Roles) > 0 {
		response.Roles = make([]string, len(user.Roles))
		for i, role := range user.Roles {
			response.Roles[i] = role.Name.String()
		}
	}

	if len(user.Phones) > 0 {
		response.Phones = make([]dto.PhoneDTO, len(user.Phones))
		for i, phone := range user.Phones {
			response.Phones[i] = dto.PhoneDTO{
				Number: phone.Number,
				Type:   phone.Type.String(),
			}
		}
	}

	return response
}
