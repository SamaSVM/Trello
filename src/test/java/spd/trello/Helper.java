package spd.trello;

import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.*;
import spd.trello.services.*;

import java.util.UUID;

import static spd.trello.BaseTest.dataSource;

public class Helper {
    private static final UserService userService = new UserService(new UserRepository(dataSource));
    private static final MemberService memberService = new MemberService(new MemberRepository(dataSource));
    private static final WorkspaceService workspaceService = new WorkspaceService(new WorkspaceRepository(dataSource));
    private static final BoardService boardService = new BoardService(new BoardRepository(dataSource));
    private static final CardListService cardListService = new CardListService(new CardListRepository(dataSource));
    private static final CardService cardService = new CardService(new CardRepository(dataSource));
    private static final CommentService commentService = new CommentService(new CommentRepository(dataSource));

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
}
