CREATE TABLE IF NOT EXISTS files
(
    id   UUID PRIMARY KEY NOT NULL,
    name VARCHAR(100)     NOT NULL,
    type VARCHAR(100)     NOT NULL,
    data BIGINT           NOT NULL
);
