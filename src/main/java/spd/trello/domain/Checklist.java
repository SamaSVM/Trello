package spd.trello.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "checklists")
public class Checklist extends Resource {
    @Column(name = "name")
    private String name;

    @Column(name = "card_id")
    private UUID cardId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "checklistId", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("checklist")
    private List<CheckableItem> checkableItems = new ArrayList<>();

    @Override
    public String toString() {
        return "Checklist{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                ", cardId=" + cardId +
                '}';
    }
}
