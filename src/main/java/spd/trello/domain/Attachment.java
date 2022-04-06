package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "attachments")
public class Attachment extends Resource {
    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "link")
    private String link;

    @Column(name = "card_id")
    private UUID cardId;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private FileDB fileDB;
}
