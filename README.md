-----

# 🚀 Sistema de Prestação de Serviços

Este é o repositório da API para o Sistema de Prestação de Serviços, desenvolvido com Spring Boot.

## 📝 Estudo de Caso

Uma empresa deseja desenvolver um Sistema de Prestação de Serviços que permita o gerenciamento eficiente de clientes, serviços e prestadores. O sistema deve possibilitar que clientes consultem os tipos de serviços oferecidos, agendem serviços com prestadores disponíveis, realizem o pagamento utilizando diferentes formas e recebam notificações sobre o status de seus agendamentos.

-----

## 🛠️ Tecnologias Utilizadas

O projeto foi construído utilizando as seguintes tecnologias:

* **Backend:** Spring Boot 3+
* **Linguagem:** Java 21+
* **Banco de Dados:** PostgreSQL 17+
* **Containerização:** Docker
* **Documentação:** Postman e Swagger

-----

## ⚙️ Pré-requisitos

Antes de começar, garanta que você tenha as seguintes ferramentas instaladas em seu ambiente:

* [Java Development Kit (JDK) 21+](https://www.oracle.com/java/technologies/downloads/)
* [Docker](https://www.docker.com/products/docker-desktop/) e [Docker Compose](https://docs.docker.com/compose/install/)
* [Maven](https://maven.apache.org/download.cgi)
* Um cliente de API como [Postman](https://www.postman.com/downloads/)

-----

## ▶️ Como Executar o Projeto

Siga os passos abaixo para executar a aplicação localmente.

1.  **Clone o repositório:**

    ```bash
    git clone https://github.com/Luiz-Ferndo/API-Servicos-P1
    cd API-Servicos-P1
    ```

2.  **Suba o container do banco de dados com Docker:**
    Na raiz do projeto, execute o comando para iniciar o container do PostgreSQL em segundo plano.

    ```bash
    docker compose up -d
    ```

3.  **Execute a aplicação Spring Boot:**
    Você pode executar a aplicação usando o wrapper do Maven.

    ```bash
    ./mvnw spring-boot:run
    ```

4.  **Acesse a aplicação:**
    A API estará disponível em `http://localhost:8080`.

-----

## 🐳 Usando o Script de Desenvolvimento (`dev.sh`)

Para facilitar o ciclo de desenvolvimento, o projeto inclui um script de shell (`dev.sh`) que automatiza as tarefas mais comuns.

**Primeiro, dê permissão de execução ao script:**

```bash
chmod +x dev.sh
```

**Comandos disponíveis:**

| Comando | Descrição |
| :--- | :--- |
| `./dev.sh build` | Compila o código-fonte e empacota a aplicação em um arquivo JAR. |
| `./dev.sh up` | Constrói e sobe os containers da aplicação e do banco de dados. |
| `./dev.sh down` | Para e remove os containers da aplicação. |
| `./dev.sh logs` | Exibe os logs dos containers em tempo real, útil para depuração. |
| `./dev.sh clean` | Para os containers, remove os volumes (dados do banco) e limpa o sistema. |
| `./dev.sh dev` | Executa um ciclo de desenvolvimento completo (down, up, logs). |
| `./dev.sh install` | **Instala o comando `dev` nativamente no seu terminal (para Bash/Zsh).** |
| `./dev.sh uninstall` | **Remove a instalação do comando `dev` do seu terminal.** |

### 🚀 Tornando o Comando Nativo (Opcional)

Para evitar ter que digitar `./dev.sh` toda vez, você pode "instalar" o script como um comando nativo no seu shell.

1.  **Execute o instalador:**

    ```bash
    ./dev.sh install
    ```

    Isso adicionará uma função ao seu arquivo de configuração (`~/.bashrc` ou `~/.zshrc`).

2.  **Atualize seu terminal:**
    Para que a mudança tenha efeito, execute o comando abaixo ou simplesmente reinicie seu terminal.

    ```bash
    # Para Bash
    source ~/.bashrc

    # Para Zsh
    source ~/.zshrc
    ```

3.  **Pronto\!** Agora você pode usar os comandos de forma muito mais simples, de qualquer pasta:

    ```bash
    dev up
    dev logs
    dev down
    ```

Para reverter o processo, basta executar `./dev.sh uninstall`.

-----

## 🔑 Autenticação

A maioria dos endpoints desta API é protegida e requer um token de autenticação **JWT (JSON Web Token)**. Para obtê-lo, você deve primeiro registrar um usuário e depois realizar o login.

1.  **Registre-se** usando o endpoint `POST /users`.
2.  **Faça login** com o endpoint `POST /users/login` para receber seu token.
3.  **Envie o token** no cabeçalho de autorização das requisições protegidas:
    `Authorization: Bearer SEU_TOKEN_JWT`

-----

## 📖 Documentação da API

A URL base para todas as requisições é: `http://localhost:8080`

### Módulo de Usuários

Endpoints para gerenciamento de usuários e autenticação.

| Método | Endpoint | Autenticação | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/users` | **Não** | Registra um novo usuário no sistema. |
| `POST` | `/users/login` | **Não** | Autentica um usuário e retorna um token JWT. |
| `GET` | `/users` | **Sim** | Lista todos os usuários cadastrados. |
| `GET` | `/users/{id}` | **Sim** | Busca um usuário específico pelo seu ID. |
| `GET` | `/users/search?email={email}` | **Sim** | Busca um usuário pelo seu endereço de email. |
| `PUT` | `/users/{id}` | **Sim** | Atualiza os dados de um usuário. |
| `DELETE` | `/users/{id}` | **Sim** | Exclui um usuário do sistema. |

#### Exemplos de Requisições (Corpo/Body)

* **`POST /users` (Registrar Usuário)**

  ```json
  {
      "email": "cliente@example.com",
      "password": "umaSenhaForte123",
      "role": "ROLE_ADMINISTRATOR"
  }
  ```

  *Obs: Os papéis (`role`) disponíveis são `ROLE_ADMINISTRATOR` e `ROLE_CUSTOMER`.*

* **`POST /users/login` (Login)**

  ```json
  {
      "email": "cliente@example.com",
      "password": "umaSenhaForte123"
  }
  ```

* **`PUT /users/{id}` (Atualizar Usuário)**

  ```json
  {
      "email": "novo.email@example.com.br"
  }
  ```

### Módulo de Agendamentos

Endpoints para gerenciar os agendamentos de serviços.

| Método | Endpoint | Autenticação | Descrição |
| :--- | :--- | :--- | :--- |
| `POST` | `/agendamentos` | **Sim** | Cria um novo agendamento. |
| `GET` | `/agendamentos` | **Sim** | Lista todos os agendamentos (pode ser filtrado). |
| `DELETE` | `/agendamentos/{id}` | **Sim** | Cancela/exclui um agendamento. |

#### Exemplo de Requisição (Corpo/Body)

* **`POST /agendamentos` (Criar Agendamento)**
  ```json
  {
      "clientId": 1,
      "providerId": 5,
      "serviceId": 12,
      "scheduledDateTime": "2025-10-20T14:00:00"
  }
  ```
  *Nota: Os endpoints de agendamento e outros módulos ainda estão em desenvolvimento e podem não estar totalmente implementados.*

-----

## 📚 Documentação Complementar

* [Requisitos](Documentação/requisitos.md)
* [Caso de Uso](Documentação/caso_de_uso.md)