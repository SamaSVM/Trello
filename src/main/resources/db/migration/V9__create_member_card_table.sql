CREATE TABLE IF NOT EXISTS member_card
(
    member_id UUID NOT NULL,
    card_id  UUID NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members (id),
    FOREIGN KEY (card_id) REFERENCES cards (id)
);