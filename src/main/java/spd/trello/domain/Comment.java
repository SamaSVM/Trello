package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "comments")
public class Comment extends Resource {
    @Column(name = "text")
    @NotNull(message = "The text field must be filled.")
    @Size(min = 2, max = 1000, message = "The name field must be between 2 and 1000 characters long.")
    private String text;

    @Column(name = "card_id")
    private UUID cardId;
}
