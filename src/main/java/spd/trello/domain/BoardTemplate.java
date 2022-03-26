package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Resource;

@Data
@EqualsAndHashCode(callSuper=false)
public class BoardTemplate extends Resource {
    private String name;
}
