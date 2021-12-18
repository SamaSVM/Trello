package spd.trello.domain;


import lombok.Getter;

import java.util.UUID;
@Getter
public class Domain {
    private final UUID id = UUID.randomUUID();
}
