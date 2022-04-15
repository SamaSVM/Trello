package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Domain;
import spd.trello.domain.perent.Resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "checkable_items")
public class CheckableItem extends Domain {
    @Column(name = "name")
    @NotNull(message = "The name field must be filled.")
    @Size(min = 2, max = 20, message = "The name field must be between 2 and 20 characters long.")
    private String name;

    @Column(name = "checked")
    private Boolean checked = false;

    @Column(name = "checklist_id")
    private UUID checklistId;
}
