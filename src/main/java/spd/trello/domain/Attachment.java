package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;
import spd.trello.domain.perent.Resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.File;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "attachments")
public class Attachment extends Resource {
    @Column(name = "name")
    private String name;

    @Column(name = "link")
    private String link;

    @Column(name = "card_id")
    private UUID cardId;
}
