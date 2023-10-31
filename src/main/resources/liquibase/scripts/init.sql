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


