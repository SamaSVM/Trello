package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.perent.Resource;

import java.util.UUID;

@Data
public class Comment extends Resource {
    private String text;
    private UUID cardId;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", text='" + text + '\'' +
                ", cardId=" + cardId +
                '}';
    }
}
