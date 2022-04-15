package spd.trello.domain.perent;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public class Domain {
    @Id
    private UUID id = UUID.randomUUID();
}
