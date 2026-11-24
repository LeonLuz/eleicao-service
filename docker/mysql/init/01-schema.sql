SET NAMES utf8mb4;

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    username VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfis JSON NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE cargo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE partido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    sigla VARCHAR(20) NOT NULL UNIQUE,
    numero INT NOT NULL UNIQUE
);

CREATE TABLE eleicao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(150) NOT NULL,
    data_eleicao DATE NOT NULL,
    turno INT NOT NULL,
    horario_abertura TIME NOT NULL,
    horario_encerramento TIME NOT NULL,
    status ENUM('AGENDADA', 'ABERTA', 'ENCERRADA') NOT NULL DEFAULT 'AGENDADA',
    CONSTRAINT uk_eleicao_data_turno UNIQUE (data_eleicao, turno)
);

CREATE TABLE eleitor (
    id_usuario BIGINT PRIMARY KEY,
    titulo_eleitor VARCHAR(12) NOT NULL UNIQUE,
    zona VARCHAR(3) NOT NULL,
    secao VARCHAR(4) NOT NULL,
    CONSTRAINT fk_eleitor_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE TABLE candidato (
    id_usuario BIGINT PRIMARY KEY,
    numero_eleitoral INT NOT NULL,
    id_partido BIGINT NOT NULL,
    id_cargo BIGINT NOT NULL,
    id_eleicao BIGINT NOT NULL,
    CONSTRAINT uk_candidato_numero_eleicao UNIQUE (numero_eleitoral, id_cargo, id_eleicao),
    CONSTRAINT fk_candidato_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_candidato_partido FOREIGN KEY (id_partido) REFERENCES partido(id),
    CONSTRAINT fk_candidato_cargo FOREIGN KEY (id_cargo) REFERENCES cargo(id),
    CONSTRAINT fk_candidato_eleicao FOREIGN KEY (id_eleicao) REFERENCES eleicao(id)
);

CREATE TABLE controle_voto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_eleicao BIGINT NOT NULL,
    id_eleitor BIGINT NOT NULL,
    id_cargo BIGINT NOT NULL,
    data_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_controle_voto UNIQUE (id_eleicao, id_eleitor, id_cargo),
    CONSTRAINT fk_cv_eleicao FOREIGN KEY (id_eleicao) REFERENCES eleicao(id),
    CONSTRAINT fk_cv_eleitor FOREIGN KEY (id_eleitor) REFERENCES eleitor(id_usuario),
    CONSTRAINT fk_cv_cargo FOREIGN KEY (id_cargo) REFERENCES cargo(id)
);

CREATE TABLE voto (
    id CHAR(36) PRIMARY KEY,
    id_eleicao BIGINT NOT NULL,
    id_cargo BIGINT NOT NULL,
    id_candidato BIGINT NOT NULL,
    CONSTRAINT fk_voto_eleicao FOREIGN KEY (id_eleicao) REFERENCES eleicao(id),
    CONSTRAINT fk_voto_cargo FOREIGN KEY (id_cargo) REFERENCES cargo(id),
    CONSTRAINT fk_voto_candidato FOREIGN KEY (id_candidato) REFERENCES candidato(id_usuario)
);

INSERT INTO cargo (nome) VALUES
('Deputado Estadual'),
('Deputado Federal'),
('Senador'),
('Governador'),
('Presidente da República');