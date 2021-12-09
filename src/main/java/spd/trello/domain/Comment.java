package spd.trello.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Comment {
    private Member member;
    private String text;
    private LocalDateTime date;
    private List<Attachment> attachments;
}
