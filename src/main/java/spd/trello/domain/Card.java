package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.perent.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Card extends Resource {
    private String name;
    private String description;
    private List<Member> assignedMembers;
    private List<Label> labels;
    private List<Attachment> attachments;
    private Boolean archived = false;
    private List<Comment> comments;
    private Reminder reminder;
    private List<Checklist> checklists;
    private UUID cardListId;

    @Override
    public String toString() {
        return "Card{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", assignedMembers=" + assignedMembers +
                ", labels=" + labels +
                ", attachments=" + attachments +
                ", archived=" + archived +
                ", comments=" + comments +
                ", reminder=" + reminder +
                ", checklists=" + checklists +
                ", listId=" + cardListId +
                '}';
    }
}

