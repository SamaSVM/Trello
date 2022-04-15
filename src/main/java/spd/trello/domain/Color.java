package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "colors")
public class Color extends Domain {
    @Column(name = "red")
    @Size(min = 0, max = 255, message = "The red color should be in the range 0 to 255.")
    private Integer red = 0;

    @Column(name = "green")
    @Size(min = 0, max = 255, message = "The green color should be in the range 0 to 255.")
    private Integer green = 0;

    @Column(name = "blue")
    @Size(min = 0, max = 255, message = "The blue color should be in the range 0 to 255.")
    private Integer blue = 0;
}
