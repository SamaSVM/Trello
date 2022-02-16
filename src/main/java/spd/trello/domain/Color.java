package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "colors")
public class Color extends Domain {
    @Column(name = "red")
    private Integer red = 0;

    @Column(name = "green")
    private Integer green = 0;

    @Column(name = "blue")
    private Integer blue = 0;

    @Column(name = "label_id")
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
