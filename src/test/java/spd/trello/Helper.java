package spd.trello;

import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.services.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

import static spd.trello.BaseTest.context;


public class Helper {
    private static final UserService userService = context.getBean(UserService.class);
    private static final MemberService memberService = context.getBean(MemberService.class);
    private static final WorkspaceService workspaceService = context.getBean(WorkspaceService.class);
    private static final BoardService boardService = context.getBean(BoardService.class);
    private static final CardListService cardListService = context.getBean(CardListService.class);
    private static final CardService cardService = context.getBean(CardService.class);
    private static final CommentService commentService = context.getBean(CommentService.class);
    private static final ReminderService reminderService = context.getBean(ReminderService.class);
    private static final ChecklistService checklistService = context.getBean(ChecklistService.class);
    private static final CheckableItemService checkableItemService = context.getBean(CheckableItemService.class);
    private static final LabelService labelService = context.getBean(LabelService.class);


    public static User getNewUser(String email) {
        return userService.create("testFirstName", "testLastName", email);
    }

    public static Member getNewMember(User user) {
        return memberService.create(user, MemberRole.ADMIN);
    }

    public static Workspace getNewWorkspace(Member member) {
        return workspaceService.create(member, "MemberName", "description");
    }

    public static Board getNewBoard(Member member, UUID workspaceId) {
        return boardService.create(member, workspaceId,"BoardName", "description");
    }

    public static CardList getNewCardList(Member member, UUID boardId) {
        return cardListService.create(member, boardId, "CardListName");
    }

    public static Card getNewCard(Member member, UUID cardListId) {
        return cardService.create(member, cardListId, "CardName", "description");
    }

    public static Comment getNewComment(Member member, UUID cardId) {
        return commentService.create(member, cardId, "testComment");
    }

    public static Reminder getNewReminder(Member member, UUID cardId) {
        return reminderService.create(
                member,
                cardId,
                Date.valueOf(LocalDate.of(2222, 1, 1)),
                Date.valueOf(LocalDate.of(2222, 1, 1))
        );
    }

    public static Checklist getNewChecklist(Member member, UUID cardId) {
        return checklistService.create(member, cardId, "testChecklist");
    }

    public static CheckableItem getNewCheckableItem(Member member, UUID checklistId) {
        return checkableItemService.create(member, checklistId, "CheckableItem");
    }

    public static Label getNewLabel(Member member, UUID cardId) {
        return labelService.create(member, cardId, "Label");
    }
}
