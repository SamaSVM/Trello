package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Resource;

@Data
@EqualsAndHashCode(callSuper=false)
public class BoardTemplate extends Resource {
    private String name;

    @Override
    public String toString() {
        return "BoardTemplate{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                '}';
    }
}
