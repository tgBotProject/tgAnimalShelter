-- liquibase formatted sql

-- changeset kseniaefimchenko:11
CREATE TABLE shelters
(
    id BIGINT PRIMARY KEY ,
    name TEXT,
    address_shelter TEXT,
    working_time TEXT,
    driving_directions TEXT,
    security_contact_details TEXT,
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

-- changeset beshik7:9
CREATE TABLE animals
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name          TEXT NOT NULL,
    photo_url     TEXT,
    birthday      DATE,
    species       TEXT NOT NULL CHECK (species IN ('CAT', 'DOG')),
    gender        TEXT NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
    shelter_id    BIGINT,

    CONSTRAINT animal_shelter_fk FOREIGN KEY (shelter_id) REFERENCES shelters(id)
);
-- changeset beshik7:9.1
ALTER TABLE animals
    ALTER COLUMN photo_url TYPE BYTEA USING photo_url::bytea;


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

-- changeset beshik7:9.2
CREATE TABLE reports
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    photo_url       TEXT,
    info            TEXT,
    datetime        TIMESTAMP NOT NULL,
    is_report_valid BOOLEAN NOT NULL DEFAULT FALSE,
    report_valid    BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT report_user_fk FOREIGN KEY (user_id) REFERENCES users(id)
);

-- changeset beshik7:9.3
ALTER TABLE reports
    DROP COLUMN report_valid;

-- changeset beshik7:9.4
ALTER TABLE reports
    ALTER COLUMN is_report_valid DROP DEFAULT,
    ALTER COLUMN is_report_valid DROP NOT NULL,
    ALTER COLUMN is_report_valid SET DEFAULT NULL;

-- changeset beshik7:9.5
ALTER TABLE reports
    ALTER COLUMN photo_url TYPE BYTEA USING photo_url::bytea;

-- changeset heimtn:45
ALTER TABLE reports
    ALTER COLUMN photo_url TYPE TEXT USING photo_url::text;