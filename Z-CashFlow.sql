-- ============================================================================
--  Z-CashFlow - Banco de dados do Projeto Integrador (Etapa 4)
--  SGBD: MySQL 8.x   |   Execute este script inteiro no MySQL Workbench
--  (File > Open SQL Script > raio "Execute"), conectado como root.
--
--  ATENCAO: o script recria o banco do zero (DROP DATABASE). Use apenas na
--  instalacao inicial ou para restaurar os dados de demonstracao.
-- ============================================================================

-- Garante que os acentos sejam gravados corretamente (UTF-8), em qualquer cliente MySQL.
SET NAMES utf8mb4;

DROP DATABASE IF EXISTS z_cashflow;
CREATE DATABASE z_cashflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE z_cashflow;

-- ----------------------------------------------------------------------------
-- Tabela: usuarios
--   senha_hash guarda o hash PBKDF2 (nunca a senha em texto puro).
--   perfil ADMIN pode gerenciar categorias; USUARIO apenas consulta.
-- ----------------------------------------------------------------------------
CREATE TABLE usuarios (
    id_usuario    INT          NOT NULL AUTO_INCREMENT,
    nome          VARCHAR(100) NOT NULL,
    email         VARCHAR(100) NOT NULL,
    senha_hash    VARCHAR(255) NOT NULL,
    perfil        ENUM('USUARIO', 'ADMIN') NOT NULL DEFAULT 'USUARIO',
    data_cadastro DATE         NOT NULL,
    CONSTRAINT pk_usuarios PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuarios_email UNIQUE (email)
) ENGINE = InnoDB;

-- ----------------------------------------------------------------------------
-- Tabela: categorias
--   tipo_fluxo define se os lancamentos da categoria somam (ENTRADA) ou
--   subtraem (SAIDA) do saldo.
-- ----------------------------------------------------------------------------
CREATE TABLE categorias (
    id_categoria   INT         NOT NULL AUTO_INCREMENT,
    nome_categoria VARCHAR(50) NOT NULL,
    tipo_fluxo     ENUM('ENTRADA', 'SAIDA') NOT NULL,
    CONSTRAINT pk_categorias PRIMARY KEY (id_categoria),
    CONSTRAINT uq_categorias_nome_tipo UNIQUE (nome_categoria, tipo_fluxo)
) ENGINE = InnoDB;

-- ----------------------------------------------------------------------------
-- Tabela: movimentacoes
--   valor e sempre positivo; o sinal vem do tipo da categoria.
--   RESTRICT impede apagar usuario/categoria que ainda possuam lancamentos.
-- ----------------------------------------------------------------------------
CREATE TABLE movimentacoes (
    id_movimentacao INT           NOT NULL AUTO_INCREMENT,
    id_usuario      INT           NOT NULL,
    id_categoria    INT           NOT NULL,
    valor           DECIMAL(10,2) NOT NULL,
    data_operacao   DATE          NOT NULL,
    descricao       VARCHAR(255)  NOT NULL,
    CONSTRAINT pk_movimentacoes PRIMARY KEY (id_movimentacao),
    CONSTRAINT ck_movimentacoes_valor CHECK (valor > 0),
    CONSTRAINT fk_mov_usuario   FOREIGN KEY (id_usuario)   REFERENCES usuarios (id_usuario)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_mov_categoria FOREIGN KEY (id_categoria) REFERENCES categorias (id_categoria)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    INDEX idx_mov_usuario_data (id_usuario, data_operacao),
    INDEX idx_mov_categoria (id_categoria)
) ENGINE = InnoDB;

-- ----------------------------------------------------------------------------
-- Usuario de aplicacao: o sistema NAO usa o root. Este usuario so pode
-- manipular dados (sem permissao para alterar a estrutura do banco).
-- Se alterar a senha, atualize tambem o arquivo zcashflow.properties.
-- ----------------------------------------------------------------------------
CREATE USER IF NOT EXISTS 'zcashflow'@'localhost' IDENTIFIED BY 'zcashflow123';
GRANT SELECT, INSERT, UPDATE, DELETE ON z_cashflow.* TO 'zcashflow'@'localhost';
FLUSH PRIVILEGES;

