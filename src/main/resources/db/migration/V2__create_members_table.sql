CREATE TABLE members
(
    id           UUID PRIMARY KEY NOT NULL,
    user_id      UUID             NOT NULL UNIQUE,
    created_by   VARCHAR(100)     NOT NULL,
    updated_by   VARCHAR(100)     NOT NULL,
    created_date TIMESTAMP        NOT NULL,
    updated_date TIMESTAMP,
    member_role  VARCHAR(25)      NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);