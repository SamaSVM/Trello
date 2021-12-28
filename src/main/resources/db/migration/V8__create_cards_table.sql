CREATE TABLE cards
(
    id           UUID PRIMARY KEY NOT NULL,
    created_by   VARCHAR(100)     NOT NULL,
    updated_by   VARCHAR(100)     NOT NULL,
    created_date TIMESTAMP        NOT NULL,
    updated_date TIMESTAMP,
    name         VARCHAR(255)     NOT NULL,
    description  VARCHAR(255),
    archived     BOOLEAN,
    card_list_id     UUID UNIQUE,
    FOREIGN KEY (card_list_id) REFERENCES card_lists (id)
);