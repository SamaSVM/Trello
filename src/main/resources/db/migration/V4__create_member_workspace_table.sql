CREATE TABLE IF NOT EXISTS member_workspace
(
    member_id    UUID NOT NULL,
    workspace_id UUID NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members (id),
    FOREIGN KEY (workspace_id) REFERENCES workspaces (id),
    UNIQUE (member_id, workspace_id)
);