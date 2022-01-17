package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Resource;

import java.sql.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class Reminder extends Resource {
    private Date start;
    private Date end;
    private Date remindOn;
    private Boolean active = true;
    private UUID cardId;

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", start=" + start +
                ", end=" + end +
                ", remindOn=" + remindOn +
                ", active=" + active +
                ", cardId=" + cardId +
                '}';
    }
}
