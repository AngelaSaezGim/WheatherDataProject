CREATE SCHEMA IF NOT EXISTS userInfo;
USE userInfo;

CREATE TABLE users (
	dni VARCHAR(9),
    name VARCHAR(100),
    surname VARCHAR(100),
    city VARCHAR(100)
);

GRANT ALL PRIVILEGES ON `userinfo`.* TO 'root'@'localhost';

SELECT * FROM users;

INSERT INTO users ( dni, name, surname, city
) VALUES 
('73665652X','Paco','Saez','Madrid'),
('73665653X','Maria','Gimenez','Valencia'),
('73665654X','Ana','Gonzalez','Barcelona'),
('73665655X','Sara','Garcia','Alicante'),
('73665656X','Pepe','Ramos','Castellón de la Plana')