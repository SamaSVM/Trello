package spd.trello.domain;

import lombok.Data;

import java.util.List;

@Data
public class CardList {
    private String name;
    private List<Card> cards;
    private Boolean archived = false;
}
