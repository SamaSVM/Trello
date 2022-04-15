package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "cards")
public class Card extends Resource {
    @Column(name = "name")
    @NotNull(message = "The name field must be filled.")
    @Size(min = 2, max = 20, message = "The name field must be between 2 and 20 characters long.")
    private String name;

    @Column(name = "description")
    @Size(min = 2, max = 255, message = "The description field must be between 2 and 255 characters long.")
    private String description;

    @Column(name = "archived")
    private Boolean archived = false;

    @Column(name = "card_list_id")
    private UUID cardListId;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "member_card",
            joinColumns = @JoinColumn(name = "card_id")
    )
    @Column(name = "member_id")
    @EqualsAndHashCode.Exclude
    private Set<UUID> membersId = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "reminder_id", referencedColumnName = "id")
    @EqualsAndHashCode.Exclude
    private Reminder reminder;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "checklists",
            joinColumns = @JoinColumn(name = "card_id")
    )
    @Column(name = "id")
    @EqualsAndHashCode.Exclude
    private Set<UUID> checklists = new HashSet<>();


    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "labels",
            joinColumns = @JoinColumn(name = "card_id")
    )
    @Column(name = "id")
    @EqualsAndHashCode.Exclude
    private Set<UUID> labels = new HashSet<>();

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "comments",
            joinColumns = @JoinColumn(name = "card_id")
    )
    @Column(name = "id")
    @EqualsAndHashCode.Exclude
    private Set<UUID> comments = new HashSet<>();

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "attachments",
            joinColumns = @JoinColumn(name = "card_id")
    )
    @Column(name = "id")
    @EqualsAndHashCode.Exclude
    private Set<UUID> attachments = new HashSet<>();
}

