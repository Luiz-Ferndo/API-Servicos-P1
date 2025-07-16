-- Tabela Cliente
CREATE TABLE cliente (
    cd_cliente SERIAL PRIMARY KEY,
    nm_cliente VARCHAR(255) NOT NULL,
    ds_email VARCHAR(255) NOT NULL UNIQUE,
    ds_senha VARCHAR(255) NOT NULL
);

-- Tabela Prestador
CREATE TABLE prestador (
    cd_prestador SERIAL PRIMARY KEY,
    nm_prestador VARCHAR(255) NOT NULL,
    cd_servico INTEGER NOT NULL,
    vl_servico DOUBLE PRECISION NOT NULL

    CONSTRAINT fk_prestador_servico FOREIGN KEY (cd_servico)
    REFERENCES servico(cd_servico) ON DELETE RESTRICT
);

-- Tabela Serviço
CREATE TABLE servico (
    cd_servico SERIAL PRIMARY KEY,
    ds_servico VARCHAR(255) NOT NULL UNIQUE
);

-- Tabela Dominio Status Agendamento
CREATE TABLE dominio_status_agendamento (
    cd_status SMALLINT PRIMARY KEY,
    ds_status VARCHAR(50) NOT NULL
);


-- Tabela Agendamento
CREATE TABLE agendamento (
    cd_agendamento SERIAL PRIMARY KEY,
    cd_cliente INTEGER NOT NULL,
    cd_prestador INTEGER NOT NULL,
    dt_agendamento TIMESTAMP NOT NULL,
    vl_agendamento DOUBLE PRECISION NOT NULL,
    cd_status SMALLINT NOT NULL,
    ds_motivo_cancelamento VARCHAR(255),

    CONSTRAINT fk_agendamento_cliente FOREIGN KEY (cd_cliente)
    REFERENCES cliente(cd_cliente) ON DELETE CASCADE,

    CONSTRAINT fk_agendamento_prestador FOREIGN KEY (cd_prestador)
    REFERENCES prestador(cd_prestador) ON DELETE CASCADE

    CONSTRAINT fk_agendamento_status FOREIGN KEY (cd_status)
    REFERENCES dominio_status_agendamento(cd_status)
);
