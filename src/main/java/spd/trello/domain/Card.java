package spd.trello.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Card {
    private String name;
    private String description;
    private List<Member> assignedMembers = new ArrayList<>();
    private List<Label> labels;
    private List<Attachment> attachments;
    private Boolean archived;
    private List<Comment> comments;
    private Reminder reminder;
    private List<Checklist> checklists;
}
