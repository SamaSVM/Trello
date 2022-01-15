package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.perent.Resource;

import java.util.UUID;

@Data
public class CardList extends Resource {
    private String name;
    private Boolean archived = false;
    private UUID boardId;

    @Override
    public String toString() {
        return "CardList{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                ", archived=" + archived +
                '}';
    }
}
