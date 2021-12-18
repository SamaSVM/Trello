package spd.trello.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Resource extends Domain {
    private Number createdBy;
    private Number updatedBy;
    private final LocalDateTime cratedDate = LocalDateTime.now();
    private LocalDateTime updatedDate;
}
