-- liquibase formatted sql

-- changeset kseniaefimchenko:11
CREATE TABLE shelters
(
    id BIGINT PRIMARY KEY ,
    name TEXT,
    info TEXT
);

-- changeset ldv236:10
CREATE TABLE users
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    chat_id         BIGINT NOT NULL,
    name            TEXT,
    phone           TEXT,
    created_at      TIMESTAMP NOT NULL,
    role            TEXT,

    CONSTRAINT user_pk          PRIMARY KEY (id),
    CONSTRAINT chat_id_unique   UNIQUE (chat_id)
);

-- changeset ldv236:10.2
CREATE TABLE adoptions
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    animal_id       BIGSERIAL NOT NULL,
    user_id         BIGSERIAL NOT NULL,
    adopted_date    DATE NOT NULL,
    trial_end_date  DATE NOT NULL,
    status          TEXT,
    note            TEXT,

    CONSTRAINT adoption_pk          PRIMARY KEY (id),
    CONSTRAINT adoption_animal_fk   FOREIGN KEY (animal_id)     REFERENCES animals(id),
    CONSTRAINT adoption_user_fk     FOREIGN KEY (user_id)       REFERENCES users(id)
);
