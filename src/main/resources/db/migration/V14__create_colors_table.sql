CREATE TABLE IF NOT EXISTS colors
(
    id       UUID PRIMARY KEY NOT NULL,
    red      INTEGER          NOT NULL,
    green    INTEGER          NOT NULL,
    blue     INTEGER          NOT NULL,
    label_id UUID UNIQUE      /*NOT NULL,
    FOREIGN KEY (label_id) REFERENCES labels (id)*/
);