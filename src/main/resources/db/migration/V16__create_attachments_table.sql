CREATE TABLE IF NOT EXISTS attachments
(
    id           UUID PRIMARY KEY NOT NULL,
    created_by   VARCHAR(100)     NOT NULL,
    updated_by   VARCHAR(100),
    created_date TIMESTAMP        NOT NULL,
    updated_date TIMESTAMP,
    name         VARCHAR(255)     NOT NULL,
    link         VARCHAR(255)     NOT NULL,
    card_id      UUID,
    FOREIGN KEY (card_id) REFERENCES cards (id)
);