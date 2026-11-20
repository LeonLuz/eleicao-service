CREATE TABLE IF NOT EXISTS usuario (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    roles    JSON
);

CREATE TABLE IF NOT EXISTS candidato (
    id_usuario INT          NOT NULL PRIMARY KEY,
    nome       VARCHAR(255) NOT NULL,
    numero     INT          NOT NULL,
    partido    VARCHAR(50)  NOT NULL,
    cargo      VARCHAR(30)  NOT NULL,
    CONSTRAINT fk_candidato_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT uk_candidato_numero_cargo UNIQUE (numero, cargo),
    CONSTRAINT ck_candidato_cargo CHECK (cargo IN ('DEPUTADO_ESTADUAL','DEPUTADO_FEDERAL','SENADOR','GOVERNADOR','PRESIDENTE'))
);
