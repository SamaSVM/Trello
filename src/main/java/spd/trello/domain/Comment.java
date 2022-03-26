package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "comments")
public class Comment extends Resource {
    @Column(name = "text")
    private String text;

    @Column(name = "card_id")
    private UUID cardId;
}
