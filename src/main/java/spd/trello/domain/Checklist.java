package spd.trello.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "checklists")
public class Checklist extends Resource {
    @Column(name = "name")
    @NotNull(message = "The name field must be filled.")
    @Size(min = 2, max = 20, message = "The name field must be between 2 and 20 characters long.")
    private String name;

    @Column(name = "card_id")
    private UUID cardId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "checklistId", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("checklist")
    @EqualsAndHashCode.Exclude
    private List<CheckableItem> checkableItems = new ArrayList<>();
}
