CREATE TABLE IF NOT EXISTS member_board
(
    member_id UUID NOT NULL,
    board_id  UUID NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members (id),
    FOREIGN KEY (board_id) REFERENCES boards (id)
);