# 🚀 Sistema de Prestação de Serviços - Go Edition

API REST para Sistema de Prestação de Serviços construída com **Go + Gin + GORM + PostgreSQL + Redis + Docker**.

## 📝 Descrição

Sistema completo para gerenciamento de prestação de serviços que permite:
- **Usuários** (Clientes e Prestadores de Serviço)
- **Serviços** oferecidos por prestadores
- **Agendamentos** entre clientes e prestadores
- **Autenticação JWT** com roles e permissões
- **Cache Redis** para performance otimizada

## 🛠️ Stack Tecnológica

- **Linguagem:** Go 1.22+
- **Framework Web:** Gin
- **ORM:** GORM
- **Banco de Dados:** PostgreSQL 17+
- **Cache:** Redis 7+
- **Autenticação:** JWT (JSON Web Tokens)
- **Containerização:** Docker + Docker Compose

## 📁 Estrutura do Projeto

```
.
├── cmd/
│   └── api/
│       └── main.go                 # Entry point da aplicação
├── internal/
│   ├── config/
│   │   └── config.go               # Configurações (env, database, redis)
│   ├── domain/
│   │   ├── entity/                 # Entidades do domínio
│   │   │   ├── user.go
│   │   │   ├── role.go
│   │   │   ├── permission.go
│   │   │   ├── servico.go
│   │   │   ├── agendamento.go
│   │   │   └── user_phone.go
│   │   └── enum/                   # Enumerações
│   │       ├── role_name.go
│   │       ├── permission.go
│   │       ├── phone_type.go
│   │       └── status.go
│   ├── dto/                        # Data Transfer Objects
│   │   ├── auth_dto.go
│   │   ├── user_dto.go
│   │   ├── servico_dto.go
│   │   └── agendamento_dto.go
│   ├── repository/                 # Camada de acesso a dados
│   │   ├── user_repository.go
│   │   ├── role_repository.go
│   │   ├── servico_repository.go
│   │   └── agendamento_repository.go
│   ├── service/                    # Lógica de negócio
│   │   ├── auth_service.go
│   │   ├── user_service.go
│   │   ├── servico_service.go
│   │   └── agendamento_service.go
│   ├── handler/                    # HTTP Handlers
│   │   ├── auth_handler.go
│   │   ├── user_handler.go
│   │   ├── servico_handler.go
│   │   └── agendamento_handler.go
│   ├── middleware/                 # Middlewares
│   │   ├── auth_middleware.go
│   │   ├── cors_middleware.go
│   │   └── error_middleware.go
│   ├── router/
│   │   └── router.go               # Configuração de rotas
│   └── pkg/                        # Utilitários
│       ├── jwt/
│       │   └── jwt.go
│       ├── redis/
│       │   └── redis.go
│       └── validator/
│           └── validator.go
├── migrations/
│   └── init.sql                    # Scripts SQL (opcional)
├── docker-compose.yml              # Orquestração Docker
├── Dockerfile                      # Build da aplicação
├── .env.example                    # Exemplo de variáveis de ambiente
├── go.mod                          # Dependências Go
├── go.sum                          # Checksums
└── README.md                       # Esta documentação

```

## ⚙️ Pré-requisitos

