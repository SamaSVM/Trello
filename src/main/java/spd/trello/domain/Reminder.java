package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import java.sql.Date;

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
}
