CREATE TABLE IF NOT EXISTS checkable_items
(
    id            UUID PRIMARY KEY NOT NULL,
    name          VARCHAR(255)     NOT NULL,
    checked       BOOLEAN          NOT NULL,
    checklist_id UUID             NOT NULL,
    FOREIGN KEY (checklist_id) REFERENCES checklists (id)
);