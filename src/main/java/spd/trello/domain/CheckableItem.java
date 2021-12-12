package spd.trello.domain;

import lombok.Data;

@Data
public class CheckableItem extends Resource{
    private String name;
    private Boolean checked = false;

    @Override
    public String toString() {
        return "CheckableItem{" +
                "id=" + id +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", cratedDate=" + cratedDate +
                ", updatedDate=" + updatedDate +
                ", name='" + name + '\'' +
                ", checked=" + checked +
                '}';
    }
}
