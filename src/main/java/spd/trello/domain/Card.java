package spd.trello.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private Set<UUID> membersIds = new HashSet<>();

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "reminders",
            joinColumns=@JoinColumn(name= "card_id")
    )
    @Column(name = "id")
    private Set<UUID> reminder = new HashSet<>();

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

    @Override
    public String toString() {
        return "Card{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", archived=" + archived +
                ", listId=" + cardListId +
                '}';
    }
}

