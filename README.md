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
* [Maven](https://maven.apache.org/download.cgi) ou [Gradle](https://gradle.org/install/) (dependendo do seu gerenciador de dependências)
* Um cliente de API como [Postman](https://www.postman.com/downloads/) ou [Insomnia](https://insomnia.rest/download)

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
    Você pode executar a aplicação usando o Maven.

    ```bash
    # Usando Maven
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

| Comando               | Descrição                                                                                             |
| :-------------------- | :---------------------------------------------------------------------------------------------------- |
| `./dev.sh build`      | Compila o código-fonte e empacota a aplicação em um arquivo JAR, pulando os testes.                     |
| `./dev.sh up`         | Constrói as imagens (se necessário) e sobe os containers da aplicação e do banco de dados.              |
| `./dev.sh down`       | Para e remove os containers da aplicação.                                                               |
| `./dev.sh logs`       | Exibe os logs dos containers em tempo real, útil para depuração.                                        |
| `./dev.sh clean`      | Para os containers, remove os volumes associados (deletando os dados do banco) e limpa o sistema Docker. |
| `./dev.sh dev`        | Executa um ciclo de desenvolvimento completo: limpa o ambiente, sobe os containers e exibe a URL.     |

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

| Método   | Endpoint                      | Autenticação | Descrição                                  |
| :------- | :---------------------------- | :----------- | :----------------------------------------- |
| `POST`   | `/users`                      | **Não** | Registra um novo usuário no sistema.       |
| `POST`   | `/users/login`                | **Não** | Autentica um usuário e retorna um token JWT. |
| `GET`    | `/users`                      | **Sim** | Lista todos os usuários cadastrados.       |
| `GET`    | `/users/{id}`                 | **Sim** | Busca um usuário específico pelo seu ID.   |
| `GET`    | `/users/search?email={email}` | **Sim** | Busca um usuário pelo seu endereço de email. |
| `PUT`    | `/users/{id}`                 | **Sim** | Atualiza os dados de um usuário.           |
| `DELETE` | `/users/{id}`                 | **Sim** | Exclui um usuário do sistema.              |

#### Exemplos de Requisições (Corpo/Body)

* **`POST /users` (Registrar Usuário)**

  ```json
  {
      "email": "cliente@example.com",
      "password": "umaSenhaForte123",
      "role": "ROLE_ADMINISTRATOR"
  }
  ```

  *Obs: Os papéis (`role`) podem ser `ROLE_ADMINISTRATOR` e `ROLE_CUSTOMER`

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

| Método   | Endpoint             | Autenticação | Descrição                                        |
| :------- | :------------------- | :----------- | :----------------------------------------------- |
| `POST`   | `/agendamentos`      | **Sim** | Cria um novo agendamento.                        |
| `GET`    | `/agendamentos`      | **Sim** | Lista todos os agendamentos (pode ser filtrado). |
| `DELETE` | `/agendamentos/{id}` | **Sim** | Cancela/exclui um agendamento.                   |

#### Exemplo de Requisição (Corpo/Body)

* **`POST /agendamentos` (Criar Agendamento)**
  *O corpo da requisição deve conter as informações necessárias para o agendamento, como o ID do cliente, do prestador e do serviço.*
  ```json
  {
      "clientId": 1,
      "providerId": 5,
      "serviceId": 12,
      "scheduledDateTime": "2025-10-20T14:00:00"
  }
  ```
  *Nota: Os endpoints de agendamento e os demais vistos na documentação do postman estão em construção e podem não estar totalmente implementados.*

-----

## 📚 Documentação Complementar

* [Requisitos](https://www.google.com/search?q=./Documentacao/requisitos.md)
* [Caso de Uso](https://www.google.com/search?q=./Documentacao/caso_de_uso.md)