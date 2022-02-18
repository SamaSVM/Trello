package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import spd.trello.domain.perent.Domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "labels")
public class Label extends Domain {
    @Column(name = "name")
    private String name;

    @Column(name = "card_id")
    private UUID cardId;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "colors",
            joinColumns = @JoinColumn(name = "label_id")
    )
    @Column(name = "id")
    private Set<UUID> color = new HashSet<>();
}
