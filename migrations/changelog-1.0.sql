--liquibase formatted sql

--changeset maxzhukoff:1
CREATE TYPE status AS ENUM ('PENDING', 'IN_PROGRESS', 'COMPLETED');

--changeset maxzhukoff:2
CREATE TYPE priority AS ENUM ('LOW', 'MEDIUM', 'HIGH');

--changeset maxzhukoff:3
CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGSERIAL PRIMARY KEY,
    email    VARCHAR(64)  NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL
);

--changeset maxzhukoff:4
CREATE TABLE IF NOT EXISTS task
(
    task_id     BIGSERIAL PRIMARY KEY,
    title       VARCHAR(128)                      NOT NULL,
    description VARCHAR(255),
    status      status                            NOT NULL DEFAULT 'PENDING',
    priority    priority                          NOT NULL DEFAULT 'MEDIUM',
    author_id   BIGINT REFERENCES users (user_id) NOT NULL,
    executor_id BIGINT REFERENCES users (user_id)
);

--changeset maxzhukoff:5
CREATE TABLE IF NOT EXISTS comment
(
    comment_id BIGSERIAL PRIMARY KEY,
    text       VARCHAR(255)                      NOT NULL,
    task_id    BIGINT REFERENCES task (task_id)  NOT NULL,
    author_id  BIGINT REFERENCES users (user_id) NOT NULL
);