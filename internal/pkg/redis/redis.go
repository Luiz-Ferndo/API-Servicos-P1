package redis

import (
	"context"
	"encoding/json"
	"fmt"
	"time"

	"github.com/Luiz-Ferndo/API-Servicos-P1/internal/dto"
	"github.com/redis/go-redis/v9"
)

// Client wrapper para o cliente Redis
type Client struct {
	client *redis.Client
}

// NewClient cria um novo cliente Redis
func NewClient(redisClient *redis.Client) *Client {
	return &Client{
		client: redisClient,
	}
}

// SetToken armazena um token no Redis
func (c *Client) SetToken(ctx context.Context, userID uint, tokenData *dto.TokenCacheData, expiration time.Duration) error {
	key := fmt.Sprintf("jwt:%d", userID)
	
	data, err := json.Marshal(tokenData)
	if err != nil {
		return fmt.Errorf("failed to marshal token data: %w", err)
	}

	if err := c.client.Set(ctx, key, data, expiration).Err(); err != nil {
		return fmt.Errorf("failed to set token in redis: %w", err)
	}

	return nil
}

// GetToken recupera um token do Redis
func (c *Client) GetToken(ctx context.Context, userID uint) (*dto.TokenCacheData, error) {
	key := fmt.Sprintf("jwt:%d", userID)
	
	data, err := c.client.Get(ctx, key).Result()
	if err != nil {
		if err == redis.Nil {
			return nil, nil // Token não encontrado
		}
		return nil, fmt.Errorf("failed to get token from redis: %w", err)
	}

	var tokenData dto.TokenCacheData
	if err := json.Unmarshal([]byte(data), &tokenData); err != nil {
		return nil, fmt.Errorf("failed to unmarshal token data: %w", err)
	}

	return &tokenData, nil
}

// DeleteToken remove um token do Redis
func (c *Client) DeleteToken(ctx context.Context, userID uint) error {
	key := fmt.Sprintf("jwt:%d", userID)
	
	if err := c.client.Del(ctx, key).Err(); err != nil {
		return fmt.Errorf("failed to delete token from redis: %w", err)
	}

	return nil
}

// SetCache armazena dados genéricos no cache
func (c *Client) SetCache(ctx context.Context, key string, value interface{}, expiration time.Duration) error {
	data, err := json.Marshal(value)
	if err != nil {
		return fmt.Errorf("failed to marshal cache data: %w", err)
	}

	if err := c.client.Set(ctx, key, data, expiration).Err(); err != nil {
		return fmt.Errorf("failed to set cache: %w", err)
	}

	return nil
}

// GetCache recupera dados genéricos do cache
func (c *Client) GetCache(ctx context.Context, key string, dest interface{}) error {
	data, err := c.client.Get(ctx, key).Result()
	if err != nil {
		if err == redis.Nil {
			return nil // Não encontrado
		}
		return fmt.Errorf("failed to get cache: %w", err)
	}

	if err := json.Unmarshal([]byte(data), dest); err != nil {
		return fmt.Errorf("failed to unmarshal cache data: %w", err)
	}

	return nil
}

// DeleteCache remove dados do cache
func (c *Client) DeleteCache(ctx context.Context, key string) error {
	if err := c.client.Del(ctx, key).Err(); err != nil {
		return fmt.Errorf("failed to delete cache: %w", err)
	}

	return nil
}

// InvalidatePattern invalida todas as chaves que correspondem ao padrão
func (c *Client) InvalidatePattern(ctx context.Context, pattern string) error {
	iter := c.client.Scan(ctx, 0, pattern, 0).Iterator()
	for iter.Next(ctx) {
		if err := c.client.Del(ctx, iter.Val()).Err(); err != nil {
			return fmt.Errorf("failed to delete key %s: %w", iter.Val(), err)
		}
	}
	if err := iter.Err(); err != nil {
		return fmt.Errorf("failed to iterate keys: %w", err)
	}

	return nil
}