-- ============================================================================
--  DADOS INICIAIS
-- ============================================================================

-- Categorias padrao (ids 1 a 12)
INSERT INTO categorias (nome_categoria, tipo_fluxo) VALUES
    ('Salário',          'ENTRADA'),  -- 1
    ('Freelance',        'ENTRADA'),  -- 2
    ('Investimentos',    'ENTRADA'),  -- 3
    ('Outros (entrada)', 'ENTRADA'),  -- 4
    ('Moradia',          'SAIDA'),    -- 5
    ('Alimentação',      'SAIDA'),    -- 6
    ('Transporte',       'SAIDA'),    -- 7
    ('Saúde',            'SAIDA'),    -- 8
    ('Lazer',            'SAIDA'),    -- 9
    ('Educação',         'SAIDA'),    -- 10
    ('Contas Fixas',     'SAIDA'),    -- 11
    ('Outros (saída)',   'SAIDA');    -- 12

-- Usuarios de teste (senhas de demonstracao, ja protegidas por hash PBKDF2):
--   gabriel@zmail.com / olaprofessor  (ADMIN)
--   erik@zmail.com    / senha12
--   cida@zmail.com    / senha033
--   atila@zmail.com   / senha3124
--   nilton@zmail.com  / senha21305
INSERT INTO usuarios (nome, email, senha_hash, perfil, data_cadastro) VALUES
    ('Gabriel Teixeira Ricca', 'gabriel@zmail.com', 'pbkdf2$120000$Ru2uQsEh+dUkFYDHOPXWVQ==$bEBAVEBBHcybiAr47k+D3dbeQXag5S8aQD1ZjdJ92hk=', 'ADMIN',   CURDATE()),
    ('Erik Manager',           'erik@zmail.com',    'pbkdf2$120000$nJbEFrx8l9rP75/00zfsmg==$Zju0mKQw8Neyhbkm5eOMy4jD7vfSbfG9gFhRJCXN8uo=', 'USUARIO', CURDATE()),
    ('Cida Colaboradora',      'cida@zmail.com',    'pbkdf2$120000$u+p3ArpZrRnXOUtWhS+dtA==$wLHgX3D0bpZIrN7VjzG5UgXwex2LMR1toyvbIzx0b4M=', 'USUARIO', CURDATE()),
    ('Atila Mentor',           'atila@zmail.com',   'pbkdf2$120000$EQcyPE5yT25uoKNMG28g9g==$1ECvlmUX2pkrVANmDZ3FDcI4o51nLFhCq4SqPvv4AbY=', 'USUARIO', CURDATE()),
    ('Nilton Interim',         'nilton@zmail.com',  'pbkdf2$120000$cduxCeRfUILGsF/1fU8eTw==$9fQCBMKqjd2EiZCvYmQg08FyQrOUKvd8sbVf73a5Th8=', 'USUARIO', CURDATE());

-- Movimentacoes de teste: datas relativas ao dia da execucao, para que o
-- dashboard sempre exiba os ultimos 6 meses com dados.
SET @m0 = DATE_FORMAT(CURDATE(), '%Y-%m-01');   -- primeiro dia do mes atual

