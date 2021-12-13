package spd.trello.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Comment {
    private Member member;
    private String text;
    private LocalDateTime date;
    private List<Attachment> attachments;
}
