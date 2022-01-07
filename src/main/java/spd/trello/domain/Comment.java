package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.perent.Resource;

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
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", member=" + member +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", attachments=" + attachments +
                '}';
    }
}
