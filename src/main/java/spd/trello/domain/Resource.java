package spd.trello.domain;

import java.time.LocalDateTime;

public class Resource extends Domain {
    Number createdBy;
    Number updatedBy;
    final LocalDateTime cratedDate = LocalDateTime.now();
    LocalDateTime updatedDate;
}
