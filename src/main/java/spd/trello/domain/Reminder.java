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
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCratedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", start=" + start +
                ", end=" + end +
                ", remindOn=" + remindOn +
                ", active=" + active +
                '}';
    }
}
