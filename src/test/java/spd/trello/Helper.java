package spd.trello;

import spd.trello.domain.Board;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.BoardRepository;
import spd.trello.repository.MemberRepository;
import spd.trello.repository.UserRepository;
import spd.trello.repository.WorkspaceRepository;
import spd.trello.services.BoardService;
import spd.trello.services.MemberService;
import spd.trello.services.UserService;
import spd.trello.services.WorkspaceService;

import java.util.UUID;

import static spd.trello.BaseTest.dataSource;

public class Helper {
    private static final UserService userService = new UserService(new UserRepository(dataSource));
    private static final MemberService memberService = new MemberService(new MemberRepository(dataSource));
    private static final WorkspaceService workspaceService = new WorkspaceService(new WorkspaceRepository(dataSource));
    private static final BoardService boardService = new BoardService(new BoardRepository(dataSource));

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
}
