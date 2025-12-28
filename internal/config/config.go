package config

import (
	"context"
	"fmt"
	"log"
	"os"
	"strconv"
	"time"

	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/entity"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/domain/enum"
	"github.com/joho/godotenv"
	"github.com/redis/go-redis/v9"
	"golang.org/x/crypto/bcrypt"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"
)

// Config armazena as configurações da aplicação
type Config struct {
	Port                 string
	GinMode              string
	DBHost               string
	DBPort               string
	DBUser               string
	DBPassword           string
	DBName               string
	RedisHost            string
	RedisPort            string
	RedisPassword        string
	RedisDB              int
	JWTSecret            string
	JWTExpirationHours   int
	CORSAllowedOrigins   string
	AdminName            string
	AdminEmail           string
	AdminPassword        string
	DB                   *gorm.DB
	RedisClient          *redis.Client
}

// LoadConfig carrega as configurações do ambiente
func LoadConfig() (*Config, error) {
	// Tenta carregar o .env, mas não falha se não existir
	_ = godotenv.Load()

	redisDB, _ := strconv.Atoi(getEnv("REDIS_DB", "0"))
	jwtExpHours, _ := strconv.Atoi(getEnv("JWT_EXPIRATION_HOURS", "24"))

	cfg := &Config{
		Port:                 getEnv("PORT", "8080"),
		GinMode:              getEnv("GIN_MODE", "debug"),
		DBHost:               getEnv("DB_HOST", "localhost"),
		DBPort:               getEnv("DB_PORT", "5432"),
		DBUser:               getEnv("DB_USER", "postgres"),
		DBPassword:           getEnv("DB_PASSWORD", "postgres"),
		DBName:               getEnv("DB_NAME", "prestacao_servicos"),
		RedisHost:            getEnv("REDIS_HOST", "localhost"),
		RedisPort:            getEnv("REDIS_PORT", "6379"),
		RedisPassword:        getEnv("REDIS_PASSWORD", ""),
		RedisDB:              redisDB,
		JWTSecret:            getEnv("JWT_SECRET", "default-secret-change-me"),
		JWTExpirationHours:   jwtExpHours,
		CORSAllowedOrigins:   getEnv("CORS_ALLOWED_ORIGINS", "*"),
		AdminName:            getEnv("ADMIN_NAME", "Admin"),
		AdminEmail:           getEnv("ADMIN_EMAIL", "admin@admin.com"),
		AdminPassword:        getEnv("ADMIN_PASSWORD", "admin123"),
	}

	return cfg, nil
}

// InitDatabase inicializa a conexão com o banco de dados
func (c *Config) InitDatabase() error {
	dsn := fmt.Sprintf("host=%s user=%s password=%s dbname=%s port=%s sslmode=disable TimeZone=America/Sao_Paulo",
		c.DBHost, c.DBUser, c.DBPassword, c.DBName, c.DBPort)

	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{
		Logger: logger.Default.LogMode(logger.Info),
	})
	if err != nil {
		return fmt.Errorf("failed to connect to database: %w", err)
	}

	c.DB = db

	// Auto migrate das tabelas
	if err := c.autoMigrate(); err != nil {
		return fmt.Errorf("failed to migrate database: %w", err)
	}

	// Inicializar dados padrão
	if err := c.seedDatabase(); err != nil {
		return fmt.Errorf("failed to seed database: %w", err)
	}

	log.Println("Database connected and migrated successfully")
	return nil
}

// InitRedis inicializa a conexão com o Redis
func (c *Config) InitRedis() error {
	client := redis.NewClient(&redis.Options{
		Addr:     fmt.Sprintf("%s:%s", c.RedisHost, c.RedisPort),
		Password: c.RedisPassword,
		DB:       c.RedisDB,
	})

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	if err := client.Ping(ctx).Err(); err != nil {
		return fmt.Errorf("failed to connect to redis: %w", err)
	}

	c.RedisClient = client
	log.Println("Redis connected successfully")
	return nil
}

// autoMigrate executa as migrations do banco
func (c *Config) autoMigrate() error {
	return c.DB.AutoMigrate(
		&entity.Permission{},
		&entity.Role{},
		&entity.User{},
		&entity.UserPhone{},
		&entity.Servico{},
		&entity.Agendamento{},
	)
}

