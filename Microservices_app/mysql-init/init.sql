CREATE DATABASE IF NOT EXISTS bookdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS userdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS cartdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS orderdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS shippingdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 1) Cria o schema eventuate
CREATE DATABASE IF NOT EXISTS eventuate CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE eventuate;

-- 2) Tabela de mensagens / outbox (Eventuate Tram)
CREATE TABLE IF NOT EXISTS message (
  database_id BIGINT NOT NULL AUTO_INCREMENT,
  id VARCHAR(100) NOT NULL,
  destination VARCHAR(255) NOT NULL,
  headers TEXT,
  payload MEDIUMTEXT NOT NULL,
  published BIGINT NOT NULL,
  creation_time BIGINT NOT NULL,
  message_partition INT,
  PRIMARY KEY (database_id),
  UNIQUE KEY (id)
) ENGINE=InnoDB;

DELIMITER $$

-- 1) Dispara antes de inserir cada linha em `message`
CREATE TRIGGER message_before_insert
BEFORE INSERT ON message
FOR EACH ROW
BEGIN
  -- gera o id (string) igual ao database_id auto‐incrementado
  SET NEW.id = CAST(NEW.database_id AS CHAR(100));
  
  -- injeta o header "ID" dentro do JSON de headers
  SET NEW.headers = JSON_SET(
    COALESCE(NEW.headers, '{}'),
    '$.ID',
    NEW.id
  );
END$$

-- 2) Garante que em UPDATE o id e o header permaneçam consistentes
CREATE TRIGGER message_before_update
BEFORE UPDATE ON message
FOR EACH ROW
BEGIN
  -- mantém o id igual ao database_id
  SET NEW.id = CAST(NEW.database_id AS CHAR(100));
  
  -- atualiza o header "ID" no JSON de headers
  SET NEW.headers = JSON_SET(
    COALESCE(NEW.headers, '{}'),
    '$.ID',
    NEW.id
  );
END$$

DELIMITER ;

-- 3) Tabela de monitoring (heartbeat)
CREATE TABLE IF NOT EXISTS cdc_monitoring (
  reader_id VARCHAR(100) NOT NULL PRIMARY KEY,
  last_time BIGINT
) ENGINE=InnoDB;

-- 4) Tabela de offset store
CREATE TABLE IF NOT EXISTS offset_store (
  client_name VARCHAR(255) NOT NULL PRIMARY KEY,
  serialized_offset VARCHAR(255)
) ENGINE=InnoDB;


-- 5) Cria / garante usuário cdc e dá grants
CREATE USER IF NOT EXISTS 'cdc'@'%' IDENTIFIED BY 'admin';

GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'cdc'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON eventuate.* TO 'cdc'@'%';

FLUSH PRIVILEGES;