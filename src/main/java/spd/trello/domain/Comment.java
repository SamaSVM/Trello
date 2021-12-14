package spd.trello.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Comment extends Resource {
    private Member member;
    private String text;
    private LocalDateTime date;
    private List<Attachment> attachments;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", cratedDate=" + cratedDate +
                ", updatedDate=" + updatedDate +
                ", member=" + member +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", attachments=" + attachments +
                '}';
    }
}
