package middleware

import (
	"fmt"
	"net/http"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/go-playground/validator/v10"
)

// ErrorMiddleware trata erros de forma padronizada
func ErrorMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Next()

		// Verificar se há erros
		if len(c.Errors) > 0 {
			err := c.Errors.Last()

			// Se é um erro de validação
			if validationErrs, ok := err.Err.(validator.ValidationErrors); ok {
				errors := make([]string, len(validationErrs))
				for i, fieldErr := range validationErrs {
					errors[i] = fmt.Sprintf("Campo '%s' %s", fieldErr.Field(), getValidationMessage(fieldErr))
				}

				c.JSON(http.StatusBadRequest, gin.H{
					"status":    http.StatusBadRequest,
					"message":   "Erro de validação",
					"path":      c.Request.URL.Path,
					"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
					"errors":    errors,
				})
				return
			}

			// Erro genérico
			status := c.Writer.Status()
			if status == http.StatusOK {
				status = http.StatusInternalServerError
			}

			c.JSON(status, gin.H{
				"status":    status,
				"message":   err.Error(),
				"path":      c.Request.URL.Path,
				"timestamp": fmt.Sprintf("%d", time.Now().Unix()),
			})
		}
	}
}

// getValidationMessage retorna uma mensagem de erro amigável
func getValidationMessage(fieldErr validator.FieldError) string {
	switch fieldErr.Tag() {
	case "required":
		return "é obrigatório"
	case "email":
		return "deve ser um email válido"
	case "min":
		return fmt.Sprintf("deve ter no mínimo %s caracteres", fieldErr.Param())
	case "max":
		return fmt.Sprintf("deve ter no máximo %s caracteres", fieldErr.Param())
	case "gt":
		return fmt.Sprintf("deve ser maior que %s", fieldErr.Param())
	case "gte":
		return fmt.Sprintf("deve ser maior ou igual a %s", fieldErr.Param())
	case "lt":
		return fmt.Sprintf("deve ser menor que %s", fieldErr.Param())
	case "lte":
		return fmt.Sprintf("deve ser menor ou igual a %s", fieldErr.Param())
	default:
		return fmt.Sprintf("falhou na validação '%s'", fieldErr.Tag())
	}
}
