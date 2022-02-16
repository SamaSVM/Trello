package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
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

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "attachments",
            joinColumns=@JoinColumn(name= "comment_id")
    )
    @Column(name = "id")
    private Set<UUID> attachments = new HashSet<>();

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", text='" + text + '\'' +
                ", cardId=" + cardId +
                '}';
    }
}
