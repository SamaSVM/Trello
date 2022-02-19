package spd.trello.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Checklist checklist = (Checklist) o;
        return Objects.equals(name, checklist.name) && Objects.equals(cardId, checklist.cardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cardId);
    }
}
