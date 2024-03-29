CREATE TABLE IF NOT EXISTS attachments
(
    id           UUID PRIMARY KEY NOT NULL,
    created_by   VARCHAR(100)     NOT NULL,
    updated_by   VARCHAR(100),
    created_date TIMESTAMP        NOT NULL,
    updated_date TIMESTAMP,
    name         VARCHAR(100),
    type         VARCHAR(100),
    link         VARCHAR(255),
    card_id      UUID             NOT NULL,
    file_id      UUID,
    FOREIGN KEY (card_id) REFERENCES cards (id),
    FOREIGN KEY (file_id) REFERENCES files (id)
);