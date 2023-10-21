-- liquibase formatted sql

-- changeset ldv236:10
CREATE TABLE users
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    chat_id     BIGINT,
    name        TEXT,
    phone       TEXT,
    created_at  TIMESTAMP NOT NULL,
    role        TEXT,

    CONSTRAINT user_pk          PRIMARY KEY (id),
    CONSTRAINT chat_id_unique   UNIQUE (chat_id)
);

-- changeset ldv236:10.1
CREATE TABLE animals
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,

    CONSTRAINT animal_pk        PRIMARY KEY (id)
);

-- changeset ldv236:10.2
CREATE TABLE adoptions
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    animal_id   BIGSERIAL,
    user_id     BIGSERIAL,
    adopted_date DATE not null,
    trial_end_date DATE not null,
    adoption_status TEXT,

    CONSTRAINT adoption_pk      PRIMARY KEY (id),
    CONSTRAINT adoption_animal_fk FOREIGN KEY (animal_id) REFERENCES animals(id),
    CONSTRAINT adoption_user_fk FOREIGN KEY (user_id) REFERENCES users(id)
);