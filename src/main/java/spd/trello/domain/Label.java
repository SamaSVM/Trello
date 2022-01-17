package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.perent.Domain;

import java.util.UUID;

@Data
public class Label extends Domain {
    private String name;
    private UUID colorId;
    private UUID cardId;

    @Override
    public String toString() {
        return "Label{" +
                "id=" + super.getId() +
                ", name='" + name + '\'' +
                ", colorId=" + colorId +
                ", cardId=" + cardId +
                '}';
    }
}
