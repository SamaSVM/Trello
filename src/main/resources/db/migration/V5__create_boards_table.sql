CREATE TABLE IF NOT EXISTS boards
(
    id           UUID PRIMARY KEY NOT NULL,
    created_by   VARCHAR(100)     NOT NULL,
    updated_by   VARCHAR(100),
    created_date TIMESTAMP        NOT NULL,
    updated_date TIMESTAMP,
    name         VARCHAR(255)     NOT NULL,
    description  VARCHAR(255),
    visibility   VARCHAR(25)      NOT NULL,
    favourite    BOOLEAN          NOT NULL,
    archived     BOOLEAN          NOT NULL,
    workspace_id UUID             NOT NULL,
    FOREIGN KEY (workspace_id) REFERENCES workspaces (id)
);