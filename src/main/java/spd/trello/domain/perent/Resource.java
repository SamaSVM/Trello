package spd.trello.domain.perent;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Resource extends Domain {
    private String createdBy;
    private String updatedBy;
    private final LocalDateTime cratedDate = LocalDateTime.now();
    private LocalDateTime updatedDate;
}
