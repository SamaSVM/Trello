package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.perent.Resource;

import java.util.UUID;

@Data
public class Card extends Resource {
    private String name;
    private String description;
    private Boolean archived = false;
    private Reminder reminder;
    private UUID cardListId;

    @Override
    public String toString() {
        return "Card{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", archived=" + archived +
                ", reminder=" + reminder +
                ", listId=" + cardListId +
                '}';
    }
}

