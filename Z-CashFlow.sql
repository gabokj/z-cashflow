CREATE DATABASE IF NOT EXISTS z_cashflow;
USE z_cashflow;

-- ------------------------------------------------------------
-- Tabela: usuarios
CREATE TABLE usuarios (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_cadastro DATE NOT NULL DEFAULT (CURRENT_DATE)
);

-- ------------------------------------------------------------
-- Tabela: categorias
CREATE TABLE categorias (
    id_categoria INT PRIMARY KEY AUTO_INCREMENT,
    nome_categoria VARCHAR(50) NOT NULL,
    tipo_fluxo ENUM('ENTRADA', 'SAIDA') NOT NULL
);

-- ------------------------------------------------------------
-- Tabela: movimentacoes
CREATE TABLE movimentacoes (
    id_movimentacao INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    id_categoria INT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_operacao DATE NOT NULL,
    descricao TEXT,
    CONSTRAINT fk_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_categoria FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
);

-- ============================================================
--  Dados iniciais - categorias padrao
-- ============================================================
INSERT INTO categorias (nome_categoria, tipo_fluxo) VALUES
  ('Salário',        'ENTRADA'),
  ('Freelance',      'ENTRADA'),
  ('Investimentos',  'ENTRADA'),
  ('Outros (entrada)','ENTRADA'),
  ('Moradia',        'SAIDA'),
  ('Alimentação',    'SAIDA'),
  ('Transporte',     'SAIDA'),
  ('Saúde',          'SAIDA'),
  ('Lazer',          'SAIDA'),
  ('Educação',       'SAIDA'),
  ('Contas Fixas',   'SAIDA'),
  ('Outros (saída)', 'SAIDA');

-- ============================================================
-- 1. (INSERT INTO) - Minimo 5 registros por tabela
-- ============================================================

-- Inserindo Usuarios
INSERT INTO usuarios (nome, email, senha) VALUES
('Gabriel Teixeira Ricca', 'gabriel@zmail.com', 'olaprofessor'),
('Erik Manager', 'erik@zmail.com', 'senha12'),
('Cida Colaboradora', 'cida@zmail.com', 'senha033'),
('Atila Mentor', 'atila@zmail.com', 'senha3124'),
('Nilton Interim', 'nilton@zmail.com', 'senha21305');

-- Inserindo Movimentacoes (Referenciando IDs e Categorias do DDL acima)
INSERT INTO movimentacoes (id_usuario, id_categoria, valor, data_operacao, descricao) VALUES
(1, 1, 3500.00, '2026-04-01', 'Salário Mensal - Estágio AI'),
(1, 6, 150.50, '2026-04-05', 'Almoço Executivo'),
(2, 3, 500.00, '2026-04-10', 'Rendimento FIIs'),
(3, 10, 299.90, '2026-04-12', 'Curso Especialização Java'),
(4, 7, 85.00, '2026-04-14', 'Uber para Mentoria');

-- ============================================================
-- 2. EXIBICAO DOS DADOS (SELECT)
-- ============================================================

-- Busca de todos os registros (Geral)
SELECT * FROM usuarios;
SELECT * FROM categorias;
SELECT * FROM movimentacoes;

-- Busca com Filtros Especificos (WHERE)
-- Visualizar apenas despesas acima de R$ 100,00
SELECT * FROM movimentacoes
WHERE valor > 100.00
  AND id_categoria IN (SELECT id_categoria FROM categorias WHERE tipo_fluxo = 'SAIDA');

-- Buscar dados do perfil do Gabriel
SELECT nome, email, data_cadastro FROM usuarios WHERE email = 'gabriel@zmail.com';

-- ============================================================
-- 3. EDICAO DE DADOS (UPDATE)
-- ============================================================

-- Atualizar o nome de um usuario
UPDATE usuarios SET nome = 'Gabriel T. Ricca' WHERE id_usuario = 1;

-- Corrigir o valor de uma movimentacao especifica
UPDATE movimentacoes SET valor = 160.00, descricao = 'Almoço Executivo + Sobremesa' WHERE id_movimentacao = 2;

-- Alterar o nome de uma categoria padrao
UPDATE categorias SET nome_categoria = 'Transporte e App' WHERE id_categoria = 7;

-- ============================================================
-- 4. EXCLUSAO DE DADOS (DELETE)
-- ============================================================

-- Remover o usuario Nilton (id_usuario 5)
-- Primeiro removemos as movimentacoes vinculadas a ele devido a FK
DELETE FROM movimentacoes WHERE id_usuario = 5;
DELETE FROM usuarios WHERE id_usuario = 5;

-- Remover uma categoria que nao sera utilizada (ex: Freelance id 2)
DELETE FROM categorias WHERE id_categoria = 2;
