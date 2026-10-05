DROP TABLE IF EXISTS book;

CREATE TABLE book (
    id     BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    title  VARCHAR(100),
    author VARCHAR(100),
    pages  INTEGER,
    price  DECIMAL(8, 2)
);
