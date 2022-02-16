package spd.trello.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import java.sql.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "reminders")
public class Reminder extends Resource {
    @Column(name = "start")
    private Date start;

    @Column(name = "\"end\"")
    private Date end;

    @Column(name = "remind_on")
    private Date remindOn;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "card_id")
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
