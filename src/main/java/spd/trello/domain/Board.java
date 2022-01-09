package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.enums.BoardVisibility;
import spd.trello.domain.perent.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Board extends Resource {
    private String name;
    private String description;
    private List<CardList> cardLists;
    private List<Member> members = new ArrayList<>();
    private BoardVisibility visibility = BoardVisibility.PRIVATE;
    private Boolean favourite = false;
    private Boolean archived = false;
    private UUID workspaceId;

    @Override
    public String toString() {
        return "Board{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cardLists=" + cardLists +
                ", members=" + members +
                ", visibility=" + visibility +
                ", favourite=" + favourite +
                ", archived=" + archived +
                ", workspaceId=" + workspaceId +
                '}';
    }
}
