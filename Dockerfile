# Stage 1: Build
FROM golang:1.22-alpine AS builder

WORKDIR /app

# Copiar arquivos de dependências
COPY go.mod go.sum ./
RUN go mod download

# Copiar código fonte
COPY . .

# Build da aplicação
RUN CGO_ENABLED=0 GOOS=linux go build -o main ./cmd/api

# Stage 2: Runtime
FROM alpine:latest

WORKDIR /root/

# Instalar ca-certificates para HTTPS
RUN apk --no-cache add ca-certificates

# Copiar binário do stage de build
COPY --from=builder /app/main .

# Copiar .env.example como .env (será sobrescrito por variáveis de ambiente)
COPY .env.example .env

EXPOSE 8080

CMD ["./main"]