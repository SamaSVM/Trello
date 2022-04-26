package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "checkable_items")
public class CheckableItem extends Domain {
    @Column(name = "name")
    private String name;

    @Column(name = "checked")
    private Boolean checked = false;

    @Column(name = "checklist_id")
    private UUID checklistId;
}
