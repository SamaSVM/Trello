CREATE TABLE IF NOT EXISTS comments
(
    id           UUID PRIMARY KEY NOT NULL,
    created_by   VARCHAR(100)     NOT NULL,
    updated_by   VARCHAR(100),
    created_date TIMESTAMP        NOT NULL,
    updated_date TIMESTAMP,
    text         VARCHAR(1000)    NOT NULL,
    card_id      UUID            /* NOT NULL,
    FOREIGN KEY (card_id) REFERENCES cards (id)*/
);