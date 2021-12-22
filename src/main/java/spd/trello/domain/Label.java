package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.perent.Domain;

@Data
public class Label extends Domain {
    private String name;
    private Color color;

    @Override
    public String toString() {
        return "Label{" +
                "id=" + super.getId() +
                ", name='" + name + '\'' +
                ", color=" + color +
                '}';
    }
}
