package spd.trello.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Board extends Resource {
    private String name;
    private String description;
    private List<CardList> cardLists;
    private List<Member> members = new ArrayList<>();
    private BoardVisibility visibility = BoardVisibility.PRIVATE;
    private Boolean favourite = false;
    private Boolean archived = false;

    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", cratedDate=" + cratedDate +
                ", updatedDate=" + updatedDate +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cardLists=" + cardLists +
                ", members=" + members +
                ", visibility=" + visibility +
                ", favourite=" + favourite +
                ", archived=" + archived +
                '}';
    }
}
