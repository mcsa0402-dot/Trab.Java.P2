-- ============================================================
-- Sistema de Eventos Comunitários - CEFET-RJ POO P2
-- Script SQL - Criação e população do banco de dados
-- ============================================================

CREATE DATABASE IF NOT EXISTS eventos_comunitarios
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE eventos_comunitarios;

-- ============================================================
-- TABELA: usuario
-- ============================================================
CREATE TABLE IF NOT EXISTS usuario (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(150) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    tipo       ENUM('ORGANIZADOR','VOLUNTARIO','PUBLICO') NOT NULL DEFAULT 'PUBLICO',
    telefone   VARCHAR(30),
    criado_em  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABELA: local_evento
-- ============================================================
CREATE TABLE IF NOT EXISTS local_evento (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(150) NOT NULL,
    endereco   VARCHAR(255),
    capacidade INT NOT NULL,
    descricao  VARCHAR(500),
    criado_em  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABELA: evento
-- ============================================================
CREATE TABLE IF NOT EXISTS evento (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo          VARCHAR(200) NOT NULL,
    descricao       TEXT,
    data_inicio     DATETIME NOT NULL,
    data_fim        DATETIME NOT NULL,
    local_id        BIGINT NOT NULL,
    capacidade      INT NOT NULL,
    categoria       VARCHAR(100),
    organizador_id  BIGINT NOT NULL,
    status          ENUM('ATIVO','CANCELADO','ENCERRADO') NOT NULL DEFAULT 'ATIVO',
    criado_em       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_evento_local        FOREIGN KEY (local_id)       REFERENCES local_evento(id),
    CONSTRAINT fk_evento_organizador  FOREIGN KEY (organizador_id) REFERENCES usuario(id)
);

-- ============================================================
-- TABELA: recurso
-- ============================================================
CREATE TABLE IF NOT EXISTS recurso (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(150) NOT NULL,
    tipo       VARCHAR(100),
    capacidade INT,
    descricao  VARCHAR(300)
);

-- ============================================================
-- TABELA: evento_recurso
-- ============================================================
CREATE TABLE IF NOT EXISTS evento_recurso (
    evento_id   BIGINT NOT NULL,
    recurso_id  BIGINT NOT NULL,
    quantidade  INT NOT NULL DEFAULT 1,
    PRIMARY KEY (evento_id, recurso_id),
    CONSTRAINT fk_er_evento  FOREIGN KEY (evento_id)  REFERENCES evento(id)  ON DELETE CASCADE,
    CONSTRAINT fk_er_recurso FOREIGN KEY (recurso_id) REFERENCES recurso(id) ON DELETE CASCADE
);

-- ============================================================
-- TABELA: inscricao
-- ============================================================
CREATE TABLE IF NOT EXISTS inscricao (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    evento_id        BIGINT NOT NULL,
    usuario_id       BIGINT NOT NULL,
    data_inscricao   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status           ENUM('CONFIRMADA','CANCELADA','LISTA_ESPERA') NOT NULL DEFAULT 'CONFIRMADA',
    observacao       VARCHAR(500),
    CONSTRAINT fk_insc_evento   FOREIGN KEY (evento_id)  REFERENCES evento(id)  ON DELETE CASCADE,
    CONSTRAINT fk_insc_usuario  FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT uq_inscricao     UNIQUE (evento_id, usuario_id)
);

-- ============================================================
-- DADOS INICIAIS - Usuários
-- (senhas em SHA-256: "senha123" = a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3)
-- ============================================================
INSERT INTO usuario (nome, email, senha_hash, tipo, telefone) VALUES
('Admin Sistema',     'admin@eventos.com',    'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'ORGANIZADOR', '(21) 99999-0001'),
('Maria Silva',       'maria@eventos.com',    'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'ORGANIZADOR', '(21) 99999-0002'),
('João Voluntário',   'joao@eventos.com',     'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'VOLUNTARIO',  '(21) 99999-0003'),
('Ana Paula',         'ana@eventos.com',      'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'PUBLICO',     '(21) 99999-0004'),
('Carlos Santos',     'carlos@eventos.com',   'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'PUBLICO',     '(21) 99999-0005'),
('Fernanda Lima',     'fernanda@eventos.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'VOLUNTARIO',  '(21) 99999-0006');

-- ============================================================
-- DADOS INICIAIS - Locais
-- ============================================================
INSERT INTO local_evento (nome, endereco, capacidade, descricao) VALUES
('Centro Comunitário Norte',  'Rua das Flores, 100 - Maria da Graça, RJ', 200, 'Salão principal com ar-condicionado'),
('Praça da Cultura',          'Av. Principal, 500 - Centro, RJ',          500, 'Espaço aberto ao ar livre'),
('Biblioteca Municipal',      'Rua do Saber, 50 - Vila Nova, RJ',          80, 'Auditório climatizado'),
('Escola Estadual Dom Pedro', 'Rua da Educação, 200 - Bonsucesso, RJ',    150, 'Ginásio poliesportivo');

-- ============================================================
-- DADOS INICIAIS - Recursos
-- ============================================================
INSERT INTO recurso (nome, tipo, capacidade, descricao) VALUES
('Projetor Epson',     'AUDIOVISUAL', 1,   'Projetor Full HD com tela retrátil'),
('Sistema de Som',     'AUDIOVISUAL', 1,   'Caixas e microfones sem fio'),
('Cadeiras Extras',    'MOBILIARIO',  100, 'Cadeiras plásticas dobráveis'),
('Mesas de Trabalho',  'MOBILIARIO',  20,  'Mesas 1,20m x 0,60m'),
('Gerador Elétrico',   'INFRAESTRUTURA', 1, 'Gerador 5kVA para eventos externos');

-- ============================================================
-- DADOS INICIAIS - Eventos
-- ============================================================
INSERT INTO evento (titulo, descricao, data_inicio, data_fim, local_id, capacidade, categoria, organizador_id, status) VALUES
('Palestra: Sustentabilidade Urbana',
 'Palestra sobre práticas sustentáveis em comunidades urbanas com especialistas locais.',
 DATE_ADD(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY + INTERVAL 3 HOUR),
 1, 100, 'PALESTRA', 1, 'ATIVO'),

('Oficina de Artesanato Reciclado',
 'Aprenda a criar peças de decoração com materiais recicláveis.',
 DATE_ADD(NOW(), INTERVAL 14 DAY), DATE_ADD(NOW(), INTERVAL 14 DAY + INTERVAL 4 HOUR),
 3, 40, 'OFICINA', 2, 'ATIVO'),

('Feira de Agricultura Familiar',
 'Feira com produtos frescos diretamente de agricultores familiares da região.',
 DATE_ADD(NOW(), INTERVAL 21 DAY), DATE_ADD(NOW(), INTERVAL 21 DAY + INTERVAL 6 HOUR),
 2, 300, 'FEIRA', 1, 'ATIVO');

-- ============================================================
-- DADOS INICIAIS - Inscrições
-- ============================================================
INSERT INTO inscricao (evento_id, usuario_id, status) VALUES
(1, 4, 'CONFIRMADA'),
(1, 5, 'CONFIRMADA'),
(2, 4, 'CONFIRMADA'),
(3, 5, 'CONFIRMADA'),
(3, 6, 'CONFIRMADA');