DROP TABLE IF EXISTS person;

CREATE TABLE person  (
    person_id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    age INTEGER,
    is_active BOOLEAN
);