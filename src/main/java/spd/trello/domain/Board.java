package spd.trello.domain;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private String name;
    private String description;
    private List<CardList> cardLists;
    private List<Member> members = new ArrayList<>();
    private BoardVisibility visibility = BoardVisibility.PRIVATE;
    private Boolean favouriteStatus = false;
    private Boolean isArchive = false;
}
