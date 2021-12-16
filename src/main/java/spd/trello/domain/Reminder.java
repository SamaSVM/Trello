package spd.trello.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Reminder extends Resource {
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime remindOn;
    private Boolean active = false;

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", cratedDate=" + cratedDate +
                ", updatedDate=" + updatedDate +
                ", start=" + start +
                ", end=" + end +
                ", remindOn=" + remindOn +
                ", active=" + active +
                '}';
    }
}
