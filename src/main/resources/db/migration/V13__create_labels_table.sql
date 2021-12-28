CREATE TABLE labels
(
    id           UUID PRIMARY KEY NOT NULL,
    name         VARCHAR(255)     NOT NULL,
    card_id      UUID UNIQUE,
    FOREIGN KEY (card_id) REFERENCES cards (id)
);