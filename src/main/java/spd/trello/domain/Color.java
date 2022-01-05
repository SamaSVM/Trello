package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.perent.Domain;

@Data
public class Color extends Domain {
    private Integer red = 0;
    private Integer green = 0;
    private Integer blue = 0;

    @Override
    public String toString() {
        return "Color{" +
                "id=" + super.getId() +
                ", red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }
}
