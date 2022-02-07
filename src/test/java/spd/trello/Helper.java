package spd.trello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.services.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class Helper {
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
    private ReminderService reminderService;
    @Autowired
    private ChecklistService checklistService;
    @Autowired
    private CheckableItemService checkableItemService;
    @Autowired
    private LabelService labelService;


    public User getNewUser(String email) {
        User user = new User();
        user.setFirstName("testFirstName");
        user.setLastName("testLastName");
        user.setEmail(email);
        return userService.create(user);
    }

    public Member getNewMember(User user) {
        Member member = new Member();
        member.setUserId(user.getId());
        member.setCreatedBy(user.getEmail());
        member.setMemberRole(MemberRole.ADMIN);
        return memberService.create(member);
    }

    public Workspace getNewWorkspace(Member member) {
        Workspace workspace = new Workspace();
        workspace.setName("MemberName");
        workspace.setDescription("description");
        workspace.setCreatedBy(member.getId().toString());
        return workspaceService.create(workspace);
    }

    public Board getNewBoard(Member member, UUID workspaceId) {
        Board board = new Board();
        board.setName("BoardName");
        board.setDescription("description");
        board.setWorkspaceId(workspaceId);
        board.setCreatedBy(member.getId().toString());
        return boardService.create(board);
    }

    public CardList getNewCardList(Member member, UUID boardId) {
        CardList cardList = new CardList();
        cardList.setBoardId(boardId);
        cardList.setName("CardListName");
        cardList.setCreatedBy(member.getId().toString());
        return cardListService.create(cardList);
    }

    public Card getNewCard(Member member, UUID cardListId) {
        Card card = new Card();
        card.setName("CardName");
        card.setDescription("description");
        card.setCardListId(cardListId);
        card.setCreatedBy(member.getId().toString());
        return cardService.create(card);
    }

    public Comment getNewComment(Member member, UUID cardId) {
        Comment comment = new Comment();
        comment.setText("testComment");
        comment.setCardId(cardId);
        comment.setCreatedBy(member.getId().toString());
        return commentService.create(comment);
    }

    public Reminder getNewReminder(Member member, UUID cardId) {
        Reminder reminder = new Reminder();
        reminder.setCardId(cardId);
        reminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 1, 1)));
        reminder.setEnd(Date.valueOf(LocalDate.of(2222, 1, 1)));
        reminder.setCreatedBy(member.getId().toString());
        return reminderService.create(reminder);
    }

    public Checklist getNewChecklist(Member member, UUID cardId) {
        Checklist checklist = new Checklist();
        checklist.setName("testChecklist");
        checklist.setCardId(cardId);
        checklist.setCreatedBy(member.getId().toString());
        return checklistService.create(checklist);
    }

    public CheckableItem getNewCheckableItem(UUID checklistId) {
        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setName("Name");
        checkableItem.setChecklistId(checklistId);
        return checkableItemService.create(checkableItem);
    }

    public Label getNewLabel(Member member, UUID cardId) {
        Label label = new Label();
        label.setName("Label");
        label.setCardId(cardId);
        return labelService.create(label);
    }
}
