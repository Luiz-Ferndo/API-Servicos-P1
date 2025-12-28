package main

import (
	"fmt"
	"log"
	"os"
	"os/signal"
	"syscall"

	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/config"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/handler"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/middleware"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/pkg/jwt"
	pkgredis "github.com/Luiz-Ferndo/API-Servicos-P1/internal/pkg/redis"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/repository"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/router"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/service"
	"github.com/gin-gonic/gin"
)

func main() {
	// Carregar configurações
	cfg, err := config.LoadConfig()
	if err != nil {
		log.Fatalf("Failed to load config: %v", err)
	}

	// Configurar modo do Gin
	gin.SetMode(cfg.GinMode)

	// Inicializar banco de dados
	if err := cfg.InitDatabase(); err != nil {
		log.Fatalf("Failed to initialize database: %v", err)
	}
	defer cfg.Close()

	// Inicializar Redis
	if err := cfg.InitRedis(); err != nil {
		log.Printf("Warning: Failed to initialize Redis: %v", err)
		log.Println("Continuing without Redis cache...")
	}

	// Inicializar repositórios
	userRepo := repository.NewUserRepository(cfg.DB)
	roleRepo := repository.NewRoleRepository(cfg.DB)
	servicoRepo := repository.NewServicoRepository(cfg.DB)
	agendamentoRepo := repository.NewAgendamentoRepository(cfg.DB)

	// Inicializar utilitários
	jwtManager := jwt.NewManager(cfg.JWTSecret, cfg.JWTExpirationHours)
	redisClient := pkgredis.NewClient(cfg.RedisClient)

	// Inicializar serviços
	authService := service.NewAuthService(userRepo, jwtManager, redisClient)
	userService := service.NewUserService(userRepo, roleRepo, redisClient)
	servicoService := service.NewServicoService(servicoRepo, userRepo, redisClient)
	agendamentoService := service.NewAgendamentoService(agendamentoRepo, servicoRepo, userRepo, redisClient)

	// Inicializar handlers
	authHandler := handler.NewAuthHandler(authService)
	userHandler := handler.NewUserHandler(userService)
	servicoHandler := handler.NewServicoHandler(servicoService)
	agendamentoHandler := handler.NewAgendamentoHandler(agendamentoService)

	// Inicializar middlewares
	authMiddleware := middleware.NewAuthMiddleware(authService)

	// Configurar rotas
	r := router.SetupRouter(
		authHandler,
		userHandler,
		servicoHandler,
		agendamentoHandler,
		authMiddleware,
		cfg.CORSAllowedOrigins,
	)

	// Canal para capturar sinais do sistema
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)

	// Iniciar servidor em uma goroutine
	go func() {
		addr := fmt.Sprintf(":%s", cfg.Port)
		log.Printf("🚀 Server starting on port %s", cfg.Port)
		log.Printf("📝 Environment: %s", cfg.GinMode)
		log.Printf("🗄️  Database: %s:%s/%s", cfg.DBHost, cfg.DBPort, cfg.DBName)
		if cfg.RedisClient != nil {
			log.Printf("🔴 Redis: %s:%s", cfg.RedisHost, cfg.RedisPort)
		}
		log.Printf("🔗 Health check: http://localhost:%s/health", cfg.Port)
		
		if err := r.Run(addr); err != nil {
			log.Fatalf("Failed to start server: %v", err)
		}
	}()

	// Aguardar sinal de encerramento
	<-quit
	log.Println("🛑 Shutting down server...")

	log.Println("✅ Server exited gracefully")
}
