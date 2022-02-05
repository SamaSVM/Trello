CREATE TABLE IF NOT EXISTS labels
(
    id      UUID PRIMARY KEY NOT NULL,
    name    VARCHAR(255)     NOT NULL,
    card_id UUID             /*NOT NULL,
    FOREIGN KEY (card_id) REFERENCES cards (id)*/
);