CREATE TABLE IF NOT EXISTS "users"
(
    id         UUID PRIMARY KEY NOT NULL,
    first_name VARCHAR(100)     NOT NULL,
    last_name  VARCHAR(100)     NOT NULL,
    email      VARCHAR(100)     NOT NULL UNIQUE,
    time_zone  VARCHAR(100),
    password   VARCHAR(100)     NOT NULL,
    role       VARCHAR(25)      NOT NULL,
    status     VARCHAR
);