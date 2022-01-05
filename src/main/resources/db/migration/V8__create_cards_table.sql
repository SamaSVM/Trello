CREATE TABLE IF NOT EXISTS cards
(
    id           UUID PRIMARY KEY NOT NULL,
    created_by   VARCHAR(100)     NOT NULL,
    updated_by   VARCHAR(100),
    created_date TIMESTAMP        NOT NULL,
    updated_date TIMESTAMP,
    name         VARCHAR(255)     NOT NULL,
    description  VARCHAR(255),
    archived     BOOLEAN          NOT NULL,
    card_list_id UUID             NOT NULL,
    FOREIGN KEY (card_list_id) REFERENCES card_lists (id)
);