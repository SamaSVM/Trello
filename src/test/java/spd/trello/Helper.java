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
        return workspaceService.create(member, workspace);
    }

    public Board getNewBoard(Member member, UUID workspaceId) {
        return boardService.create(member, workspaceId, "BoardName", "description");
    }

    public CardList getNewCardList(Member member, UUID boardId) {
        return cardListService.create(member, boardId, "CardListName");
    }

    public Card getNewCard(Member member, UUID cardListId) {
        return cardService.create(member, cardListId, "CardName", "description");
    }

    public Comment getNewComment(Member member, UUID cardId) {
        return commentService.create(member, cardId, "testComment");
    }

    public Reminder getNewReminder(Member member, UUID cardId) {
        return reminderService.create(
                member,
                cardId,
                Date.valueOf(LocalDate.of(2222, 1, 1)),
                Date.valueOf(LocalDate.of(2222, 1, 1))
        );
    }

    public Checklist getNewChecklist(Member member, UUID cardId) {
        return checklistService.create(member, cardId, "testChecklist");
    }

    public CheckableItem getNewCheckableItem(Member member, UUID checklistId) {
        return checkableItemService.create(member, checklistId, "CheckableItem");
    }

    public Label getNewLabel(Member member, UUID cardId) {
        return labelService.create(member, cardId, "Label");
    }
}
