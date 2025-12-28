package router

import (
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/handler"
	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/middleware"
	"github.com/gin-gonic/gin"
)

// SetupRouter configura todas as rotas da aplicação
func SetupRouter(
	authHandler *handler.AuthHandler,
	userHandler *handler.UserHandler,
	servicoHandler *handler.ServicoHandler,
	agendamentoHandler *handler.AgendamentoHandler,
	authMiddleware *middleware.AuthMiddleware,
	corsOrigins string,
) *gin.Engine {
	router := gin.Default()

	// Middlewares globais
	router.Use(middleware.CORSMiddleware(corsOrigins))
	router.Use(middleware.ErrorMiddleware())

	// Health check
	router.GET("/health", func(c *gin.Context) {
		c.JSON(200, gin.H{
			"status": "ok",
			"message": "API está funcionando",
		})
	})

	// Grupo de rotas públicas - Autenticação
	authGroup := router.Group("/auth")
	{
		authGroup.POST("/login", authHandler.Login)
	}

	// Grupo de rotas públicas - Usuários (registro)
	router.POST("/users", userHandler.Create)

	// Grupo de rotas protegidas - Usuários
	usersGroup := router.Group("/users")
	usersGroup.Use(authMiddleware.Authenticate())
	{
		usersGroup.GET("", userHandler.FindAll)
		usersGroup.GET("/:id", userHandler.FindByID)
		usersGroup.GET("/search", userHandler.FindByEmail)
		usersGroup.PUT("/:id", userHandler.Update)
		usersGroup.DELETE("/:id", userHandler.Delete)
	}

	// Grupo de rotas protegidas - Serviços
	servicosGroup := router.Group("/servicos")
	servicosGroup.Use(authMiddleware.Authenticate())
	{
		servicosGroup.GET("", servicoHandler.FindAll)
		servicosGroup.GET("/:id", servicoHandler.FindByID)
		
		// Rotas que requerem role SERVICE_PROVIDER
		servicosGroup.POST("", authMiddleware.RequireRole("ROLE_SERVICE_PROVIDER"), servicoHandler.Create)
		servicosGroup.DELETE("/:id", authMiddleware.RequireRole("ROLE_SERVICE_PROVIDER"), servicoHandler.Delete)
	}

	// Grupo de rotas protegidas - Agendamentos
	agendamentosGroup := router.Group("/agendamentos")
	agendamentosGroup.Use(authMiddleware.Authenticate())
	{
		agendamentosGroup.POST("", agendamentoHandler.Create)
		agendamentosGroup.GET("", agendamentoHandler.FindAll)
		agendamentosGroup.GET("/cliente/:id", agendamentoHandler.FindByCliente)
		agendamentosGroup.GET("/prestador/:id", agendamentoHandler.FindByPrestador)
		agendamentosGroup.PUT("/:id/status", agendamentoHandler.UpdateStatus)
	}

	return router
}
