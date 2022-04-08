package spd.trello.unit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.services.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class UnitHelper {
    @Autowired
    private UserService userService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private CardListService cardListService;
    @Autowired
    private CardService cardService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ChecklistService checklistService;
    @Autowired
    private LabelService labelService;
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private CheckableItemService checkableItemService;


    public User getNewUser(String email) {
        User user = new User();
        user.setFirstName("testFirstName");
        user.setLastName("testLastName");
        user.setEmail(email);
        return userService.save(user);
    }

    public Member getNewMember(String email) {
        Member member = new Member();
        User user = getNewUser(email);
        member.setUserId(user.getId());
        member.setCreatedBy(user.getEmail());
        member.setMemberRole(MemberRole.ADMIN);
        return memberService.save(member);
    }

    public Workspace getNewWorkspace(String email) {
        Workspace workspace = new Workspace();
        Member member = getNewMember(email);
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setName("MemberName");
        workspace.setDescription("description");
        workspace.setCreatedBy(member.getId().toString());
        Set<UUID> membersId = new HashSet<>();
        membersId.add(member.getId());
        workspace.setMembersId(membersId);
        return workspaceService.save(workspace);
    }

    public Board getNewBoard(String email) {
        Workspace workspace = getNewWorkspace(email);
        Board board = new Board();
        board.setName("BoardName");
        board.setDescription("description");
        board.setWorkspaceId(workspace.getId());
        board.setCreatedBy(workspace.getCreatedBy());
        Set<UUID> membersId = workspace.getMembersId();
        board.setMembersId(membersId);
        return boardService.save(board);
    }

    public CardList getNewCardList(String email) {
        Board board = getNewBoard(email);
        CardList cardList = new CardList();
        cardList.setBoardId(board.getId());
        cardList.setName("CardListName");
        cardList.setCreatedBy(board.getCreatedBy());
        return cardListService.save(cardList);
    }

    public Card getNewCard(String email) {
        CardList cardList = getNewCardList(email);
        Reminder reminder = getNewReminder(email);
        Card card = new Card();
        card.setReminder(reminder);
        card.setName("CardName");
        card.setDescription("description");
        card.setCardListId(cardList.getId());
        card.setCreatedBy(cardList.getCreatedBy());
        Set<UUID> membersId = boardService.getById(cardList.getBoardId()).getMembersId();
        card.setMembersId(membersId);
        return cardService.save(card);
    }

    public Reminder getNewReminder(String email) {
        Reminder reminder = new Reminder();
        reminder.setCreatedBy(email);
        reminder.setRemindOn(LocalDateTime.of(2022, 2, 2, 2, 2, 2));
        reminder.setStart(LocalDateTime.of(2022, 2, 2, 2, 2, 2));
        reminder.setEnd(LocalDateTime.of(2022, 2, 2, 2, 2, 2));
        reminder.setCreatedDate(LocalDateTime.now());
        return reminder;
    }

    public Comment getNewComment(String email) {
        Card card = getNewCard(email);
        Comment comment = new Comment();
        comment.setText("testComment");
        comment.setCardId(card.getId());
        comment.setCreatedBy(card.getCreatedBy());
        return commentService.save(comment);
    }

    public Checklist getNewChecklist(String email) {
        Card card = getNewCard(email);
        Checklist checklist = new Checklist();
        checklist.setName("testChecklist");
        checklist.setCardId(card.getId());
        checklist.setCreatedBy(card.getCreatedBy());
        return checklistService.save(checklist);
    }

    public CheckableItem getNewCheckableItem(String email) {
        Checklist checklist = getNewChecklist(email);

        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setChecklistId(checklist.getId());
        checkableItem.setName("Name");
        return checkableItemService.save(checkableItem);
    }

    public Label getNewLabel(String email) {
        Card card = getNewCard(email);
        Label label = new Label();
        label.setName("Label");
        label.setCardId(card.getId());
        label.setColor(getNewColor());
        return labelService.save(label);
    }

    public Color getNewColor() {
        Color color = new Color();
        color.setRed(1);
        color.setGreen(2);
        color.setBlue(3);
        return color;
    }

    public Attachment getNewAttachment(String email) {
        Card card = getNewCard(email);
        Attachment attachment = new Attachment();
        attachment.setCardId(card.getId());
        attachment.setLink("link");
        attachment.setName("name");
        attachment.setCreatedBy(card.getCreatedBy());
        return attachmentService.save(attachment);
    }
}
