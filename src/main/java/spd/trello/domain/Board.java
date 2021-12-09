package spd.trello.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Board {
    private String name;
    private String description;
    private List<CardList> cardLists;
    private List<Member> members = new ArrayList<>();
    private BoardVisibility visibility = BoardVisibility.PRIVATE;
    private Boolean favourite = false;
    private Boolean archived = false;
}
