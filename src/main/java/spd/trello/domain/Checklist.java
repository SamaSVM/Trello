package spd.trello.domain;

import lombok.Data;

import java.util.List;

@Data
public class Checklist extends Resource{
    private String name;
    private List<CheckableItem> items;

    @Override
    public String toString() {
        return "Checklist{" +
                "id=" + id +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", cratedDate=" + cratedDate +
                ", updatedDate=" + updatedDate +
                ", name='" + name + '\'' +
                ", items=" + items +
                '}';
    }
}
