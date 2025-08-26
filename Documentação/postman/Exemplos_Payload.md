# Exemplos de Payload

## Usuários

### Exemplo 1: Prestador de Serviço (Eletricista)

```json
{
  "name": "João Silva",
  "email": "joao.eletricista@provedor.com",
  "password": "senhaForte123",
  "role": "ROLE_SERVICE_PROVIDER"
}
```

### Exemplo 2: Prestadora de Serviço (Encanadora)

```json
{
  "name": "Maria Oliveira",
  "email": "maria.encanadora@provedor.com",
  "password": "senhaForte123",
  "role": "ROLE_SERVICE_PROVIDER"
}
```

### Exemplo 3: Cliente 1

```json
{
  "name": "Carlos Souza",
  "email": "carlos.souza@email.com",
  "password": "outraSenha456",
  "role": "ROLE_CUSTOMER"
}
```

### Exemplo 4: Cliente 2

```json
{
  "name": "Ana Pereira",
  "email": "ana.pereira@email.com",
  "password": "outraSenha456",
  "role": "ROLE_CUSTOMER"
}
```

## Serviços

### Exemplo 1: Serviço do João (Eletricista)

```json
{
  "nome": "Manutenção Elétrica Preventiva",
  "valor": 350.00,
  "descricao": "Revisão completa de disjuntores, tomadas e pontos de luz para garantir a segurança da residência."
}
```

### Exemplo 2: Outro Serviço do João

```json
{
  "nome": "Instalação de Luminária de Teto",
  "valor": 130.00,
  "descricao": "Instalação e conexão de lustres e plafons, incluindo a passagem de fiação se necessário."
}
```

### Exemplo 3: Serviço da Maria (Encanadora)

```json
{
  "nome": "Desentupimento de Ralo",
  "valor": 180.00,
  "descricao": "Serviço de desentupimento de ralos de banheiro ou cozinha com maquinário especializado."
}
```

### Exemplo 4: Outro Serviço da Maria

```json
{
  "nome": "Conserto de Vazamento em Pia",
  "valor": 90.00,
  "descricao": "Troca de sifão e vedação para conserto de vazamentos em pias de cozinha ou banheiro."
}
```

## Agendamentos

### Exemplo 1: Ana agenda um serviço com Maria

*Endpoint: `POST {{base_url}}/agendamentos`*

```json
{
  "prestadorId": 3,
  "servicoId": 3,
  "dataHora": "2025-09-12T14:30:00"
}
```

### Exemplo 2: Carlos agenda um serviço com João
*Endpoint: `POST {{base_url}}/agendamentos`*

```json
{
  "prestadorId": 2,
  "servicoId": 1,
  "dataHora": "2025-09-15T14:30:00"
}
```


## Atualização de Status do Agendamento

### Exemplo 1: Maria finaliza o serviço da Ana

*Endpoint: `PUT {{base_url}}/agendamentos/2/status`*

```json
{
  "status": "FINALIZADO",
  "motivo": null
}
```

### Exemplo 2: João cancela o serviço do Carlos

*Endpoint: `PUT {{base_url}}/agendamentos/1/status`*

```json
{
  "status": "CANCELADO",
  "motivo": "O cliente solicitou o cancelamento com 24h de antecedência."
}
```