INSERT INTO movimentacoes (id_usuario, id_categoria, valor, data_operacao, descricao) VALUES
    -- Gabriel (id 1) - mes atual
    (1, 1, 3500.00, DATE_ADD(@m0, INTERVAL 0 DAY),  'Salário mensal - Estágio AI'),
    (1, 5, 1200.00, DATE_ADD(@m0, INTERVAL 4 DAY),  'Aluguel'),
    (1, 6,  160.00, DATE_ADD(@m0, INTERVAL 4 DAY),  'Almoço executivo + sobremesa'),
    (1, 3,  500.00, DATE_ADD(@m0, INTERVAL 8 DAY),  'Rendimento FIIs'),
    (1, 9,  120.00, DATE_ADD(@m0, INTERVAL 2 DAY),  'Cinema com amigos'),
    -- Gabriel - 1 mes atras
    (1, 1, 3500.00, DATE_SUB(@m0, INTERVAL 1 MONTH),                          'Salário mensal - Estágio AI'),
    (1, 5, 1200.00, DATE_ADD(DATE_SUB(@m0, INTERVAL 1 MONTH), INTERVAL 4 DAY), 'Aluguel'),
    (1, 6,  480.00, DATE_ADD(DATE_SUB(@m0, INTERVAL 1 MONTH), INTERVAL 9 DAY), 'Supermercado do mês'),
    (1, 2,  900.00, DATE_ADD(DATE_SUB(@m0, INTERVAL 1 MONTH), INTERVAL 14 DAY),'Projeto freelance - site'),
    (1, 7,  210.00, DATE_ADD(DATE_SUB(@m0, INTERVAL 1 MONTH), INTERVAL 11 DAY),'Transporte por aplicativo'),
    -- Gabriel - 2 meses atras
    (1, 1, 3500.00, DATE_SUB(@m0, INTERVAL 2 MONTH),                          'Salário mensal - Estágio AI'),
    (1, 5, 1200.00, DATE_ADD(DATE_SUB(@m0, INTERVAL 2 MONTH), INTERVAL 4 DAY), 'Aluguel'),
    (1, 10, 299.90, DATE_ADD(DATE_SUB(@m0, INTERVAL 2 MONTH), INTERVAL 12 DAY),'Curso de especialização Java'),
    (1, 6,  530.00, DATE_ADD(DATE_SUB(@m0, INTERVAL 2 MONTH), INTERVAL 10 DAY),'Supermercado do mês'),
    -- Gabriel - 3 meses atras
    (1, 1, 3500.00, DATE_SUB(@m0, INTERVAL 3 MONTH),                          'Salário mensal - Estágio AI'),
    (1, 5, 1200.00, DATE_ADD(DATE_SUB(@m0, INTERVAL 3 MONTH), INTERVAL 4 DAY), 'Aluguel'),
    (1, 8,  250.00, DATE_ADD(DATE_SUB(@m0, INTERVAL 3 MONTH), INTERVAL 15 DAY),'Consulta médica'),
    (1, 3,  480.00, DATE_ADD(DATE_SUB(@m0, INTERVAL 3 MONTH), INTERVAL 8 DAY), 'Rendimento FIIs'),
    -- Gabriel - 4 meses atras
    (1, 1, 3500.00, DATE_SUB(@m0, INTERVAL 4 MONTH),                          'Salário mensal - Estágio AI'),
    (1, 5, 1200.00, DATE_ADD(DATE_SUB(@m0, INTERVAL 4 MONTH), INTERVAL 4 DAY), 'Aluguel'),
    (1, 9,  340.00, DATE_ADD(DATE_SUB(@m0, INTERVAL 4 MONTH), INTERVAL 18 DAY),'Viagem de fim de semana'),
    -- Gabriel - 5 meses atras
    (1, 1, 3500.00, DATE_SUB(@m0, INTERVAL 5 MONTH),                          'Salário mensal - Estágio AI'),
    (1, 5, 1200.00, DATE_ADD(DATE_SUB(@m0, INTERVAL 5 MONTH), INTERVAL 4 DAY), 'Aluguel'),
    (1, 11, 190.00, DATE_ADD(DATE_SUB(@m0, INTERVAL 5 MONTH), INTERVAL 6 DAY), 'Internet e energia'),
    -- Demais usuarios (cada um enxerga apenas as proprias movimentacoes)
    (2, 3,  500.00, DATE_ADD(@m0, INTERVAL 9 DAY),  'Rendimento FIIs'),
    (2, 1, 4200.00, DATE_ADD(@m0, INTERVAL 0 DAY),  'Salário'),
    (3, 10, 299.90, DATE_ADD(@m0, INTERVAL 11 DAY), 'Curso Especialização Java'),
    (3, 1, 3100.00, DATE_ADD(@m0, INTERVAL 0 DAY),  'Salário'),
    (4, 7,   85.00, DATE_ADD(@m0, INTERVAL 13 DAY), 'Uber para mentoria'),
    (4, 2,  750.00, DATE_ADD(@m0, INTERVAL 3 DAY),  'Consultoria avulsa');
