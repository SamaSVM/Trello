package spd.trello.domain;

import java.time.LocalDateTime;

public class Resource {
    private Number createdBy;
    private Number updatedBy;
    private final LocalDateTime cratedDate = LocalDateTime.now();
    private LocalDateTime updatedDate;
}
