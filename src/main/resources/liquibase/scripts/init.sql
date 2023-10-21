-- liquibase formatted sql

-- changeset kseniaefimchenko:11
CREATE TABLE shelters
(
    id BIGINT PRIMARY KEY ,
    name TEXT,
    info TEXT
)

-- changeset kseniaefimchenko:11.1
CREATE TABLE animals
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,

    CONSTRAINT animal_pk        PRIMARY KEY (id)
);


