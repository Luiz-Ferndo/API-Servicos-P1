-- Script de inicialização do banco de dados
-- Este arquivo é opcional, pois o GORM já faz auto-migration
-- Mas pode ser útil para configurações específicas ou para deploy em produção

-- Garantir que o banco está usando UTF-8
ALTER DATABASE prestacao_servicos SET client_encoding TO 'UTF8';

-- Criar extensão para UUID se necessário
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- As tabelas serão criadas automaticamente pelo GORM através de AutoMigrate
-- Este script pode ser usado para criar índices adicionais ou constraints específicas

-- Índices úteis para performance
-- Serão criados após as tabelas serem geradas pelo GORM

-- CREATE INDEX IF NOT EXISTS idx_users_email ON users(ds_email);
-- CREATE INDEX IF NOT EXISTS idx_agendamento_data ON agendamento(dt_agendamento);
-- CREATE INDEX IF NOT EXISTS idx_agendamento_cliente ON agendamento(cd_cliente_user);
-- CREATE INDEX IF NOT EXISTS idx_agendamento_prestador ON agendamento(cd_prestador_user);
-- CREATE INDEX IF NOT EXISTS idx_servico_ativo ON servico(st_ativo);
