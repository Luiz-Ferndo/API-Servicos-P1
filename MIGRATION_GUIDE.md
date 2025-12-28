# 🔄 Guia de Migração: Java/Spring Boot → Go/Gin

Este documento explica as diferenças entre as duas implementações e como migrar entre elas.

## 📊 Comparação de Tecnologias

| Aspecto | Java/Spring Boot | Go/Gin |
|---------|------------------|---------|
| **Linguagem** | Java 21+ | Go 1.22+ |
| **Framework Web** | Spring Boot 3 | Gin |
| **ORM** | JPA/Hibernate | GORM |
| **Autenticação** | Spring Security + JWT | JWT + Middleware customizado |
| **Banco de Dados** | PostgreSQL 17+ | PostgreSQL 17+ (compatível) |
| **Cache** | ❌ Não incluído | ✅ Redis 7+ |
| **Build** | Maven | Go modules |
| **Tamanho do Binary** | ~50-100MB (JAR) | ~15-20MB |
| **Memória (RAM)** | ~300-500MB | ~50-100MB |
| **Startup Time** | ~5-10 segundos | <1 segundo |

## 🗄️ Compatibilidade de Banco de Dados

**Boa notícia:** As duas versões são **100% compatíveis** em termos de banco de dados!

- Ambas usam o mesmo schema PostgreSQL
- As tabelas, colunas e relacionamentos são idênticos
- Você pode alternar entre as versões sem migrar dados

### Nomes de Colunas (idênticos)

```sql
-- Users
users (cd_user, nm_user, ds_email, ds_password)

-- Roles
role (cd_role, nm_role)

-- Servico
servico (cd_servico, nm_servico, vl_servico, ds_servico, st_ativo)

-- Agendamento
agendamento (cd_agendamento, cd_cliente_user, cd_prestador_user, 
             cd_servico, dt_agendamento, vl_agendamento, ds_status, 
             ds_motivo_cancelamento)
```

## 🔌 Compatibilidade de API

As APIs são **100% compatíveis**. Os mesmos endpoints, payloads e responses funcionam em ambas versões.

### Endpoints Idênticos

```
POST   /auth/login
POST   /users
GET    /users
GET    /users/:id
GET    /users/search?email=
PUT    /users/:id
DELETE /users/:id
POST   /servicos
GET    /servicos
GET    /servicos/:id
DELETE /servicos/:id
POST   /agendamentos
GET    /agendamentos
GET    /agendamentos/cliente/:id
GET    /agendamentos/prestador/:id
PUT    /agendamentos/:id/status
```

### Payloads JSON Idênticos

**Exemplo - Login (funciona em ambas versões):**
```json
POST /auth/login
{
  "email": "admin@admin.com",
  "password": "abcd123456"
}
```

**Exemplo - Criar Usuário (funciona em ambas versões):**
```json
POST /users
{
  "name": "João Silva",
  "email": "joao@email.com",
  "password": "senha123",
  "role": "ROLE_SERVICE_PROVIDER"
}
```

## 🚀 Como Migrar

### Cenário 1: Migrar de Java para Go (mantendo os dados)

1. **Pare a aplicação Java:**
   ```bash
   docker-compose down app  # ou ctrl+c se rodando localmente
   ```

2. **Mantenha o PostgreSQL rodando:**
   ```bash
   # O container do PostgreSQL NÃO deve ser parado
   docker ps | grep postgres  # deve estar rodando
   ```

3. **Atualize o docker-compose.yml para a versão Go:**
   - Já está pronto no repositório!

4. **Inicie a aplicação Go:**
   ```bash
   docker-compose up -d app
   ```

5. **Verifique se está funcionando:**
   ```bash
   curl http://localhost:8080/health
   ```

A aplicação Go irá:
- ✅ Conectar ao mesmo banco PostgreSQL
- ✅ Validar/ajustar o schema automaticamente (GORM AutoMigrate)
- ✅ Reutilizar todos os dados existentes
- ✅ Responder aos mesmos endpoints

