package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Resource;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class Checklist extends Resource {
    private String name;
    private UUID cardId;

    @Override
    public String toString() {
        return "Checklist{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                ", cardId=" + cardId +
                '}';
    }
}