- [Go 1.22+](https://golang.org/dl/)
- [Docker](https://www.docker.com/products/docker-desktop/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## ▶️ Como Executar

### Opção 1: Com Docker Compose (Recomendado)

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/Luiz-Ferndo/API-Servicos-P1
   cd API-Servicos-P1
   ```

2. **Configure as variáveis de ambiente:**
   ```bash
   cp .env.example .env
   # Edite o arquivo .env conforme necessário
   ```

3. **Inicie os serviços:**
   ```bash
   docker-compose up -d
   ```

4. **Acesse a aplicação:**
   - API: `http://localhost:8080`
   - Health Check: `http://localhost:8080/health`

5. **Para ver os logs:**
   ```bash
   docker-compose logs -f app
   ```

6. **Para parar os serviços:**
   ```bash
   docker-compose down
   ```

### Opção 2: Execução Local (Desenvolvimento)

1. **Instale as dependências:**
   ```bash
   go mod download
   ```

2. **Inicie PostgreSQL e Redis:**
   ```bash
   docker-compose up -d postgres redis
   ```

3. **Configure as variáveis de ambiente:**
   ```bash
   cp .env.example .env
   # Ajuste DB_HOST e REDIS_HOST para localhost
   ```

4. **Execute a aplicação:**
   ```bash
   go run cmd/api/main.go
   ```

## 🔧 Variáveis de Ambiente

Copie `.env.example` para `.env` e ajuste conforme necessário:

```env
# Server
PORT=8080
GIN_MODE=release          # use "debug" para desenvolvimento

# Database
DB_HOST=postgres          # use "localhost" para desenvolvimento local
DB_PORT=5432
DB_USER=admin
DB_PASSWORD=admin123
DB_NAME=prestacao_servicos

# Redis
REDIS_HOST=redis          # use "localhost" para desenvolvimento local
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_DB=0

# JWT
JWT_SECRET=seu-secret-super-seguro
JWT_EXPIRATION_HOURS=24

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080

# Admin User (criado automaticamente na inicialização)
ADMIN_NAME=Admin
ADMIN_EMAIL=admin@admin.com
ADMIN_PASSWORD=abcd123456
```

## 📖 Documentação da API

### URL Base
`http://localhost:8080`

### 🔑 Autenticação

A maioria dos endpoints requer autenticação via **Bearer Token JWT**.

1. **Registre-se** usando `POST /users`
2. **Faça login** com `POST /auth/login` para obter o token
3. **Envie o token** no header: `Authorization: Bearer SEU_TOKEN`

| Método | Endpoint      | Autenticação | Descrição                     |
|--------|---------------|--------------|-------------------------------|
| POST   | `/auth/login` | Não          | Autentica e retorna JWT token |

**Exemplo de Login:**
```json
POST /auth/login
{
  "email": "admin@admin.com",
  "password": "abcd123456"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

### 👤 Módulo de Usuários

| Método | Endpoint                      | Autenticação | Descrição                    |
|--------|-------------------------------|--------------|------------------------------|
| POST   | `/users`                      | Não          | Registra novo usuário        |
| GET    | `/users`                      | Sim          | Lista todos usuários         |
| GET    | `/users/:id`                  | Sim          | Busca usuário por ID         |
| GET    | `/users/search?email={email}` | Sim          | Busca usuário por email      |
| PUT    | `/users/:id`                  | Sim          | Atualiza usuário             |
| DELETE | `/users/:id`                  | Sim          | Remove usuário               |

**Exemplo - Registrar Prestador:**
```json
POST /users
{
  "name": "João Silva",
  "email": "joao.eletricista@provedor.com",
  "password": "senhaForte123",
  "role": "ROLE_SERVICE_PROVIDER"
}
```

**Exemplo - Registrar Cliente:**
```json
POST /users
{
  "name": "Carlos Souza",
  "email": "carlos.souza@email.com",
  "password": "outraSenha456",
  "role": "ROLE_CUSTOMER",
  "phones": [
    {
      "number": "11999999999",
      "type": "MOBILE"
    }
  ]
}
```

### 🛠️ Módulo de Serviços

| Método | Endpoint         | Autenticação | Role                  | Descrição               |
|--------|------------------|--------------|------------------------|-------------------------|
| POST   | `/servicos`      | Sim          | SERVICE_PROVIDER       | Cadastra novo serviço   |
| GET    | `/servicos`      | Sim          | Qualquer               | Lista serviços          |
| GET    | `/servicos/:id`  | Sim          | Qualquer               | Busca serviço por ID    |
| DELETE | `/servicos/:id`  | Sim          | SERVICE_PROVIDER       | Remove serviço          |

**Exemplo - Cadastrar Serviço:**
```json
POST /servicos
Authorization: Bearer {token}
{
  "nome": "Manutenção Elétrica Preventiva",
  "valor": 350.00,
  "descricao": "Revisão completa de disjuntores, tomadas e pontos de luz."
}
```

### 📅 Módulo de Agendamentos

| Método | Endpoint                         | Autenticação | Descrição                        |
|--------|----------------------------------|--------------|----------------------------------|
| POST   | `/agendamentos`                  | Sim          | Cria agendamento                 |
| GET    | `/agendamentos`                  | Sim          | Lista todos agendamentos         |
| GET    | `/agendamentos/cliente/:id`      | Sim          | Agendamentos de um cliente       |
| GET    | `/agendamentos/prestador/:id`    | Sim          | Agendamentos de um prestador     |
| PUT    | `/agendamentos/:id/status`       | Sim          | Atualiza status do agendamento   |

**Exemplo - Criar Agendamento:**
```json
POST /agendamentos
Authorization: Bearer {token}
{
  "prestadorId": 1,
  "servicoId": 1,
  "dataHora": "2025-09-10T10:00:00Z"
}
```

**Exemplo - Atualizar Status:**
```json
PUT /agendamentos/1/status
Authorization: Bearer {token}
{
  "status": "CONFIRMADO"
}
```

**Exemplo - Cancelar com Motivo:**
```json
PUT /agendamentos/1/status
Authorization: Bearer {token}
{
  "status": "CANCELADO",
  "motivo": "Cliente solicitou reagendamento"
}
```

## 🔐 Roles e Permissões

### Roles Disponíveis:
- `ROLE_CUSTOMER` - Cliente do sistema
- `ROLE_SERVICE_PROVIDER` - Prestador de serviço
- `ROLE_ADMINISTRATOR` - Administrador

### Status de Agendamento:
- `AGENDADO` - Agendamento criado
- `CONFIRMADO` - Confirmado pelo prestador
- `CANCELADO` - Cancelado (requer motivo)
- `FINALIZADO` - Serviço concluído
- `NAO_COMPARECEU` - Cliente não compareceu

## 🧪 Testando a API

### Usando cURL

```bash
# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@admin.com","password":"abcd123456"}'

# Criar usuário
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Teste","email":"teste@email.com","password":"123456","role":"ROLE_CUSTOMER"}'

# Listar usuários (com autenticação)
curl -X GET http://localhost:8080/users \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

### Usando Postman ou Insomnia

Importe a coleção de requisições (se disponível) ou crie requisições manualmente seguindo os exemplos acima.

## 🐳 Comandos Docker Úteis

```bash
# Construir e iniciar
docker-compose up --build

# Iniciar em background
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar serviços
docker-compose stop

# Parar e remover containers
docker-compose down

# Remover volumes (apaga dados do banco)
docker-compose down -v

# Reconstruir apenas a API
docker-compose up -d --build app
```

## 📊 Estrutura do Banco de Dados

O GORM cria automaticamente as seguintes tabelas:

- `users` - Usuários do sistema
- `role` - Papéis/funções
- `permission` - Permissões
- `user_role` - Relacionamento usuário-papel (N:N)
- `role_permission` - Relacionamento papel-permissão (N:N)
- `user_phone` - Telefones dos usuários
- `servico` - Serviços oferecidos
- `prestador_servicos` - Relacionamento prestador-serviço (N:N)
- `agendamento` - Agendamentos de serviços

## 🚀 Build para Produção

```bash
# Build local
CGO_ENABLED=0 GOOS=linux go build -o main ./cmd/api

# Build com Docker
docker build -t api-servicos:latest .

# Executar
docker run -p 8080:8080 --env-file .env api-servicos:latest
```

## 🔍 Troubleshooting

### Erro de conexão com o banco
- Verifique se o PostgreSQL está rodando: `docker-compose ps`
- Verifique as credenciais no `.env`
- Aguarde o health check: `docker-compose logs postgres`

### Erro de conexão com Redis
- A aplicação funciona sem Redis, mas com performance reduzida
- Verifique se o Redis está rodando: `docker-compose ps redis`

### Porta 8080 já em uso
- Altere a variável `PORT` no `.env` ou `docker-compose.yml`
- Ou pare o serviço que está usando a porta

## 📝 Logs e Debug

Para modo debug durante desenvolvimento:

1. Altere `GIN_MODE=debug` no `.env`
2. Os logs mostrarão todas as requisições HTTP em detalhes
3. Erros de validação e SQL serão mais verbosos

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

## 👨‍💻 Autor

Desenvolvido como parte do projeto de Sistema de Prestação de Serviços.

---

**Nota:** Esta é uma reescrita completa do sistema original Java/Spring Boot para Go/Gin, mantendo a mesma funcionalidade e estrutura de API.
