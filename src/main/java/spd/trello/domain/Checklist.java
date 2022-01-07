package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.perent.Resource;

import java.util.List;

@Data
public class Checklist extends Resource {
    private String name;
    private List<CheckableItem> items;

    @Override
    public String toString() {
        return "Checklist{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                ", items=" + items +
                '}';
    }
}
