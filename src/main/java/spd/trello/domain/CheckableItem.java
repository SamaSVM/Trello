package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.perent.Resource;

@Data
public class CheckableItem extends Resource {
    private String name;
    private Boolean checked = false;

    @Override
    public String toString() {
        return "CheckableItem{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCratedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                ", checked=" + checked +
                '}';
    }
}
