package spd.trello.domain;

import lombok.Data;

import java.util.List;

@Data
public class Checklist {
    private String name;
    private List<CheckableItem> items;
}
