-- Tabela Cliente
CREATE TABLE cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL
);

-- Tabela Prestador
CREATE TABLE prestador (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    servico VARCHAR(255) NOT NULL,
    preco DOUBLE PRECISION NOT NULL
);

-- Tabela Agendamento
CREATE TABLE agendamento (
     id SERIAL PRIMARY KEY,
     cliente_id INTEGER REFERENCES cliente(id),
     prestador_id INTEGER REFERENCES prestador(id),
     data_hora TIMESTAMP NOT NULL,
     valor DOUBLE PRECISION,
     status VARCHAR(255),
     motivo_cancelamento VARCHAR(255)
);
