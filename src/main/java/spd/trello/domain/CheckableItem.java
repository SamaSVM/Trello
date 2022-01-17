package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Domain;
import spd.trello.domain.perent.Resource;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class CheckableItem extends Domain {
    private String name;
    private Boolean checked = false;
    private UUID checklistId;

    @Override
    public String toString() {
        return "CheckableItem{" +
                "id=" + super.getId() +
                ", name='" + name + '\'' +
                ", checked=" + checked +
                ", checklistId=" + checklistId +
                '}';
    }
}
