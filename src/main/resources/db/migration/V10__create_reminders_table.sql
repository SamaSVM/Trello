CREATE TABLE IF NOT EXISTS reminders
(
    id           UUID PRIMARY KEY NOT NULL,
    created_by   VARCHAR(100)     NOT NULL,
    updated_by   VARCHAR(100),
    created_date TIMESTAMP        NOT NULL,
    updated_date TIMESTAMP,
    start        TIMESTAMP,
    "end"        TIMESTAMP,
    remind_on    TIMESTAMP        NOT NULL,
    active       BOOLEAN          NOT NULL,
    card_id      UUID             /*NOT NULL,
    FOREIGN KEY (card_id) REFERENCES cards (id)*/
);