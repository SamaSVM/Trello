package spd.trello.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Reminder {
    private LocalDateTime start;
    private LocalDateTime end;
    private LocalDateTime remindOn;
    private Boolean active;
}