// seedDatabase inicializa os dados padrão do sistema
func (c *Config) seedDatabase() error {
	// Criar permissões
	permissions := []entity.Permission{
		{Name: enum.ManageUsers, Description: enum.ManageUsers.GetDescription()},
		{Name: enum.ManageServices, Description: enum.ManageServices.GetDescription()},
		{Name: enum.ViewReports, Description: enum.ViewReports.GetDescription()},
		{Name: enum.BookService, Description: enum.BookService.GetDescription()},
		{Name: enum.ViewAppointments, Description: enum.ViewAppointments.GetDescription()},
		{Name: enum.CancelAppointment, Description: enum.CancelAppointment.GetDescription()},
		{Name: enum.ConfirmExecution, Description: enum.ConfirmExecution.GetDescription()},
		{Name: enum.DefineAvailability, Description: enum.DefineAvailability.GetDescription()},
		{Name: enum.MakePayment, Description: enum.MakePayment.GetDescription()},
		{Name: enum.ViewServices, Description: enum.ViewServices.GetDescription()},
	}

	for _, perm := range permissions {
		var existing entity.Permission
		if err := c.DB.Where("nm_permission = ?", perm.Name).First(&existing).Error; err != nil {
			if err == gorm.ErrRecordNotFound {
				if err := c.DB.Create(&perm).Error; err != nil {
					return err
				}
			}
		}
	}

	// Criar roles
	var allPerms []entity.Permission
	c.DB.Find(&allPerms)

	// Admin role - todas as permissões
	var adminRole entity.Role
	if err := c.DB.Where("nm_role = ?", enum.RoleAdministrator).First(&adminRole).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			adminRole = entity.Role{
				Name:        enum.RoleAdministrator,
				Permissions: make([]*entity.Permission, len(allPerms)),
			}
			for i := range allPerms {
				adminRole.Permissions[i] = &allPerms[i]
			}
			if err := c.DB.Create(&adminRole).Error; err != nil {
				return err
			}
		}
	}

	// Customer role
	var customerRole entity.Role
	if err := c.DB.Where("nm_role = ?", enum.RoleCustomer).First(&customerRole).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			customerPerms := []*entity.Permission{}
			for i := range allPerms {
				if allPerms[i].Name == enum.BookService || 
				   allPerms[i].Name == enum.ViewAppointments || 
				   allPerms[i].Name == enum.CancelAppointment ||
				   allPerms[i].Name == enum.ViewServices ||
				   allPerms[i].Name == enum.MakePayment {
					customerPerms = append(customerPerms, &allPerms[i])
				}
			}
			customerRole = entity.Role{
				Name:        enum.RoleCustomer,
				Permissions: customerPerms,
			}
			if err := c.DB.Create(&customerRole).Error; err != nil {
				return err
			}
		}
	}

	// Service Provider role
	var providerRole entity.Role
	if err := c.DB.Where("nm_role = ?", enum.RoleServiceProvider).First(&providerRole).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			providerPerms := []*entity.Permission{}
			for i := range allPerms {
				if allPerms[i].Name == enum.ManageServices || 
				   allPerms[i].Name == enum.ViewAppointments || 
				   allPerms[i].Name == enum.ConfirmExecution ||
				   allPerms[i].Name == enum.DefineAvailability ||
				   allPerms[i].Name == enum.ViewServices {
					providerPerms = append(providerPerms, &allPerms[i])
				}
			}
			providerRole = entity.Role{
				Name:        enum.RoleServiceProvider,
				Permissions: providerPerms,
			}
			if err := c.DB.Create(&providerRole).Error; err != nil {
				return err
			}
		}
	}

	// Criar usuário admin se não existir
	var adminUser entity.User
	if err := c.DB.Where("ds_email = ?", c.AdminEmail).First(&adminUser).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			hashedPassword, err := bcrypt.GenerateFromPassword([]byte(c.AdminPassword), bcrypt.DefaultCost)
			if err != nil {
				return err
			}

			// Recarregar admin role com permissões
			c.DB.Preload("Permissions").Where("nm_role = ?", enum.RoleAdministrator).First(&adminRole)

			adminUser = entity.User{
				Name:     c.AdminName,
				Email:    c.AdminEmail,
				Password: string(hashedPassword),
				Roles:    []*entity.Role{&adminRole},
			}
			if err := c.DB.Create(&adminUser).Error; err != nil {
				return err
			}
			log.Printf("Admin user created: %s", c.AdminEmail)
		}
	}

	return nil
}

// Close fecha as conexões com banco e redis
func (c *Config) Close() error {
	if c.RedisClient != nil {
		if err := c.RedisClient.Close(); err != nil {
			return err
		}
	}

	if c.DB != nil {
		sqlDB, err := c.DB.DB()
		if err != nil {
			return err
		}
		return sqlDB.Close()
	}

	return nil
}

func getEnv(key, defaultValue string) string {
	if value := os.Getenv(key); value != "" {
		return value
	}
	return defaultValue
}
