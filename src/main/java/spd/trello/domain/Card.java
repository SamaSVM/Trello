package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import java.util.*;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "cards")
public class Card extends Resource {
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "archived")
    private Boolean archived = false;

    @Column(name = "card_list_id")
    private UUID cardListId;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "member_card",
            joinColumns=@JoinColumn(name= "card_id")
    )
    @Column(name = "member_id")
    private Set<UUID> membersId = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "reminder_id", referencedColumnName = "id")
    private Reminder reminder;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "checklists",
            joinColumns=@JoinColumn(name= "card_id")
    )
    @Column(name = "id")
    private Set<UUID> checklists = new HashSet<>();


    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "labels",
            joinColumns=@JoinColumn(name= "card_id")
    )
    @Column(name = "id")
    private Set<UUID> labels = new HashSet<>();

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "comments",
            joinColumns=@JoinColumn(name= "card_id")
    )
    @Column(name = "id")
    private Set<UUID> comments = new HashSet<>();

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "attachments",
            joinColumns=@JoinColumn(name= "card_id")
    )
    @Column(name = "id")
    private Set<UUID> attachments = new HashSet<>();
}

