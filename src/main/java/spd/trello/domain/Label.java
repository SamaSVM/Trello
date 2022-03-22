package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import spd.trello.domain.perent.Domain;

import javax.persistence.*;
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "color", referencedColumnName = "id")
    private Color color;
}