### Cenário 2: Migrar de Go para Java (mantendo os dados)

1. **Pare a aplicação Go:**
   ```bash
   docker-compose down app redis
   ```

2. **Mantenha o PostgreSQL rodando**

3. **Atualize o docker-compose.yml para a versão Java** (use a versão anterior do arquivo)

4. **Inicie a aplicação Java:**
   ```bash
   docker-compose up -d app
   ```

### Cenário 3: Rodar ambas simultaneamente (diferentes portas)

É possível rodar ambas versões ao mesmo tempo para testes:

```bash
# No docker-compose da versão Go, altere a porta:
services:
  app:
    ports:
      - "8081:8080"  # Go na porta 8081
```

Assim você terá:
- Java: `http://localhost:8080`
- Go: `http://localhost:8081`
- Ambas acessando o mesmo PostgreSQL

## 🔑 Diferenças Importantes

### 1. Redis (só na versão Go)

A versão Go usa Redis para:
- Cache de tokens JWT
- Cache de consultas frequentes
- Cache de dados de usuário

**Impacto:** A versão Go é mais rápida em requisições subsequentes.

### 2. Variáveis de Ambiente

**Java (.env):**
```env
DB_URL=jdbc:postgresql://postgres:5432/prestacao_servicos
DB_USERNAME=postgres
DB_PASSWORD=root
JWT_SECRET_KEY=...
JWT_EXPIRATION_TIME=4
```

**Go (.env):**
```env
DB_HOST=postgres
DB_PORT=5432
DB_USER=admin
DB_PASSWORD=admin123
DB_NAME=prestacao_servicos
JWT_SECRET=...
JWT_EXPIRATION_HOURS=24
REDIS_HOST=redis
REDIS_PORT=6379
```

### 3. Estrutura de Código

**Java:**
```
src/main/java/com/prestacaoservicos/
├── controller/
├── entity/
├── repository/
├── service/
└── security/
```

**Go:**
```
internal/
├── handler/      (equivalente a controller)
├── domain/entity/
├── repository/
├── service/
├── middleware/   (equivalente a security)
└── config/
```

## 📈 Quando usar cada versão?

### Use Java/Spring Boot se:
- ✅ Equipe já conhece Java/Spring
- ✅ Precisa de ecossistema Spring maduro
- ✅ Integração com outras aplicações Java
- ✅ Infraestrutura otimizada para JVM

### Use Go/Gin se:
- ✅ Busca performance máxima
- ✅ Quer menor consumo de recursos (RAM/CPU)
- ✅ Precisa de startup rápido
- ✅ Deploy simplificado (binary único)
- ✅ Escala horizontal facilitada
- ✅ Quer aprender Go/tecnologias modernas

## 🧪 Testando a Compatibilidade

Execute o mesmo teste em ambas versões:

```bash
# 1. Login (obter token)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@admin.com","password":"abcd123456"}'

# Copie o token recebido

# 2. Listar usuários
curl -X GET http://localhost:8080/users \
  -H "Authorization: Bearer SEU_TOKEN"

# 3. Criar serviço
curl -X POST http://localhost:8080/servicos \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Teste",
    "valor": 100.00,
    "descricao": "Serviço de teste"
  }'
```

Os resultados devem ser **idênticos** em ambas versões! 🎉

## 🤝 Suporte

Ambas as versões são mantidas e funcionais. Escolha a que melhor atende suas necessidades!

Para dúvidas específicas:
- **Java:** Consulte a documentação Spring Boot
- **Go:** Consulte README_GO.md

## 📚 Recursos Adicionais

- [Documentação Gin Framework](https://gin-gonic.com/)
- [Documentação GORM](https://gorm.io/)
- [Documentação Spring Boot](https://spring.io/projects/spring-boot)
- [Go by Example](https://gobyexample.com/)

---

**Resumo:** As duas implementações são completamente compatíveis e intercambiáveis. Migre com confiança! ✨
