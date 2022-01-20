package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Domain;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class Color extends Domain {
    private Integer red = 0;
    private Integer green = 0;
    private Integer blue = 0;
    private UUID labelId;

    @Override
    public String toString() {
        return "Color{" +
                "id=" + super.getId() +
                ", red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                ", labelId=" + labelId +
                '}';
    }
}
