import java.time.LocalDateTime;
import java.util.List;

public class Card {
    private String name;
    private String description;
    private List<Member> assignedMembers;
    private List<Label> labels;
    private List<Attachment> attachments;
    private boolean isArchived;
    private List<Comment> comments;
    private Reminder reminder;
    private List<Checklist> checklists;
    private LocalDateTime creationDate;
}
