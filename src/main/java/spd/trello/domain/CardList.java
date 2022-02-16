package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "card_lists")
public class CardList extends Resource {
    @Column(name = "name")
    private String name;

    @Column(name = "archived")
    private Boolean archived = false;

    @Column(name = "board_id")
    private UUID boardId;

    @Override
    public String toString() {
        return "CardList{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                ", archived=" + archived +
                '}';
    }
}
